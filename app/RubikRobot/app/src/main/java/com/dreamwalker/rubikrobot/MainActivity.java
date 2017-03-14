/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreamwalker.rubikrobot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.rubikrobot.bluetooth.BluetoothChatService;
import com.dreamwalker.rubikrobot.bluetooth.DeviceListActivity;
import com.dreamwalker.rubikrobot.camera.ColorRecognition;
import com.dreamwalker.rubikrobot.solver.RubikRobot;
import com.dreamwalker.rubikrobot.ui.AboutActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * This is the main Activity that displays the current chat session.
 */

@SuppressLint("NewApi")
public class MainActivity extends Activity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean Debug = true;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static int camera_timer = 0;

    // Receive blubooth data
    String readMessage;
    Bundle bundle = null;
    int lineWidth = 100;
    //ColorRecognition
    ColorRecognition colorRecognition = new ColorRecognition();
    private Camera mCamera;
    private SurfaceView cameraSurface;
    private SurfaceView locateSurface;
    //预览界面和硬件平台的标定位置以及线粗
    private int locate_x = 100;
    private int locate_y = 100;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (Debug) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
//                    mTitle.setText(R.string.title_connected_to);
//                    mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
//                    mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
//                    mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    break;
                case MESSAGE_READ: {
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    readMessage = new String(readBuf, 0, msg.arg1);
                    Toast.makeText(MainActivity.this, "蓝牙接收到字符串" + readMessage, Toast.LENGTH_SHORT).show();

                    try{
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean b, Camera camera) {
                                mCamera.takePicture(null, null, new MyPictureCallback());
                            }
                        });

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    
                    //Toast.makeText(MainActivity.this, "蓝牙接收到字符串" + readMessage, Toast.LENGTH_SHORT).show();
                    //sendMessages(search.solution(colorRecognition.Get_RubikTotalColor(), 21, 100, 0, 0));
                }
                break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        //sendMessage(message);
                    }
                    if (Debug) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    public static int getPreviewDegree(Activity activity) {

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        if (Debug) Log.i(TAG, "Camera rotation " + rotation);

        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 180;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 0;
                break;
        }
        return degree;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Debug) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.activity_main);
        Button takePicture = (Button) findViewById(R.id.button);
        takePicture.setOnClickListener(mOnClickListener);

        cameraSurface = (SurfaceView) findViewById(R.id.surfaceView1);
        cameraSurface.getHolder().setKeepScreenOn(true);
        cameraSurface.getHolder().setFormat(PixelFormat.TRANSPARENT);
        cameraSurface.getHolder().addCallback(new SurfaceCallback());
        cameraSurface.setSecure(true);

        locateSurface = (SurfaceView) findViewById(R.id.surfaceView2);
        locateSurface.getHolder().addCallback(new SurfaceCallback());
        locateSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        locateSurface.setZOrderMediaOverlay(true);

        // 获取屏幕分辨率尺寸
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

        int screenWidth = wm.getDefaultDisplay().getWidth();
        int screenHeight = wm.getDefaultDisplay().getHeight();

        //计算预览界面和硬件平台的标定位置中心，这里屏幕中心
         locate_x = screenWidth/2;
         locate_y = screenHeight/2;

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

//              mCamera.autoFocus(Camera.Parameters.FOCUS_MODE_AUTO);
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    mCamera.takePicture(null, null, new MyPictureCallback());
                }
            });

            }
        };


    @Override
    public void onStart() {
        super.onStart();
        if (Debug) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (Debug) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.

        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);
        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (Debug) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Debug) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if (Debug) Log.e(TAG, "-- ON DESTROY --");
    }


    private void ensureDiscoverable() {
        if (Debug) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessages(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

        }
    }

    /**
     * 蓝牙回调 上一个activity 结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Debug) Log.d(TAG, "-- onActivityResult --" + resultCode);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "-- BT not enabled --");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    /**
     * 重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * 重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bluetooth:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;

//            case R.id.menu_preview:
//                startActivity(new Intent(this, CubePreview.class));
//                return true;

            case R.id.menu_about:

                // Ensure this device is discoverable by others
                //ensureDiscoverable();

                //在此处编写about_activity
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    }

    /**
     * 保存当前图片到sd卡
     *
     * @param data
     * @throws IOException
     */
    public void saveToSDCard(byte[] data) throws IOException {

        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/RubikRobot/");
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, String.valueOf(++camera_timer) + ".jpg");

//        File jpgFile = new File("/mnt/extsd0/RubikRobot/", String.valueOf(++camera_timer) + ".jpg");
//        File jpgFile = new File(Environment.getExternalStorageDirectory() + "/RubikRobot/", String.valueOf(++camera_timer) + ".jpg");
        FileOutputStream outputStream = new FileOutputStream(jpgFile);

        outputStream.write(data);
        outputStream.close();
    }

    /**
     * Returns an ordinal value for the SurfaceHolder, or -1 for an invalid surface.
     */
    private int getSurfaceId(SurfaceHolder holder) {
        if (holder.equals(cameraSurface.getHolder())) {
            return 1;
        } else if (holder.equals(locateSurface.getHolder())) {
            return 2;
        } else {
            return -1;
        }
    }

    /**
     * 业务逻辑
     * 拍照完 ，识别颜色，计算复原步骤，控制蓝牙发送
     */
    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap bMap;

            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data);

                bMap = BitmapFactory.decodeByteArray(data,0,data.length);
                Bitmap bMapRotate;

                Matrix matrix = new Matrix();
                matrix.reset();
                matrix.postRotate(90);
                bMapRotate = Bitmap.createBitmap(bMap, 0, 0, bMap.getWidth(),
                        bMap.getHeight(), matrix, true);
                bMap = bMapRotate;

                //保存图片
                try {

                    File fileFolder = new File(Environment.getExternalStorageDirectory()
                        + "/RubikRobot/");
                    if (!fileFolder.exists()) {
                        fileFolder.mkdir();
                    }

                    // Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    File jpgFile = new File(fileFolder, String.valueOf(++camera_timer) + ".jpg");
                    BufferedOutputStream bos =
                            new BufferedOutputStream(new FileOutputStream(jpgFile));

                    bMap.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中

                    bos.flush();//输出
                    bos.close();//关闭

                }catch (Exception e){
                    e.printStackTrace();
                }

//                saveToSDCard(data);
                // Toast.makeText(getApplicationContext(), R.string.success,Toast.LENGTH_SHORT).show();

                camera.startPreview();
                if (camera_timer == 6) {
                    camera_timer = 0;

                    sendMessages(readMessage);
//                    startActivity(new Intent(MainActivity.this, CubePreview.class));
                    String result = RubikRobot.getSolution(colorRecognition.getRubikTotalColor());
                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();

                    char flag_error = result.charAt(1);

                    if (flag_error != 'e') {
                        sendMessages(result);
                    } else {
                        Toast.makeText(MainActivity.this, RubikRobot.getSolution(colorRecognition.getRubikTotalColor()), Toast.LENGTH_LONG).show();
                    }

                } else {
                    sendMessages(readMessage);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class SurfaceCallback implements SurfaceHolder.Callback {

        public void surfaceCreated(SurfaceHolder holder) {
            int id = getSurfaceId(holder);

            if (id < 0) {
                Log.w(TAG, "surfaceCreated UNKNOWN holder=" + holder);
            } else {
                Log.d(TAG, "surfaceCreated #" + id + " holder=" + holder);
            }

            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(holder);
//                mCamera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
//                mCamera.startPreview();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            //设置参数并开始预览
            Log.d("surfaceChanged", "--------------surfaceChanged------------------");

            int id = getSurfaceId(holder);
            Surface surface = holder.getSurface();

            switch (id) {
                case 1:
                    // default layer: circle on left / top
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPictureFormat(PixelFormat.JPEG);

//                    if (getWindowManager().getDefaultDisplay().getOrientation() == 0)
//                    {//坚
                        Log.d("0", "setPreviewSize:"+height+"*"+width);
                        parameters.setPreviewSize(height, width);
                        mCamera.setDisplayOrientation(90);
//                    }else {  //横
//                        Log.d("1", "setPreviewSize:"+width+"*"+height);
//                        parameters.setPreviewSize(width, height);
//                        mCamera.setDisplayOrientation(0);
//                    }
//                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    parameters.setJpegQuality(90);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();

                    break;
                case 2:
                    // media overlay layer: circle on right / bottom
                    tryDrawing(holder);
                    break;
                default:
                    throw new RuntimeException("wha?");
            }
        };

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // ignore
            Log.d(TAG, "Surface destroyed holder=" + holder);

            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }

        private void tryDrawing(SurfaceHolder holder) {
            Log.i(TAG, "Trying to draw...");

            Canvas canvas = holder.lockCanvas();
            if (canvas == null) {
                Log.e(TAG, "Cannot draw onto the canvas as it's null");
            } else {

                Log.i(TAG, "Drawing...");

                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
//                paint.setShadowLayer(50 /2 + 1, 0, 0, Color.RED);
//                canvas.drawCircle(screenWidth/2,screenHeight/2, 50, paint);
                paint.setStrokeWidth(5); //画笔粗细
                canvas.drawLine(locate_x - lineWidth / 2, locate_y, locate_x + lineWidth / 2, locate_y, paint);
                canvas.drawLine(locate_x, locate_y - lineWidth / 2, locate_x, locate_y + lineWidth / 2, paint);
                holder.unlockCanvasAndPost(canvas);

            }
        }
    }
}