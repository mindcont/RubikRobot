package com.mindcont.rubikrobot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.mindcont.rubikrobot.bluetooth.BluetoothChat;
import com.mindcont.rubikrobot.bluetooth.BluetoothChatService;
import com.mindcont.rubikrobot.bluetooth.DeviceListActivity;
import com.mindcont.rubikrobot.ui.AboutActivity;
import com.mindcont.rubikrobot.ui.CubePreview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the activity_main Activity that displays the current chat session.
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity {

    // Debugging
    private static final String TAG = "MainActivity";
    private static final boolean Debug = true;

    // Message types sent from the BluetoothChatService Handler
    //消息类型有from the bluetoothchatservice处理
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    // 从bluetoothchatservice处理接收的关键字
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Receive blubooth data
    String readMessage;
    Bundle bundle = null;
    BluetoothChat mBluetooth = new BluetoothChat();

    // Take Photos
    private Camera camera;
    private Camera.Parameters parameters = null;

//    private SVDraw  mSVDraw = null;
//    private Canvas canvas;
//    private Paint paint ;//依靠此类开始画线




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Debug) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.activity_main);

//        mSVDraw =(com.example.android.BluetoothChat.SVDraw)findViewById(R.id.mDraw);
//        addContentView(mSVDraw, new ViewGroup.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        SurfaceView surfaceView = (SurfaceView) this
                .findViewById(R.id.surfaceView);
        surfaceView.getHolder()
                .setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setFixedSize(176, 144);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceCallback());

        // Get local Bluetooth adapter
        mBluetooth.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetooth.mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Debug) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!BluetoothChat.mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, mBluetooth.REQUEST_ENABLE_BT);
            mBluetooth.setupChat();
            // Otherwise, setup the chat session
        } else {
            if (BluetoothChat.mChatService == null)
                mBluetooth.setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (Debug) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mBluetooth.mChatService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetooth.mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mBluetooth.mChatService.start();
            }
        }
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
        if (mBluetooth.mChatService != null) mBluetooth.mChatService.stop();
        if (Debug) Log.e(TAG, "--- ON DESTROY ---");
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
                Intent serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, mBluetooth.REQUEST_CONNECT_DEVICE);
                return true;

            case R.id.menu_preview:
                startActivity(new Intent(MainActivity.this, CubePreview.class));
                return true;
            case R.id.menu_about:
                // Ensure this device is discoverable by others
                //ensureDiscoverable();

                //在此处编写about_activity
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
        }
        return false;
    }

    /**
     *
     */
    public void saveToSDCard(byte[] data) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(Environment.getExternalStorageDirectory()
                + "/finger/");
        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile);
        outputStream.write(data);
        outputStream.close();

        FileInputStream fis = new FileInputStream("/sdcard/finger/" + filename);
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        /*** 旋转图片  **/
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream("/sdcard/finger/" + filename);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            // 如果图片还没有回收，强制回收
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        } catch (Exception e) {
        }

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
                Toast.makeText(getApplicationContext(), R.string.success, Toast.LENGTH_SHORT).show();
                mBluetooth.sendMessage(readMessage);
                camera.startPreview();


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
                camera.setDisplayOrientation(getPreviewDegree(MainActivity.this));
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
}
