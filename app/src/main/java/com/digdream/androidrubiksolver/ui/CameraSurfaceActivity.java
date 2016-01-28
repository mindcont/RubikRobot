package com.digdream.androidrubiksolver.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.android.RubiksCubeGLSurfaceView;
import com.digdream.androidrubiksolver.camera.Guides;
import com.digdream.androidrubiksolver.camera.Preview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by user on 3/14/15.
 */
public class CameraSurfaceActivity extends Activity {

    private static final String TAG = "CameraSurfaceActivity";
    private Camera mCamera;
    private Activity act;
    private Context ctx;
    private Preview mPreview;
    private RubiksCubeGLSurfaceView glView;
    private TextView mColor;
    String face;
    Guides mGuides;
    int mPreviewHeight, mPreviewWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        glView = new RubiksCubeGLSurfaceView(this);
        setContentView(R.layout.activity_camera_surface);
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.rubikPreview);
        linearLayout.addView(glView);

        ctx = this;
        act = this;

        mPreview = new Preview(this, (SurfaceView) findViewById(R.id.surface_camera));

        mPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mPreviewWidth = mPreview.getWidth();
                        mPreviewHeight = mPreview.getHeight();
                        mPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        mPreview.setLayoutParams(new LayoutParams(mPreviewHeight, mPreviewWidth));
        ((FrameLayout) findViewById(R.id.layout_camera)).addView(mPreview);
        face = getIntent().getStringExtra("face");
        mGuides = new Guides(this, face);
        ((RelativeLayout) findViewById(R.id.guides)).addView(mGuides);
        mGuides.getLayoutParams().width = LayoutParams.FILL_PARENT;
        mGuides.getLayoutParams().height = LayoutParams.FILL_PARENT;
        mPreview.setKeepScreenOn(true);

        mPreview.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        mColor = (TextView) findViewById(R.id.tv_camera_color);
        mColor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });

        Toast.makeText(ctx, getString(R.string.take_photo_help), Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                mCamera = Camera.open(0);
                mCamera.startPreview();
                mPreview.setCamera(mCamera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, getString(R.string.camera_not_found), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        mCamera.startPreview();
        mPreview.setCamera(mCamera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            //写入 SD 卡中
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/rubiksolver");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }
}
