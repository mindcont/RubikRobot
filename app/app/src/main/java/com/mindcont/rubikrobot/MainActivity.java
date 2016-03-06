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

package com.mindcont.rubikrobot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.mindcont.rubikrobot.bluetooth.BluetoothChatService;
import com.mindcont.rubikrobot.bluetooth.DeviceListActivity;
import com.mindcont.rubikrobot.camera.ColorRecognition;
import com.mindcont.rubikrobot.solver.RubikRobot;
import com.mindcont.rubikrobot.ui.AboutActivity;
import com.mindcont.rubikrobot.ui.CubePreview;

import java.io.ByteArrayOutputStream;
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
    private static final boolean D = true;
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static int camera_timer = 0;
    // Receive blubooth data
    String readMessage;
    Bundle bundle = null;
    //ColorRecognition
    ColorRecognition colorRecognition = new ColorRecognition();
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    // Take Photos
    private Camera camera;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
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
                    camera.takePicture(null, null, new MyPictureCallback());
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
    // The Handler that gets information back from the BluetoothChatService
    private Camera.Parameters parameters = null;
    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
            new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    // If the action is a key-up event on the return key, send the message
                    if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                        String message = view.getText().toString();
                        //sendMessage(message);
                    }
                    if (D) Log.i(TAG, "END onEditorAction");
                    return true;
                }
            };

    public static int getPreviewDegree(Activity activity) {

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = (SurfaceView) this
                .findViewById(R.id.surfaceView);
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }
    public void onPreviewFrame(byte[] data, Camera camera) {

        int imageWidth = camera.getParameters().getPreviewSize().width  ;
        int imageHeight =camera.getParameters().getPreviewSize().height ;
//        int RGBData[] = new int[imageWidth* imageHeight];
//        byte[] mYUVData = new byte[data.length];
//        System.arraycopy(data, 0, mYUVData, 0, data.length);
//        RGBData= decodeYUV420SP( mYUVData, imageWidth, imageHeight);
//
//        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
//        bitmap.setPixels(RGBData, 0, imageWidth, 0, 0, imageWidth, imageHeight);
//        int getcolor=bitmap.getPixel(10,10);
//        String color=String.valueOf(getcolor);

        YuvImage yuvImage= new YuvImage(data, ImageFormat.NV21,imageWidth,imageHeight,null);
        ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0,0,imageWidth,imageHeight),80,byteArrayOutputStream);
        byte [] rgbData = byteArrayOutputStream.toByteArray();


        BitmapFactory.Options bitmapFactoryOption =new BitmapFactory.Options();
        bitmapFactoryOption.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bmp =BitmapFactory.decodeByteArray(rgbData,0,rgbData.length,bitmapFactoryOption);

        int getcolor=bmp.getPixel(10, 10);
        String color=String.valueOf(getcolor);

        int R = ((getcolor & 0xff0000) >> 16);
        int G = ((getcolor & 0xff00) >> 8);
        int B = ((getcolor & 0xff));

        String Rcolor=String.valueOf(R);
        String Gcolor=String.valueOf(G);
        String Bcolor=String.valueOf(B);

        String screenWidth = String.valueOf(imageWidth);
        String screenHidth = String.valueOf(imageHeight);

        Log.d(TAG,"+++ the R color part is  "+Rcolor);
        Log.d(TAG,"+++ the G color part is  "+Gcolor);
        Log.d(TAG,"+++ the B color part is   "+Bcolor);

        Log.d(TAG,"+++ the screenWidth is   "+screenWidth);
        Log.d(TAG,"+++ the screenHidth is   "+screenHidth);



        Toast.makeText(this,Rcolor,Toast.LENGTH_LONG).show();
        Toast.makeText(this,Gcolor,Toast.LENGTH_LONG).show();
        Toast.makeText(this,Bcolor,Toast.LENGTH_LONG).show();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

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
        if (D) Log.e(TAG, "+ ON RESUME +");

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
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
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
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //重写onCreateOptionMenu(Menu menu)方法，当菜单第一次被加载时调用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //填充选项菜单（读取XML文件、解析、加载到Menu组件上）
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    //重写OptionsItemSelected(MenuItem item)来响应菜单项(MenuItem)的点击事件（根据id来区分是哪个item）
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
     *
     */
    public void saveToSDCard(byte[] data) throws IOException {

//        Date date = new Date();
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//        String filename = format.format(date) + ".jpg";
//        File fileFolder = new File(Environment.getExternalStorageDirectory()
//                + "/finger/");
//        if (!fileFolder.exists()) {
//            fileFolder.mkdir();
//        }
//         File jpgFile = new File(fileFolder, filename);

        File jpgFile = new File("/mnt/extsd0/", String.valueOf(++camera_timer) + ".jpg");
//        File jpgFile = new File(Environment.getExternalStorageDirectory(), String.valueOf(++camera_timer) + ".jpg");
        FileOutputStream outputStream = new FileOutputStream(jpgFile);

        outputStream.write(data);
        outputStream.close();
    }

    /**
     *
     */
    private final class MyPictureCallback implements Camera.PictureCallback {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                bundle = new Bundle();
                bundle.putByteArray("bytes", data);
                saveToSDCard(data);
                // Toast.makeText(getApplicationContext(), R.string.success,Toast.LENGTH_SHORT).show();

                camera.startPreview();
                if (camera_timer == 6) {
                    camera_timer = 0;

                    sendMessages(readMessage);
                    startActivity(new Intent(MainActivity.this, CubePreview.class));
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

    private final class SurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            parameters = camera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPreviewSize(width, height);
            parameters.setPreviewFrameRate(5);
            parameters.setPictureSize(width, height);
            parameters.setJpegQuality(80);

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(holder);
                // camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
                camera.startPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
    }

}