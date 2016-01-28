package com.digdream.androidrubiksolver.ui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.cube.LayoutCalculator;
import com.digdream.androidrubiksolver.cube.Rotator;
import com.digdream.androidrubiksolver.cube.RubiksCube;
import com.digdream.androidrubiksolver.input.FaceInputMethod;
import com.digdream.androidrubiksolver.input.ManualFaceInputMethod;
import com.digdream.androidrubiksolver.solution.SolutionActivity;
import com.digdream.androidrubiksolver.solver.Search;

public class CameraActivity extends Activity implements CvCameraViewListener2, Observer {

	private static final String TAG = "ARCS::MainActivity";
	private static final String STATE_FACELETS = "cubeFacelets";

	private static final Scalar BGCOLOR = new Scalar(70, 70, 70);

	public static final String CUBE ="com.github.sgelb.arcs.CUBE";
	public static final String SOLUTION ="com.github.sgelb.arcs.SOLUTION";
	public static final String WIDTH ="com.github.sgelb.arcs.WIDTH";
	public static final String HEIGHT ="com.github.sgelb.arcs.HEIGHT";

	private Mat frame;
	private CameraBridgeViewBase mOpenCvCameraView;
	private FaceInputMethod faceInputMethod = null;
	private SharedPreferences prefs;

	private int width;
	private int height;
	private static Context context;
	private TextView instructionContent;
	private ImageButton forwardBtn;
	private ImageButton backBtn;
	private ImageButton cameraBtn;

	private RubiksCube cube;
	private Integer currentFace;
	private CubeSolver cubeSolver;
	private int padding;
	private int xOffset;

	public CameraActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
		faceInputMethod = new ManualFaceInputMethod(this);
	}

	/* Called when activity is first created. 首次创建活动时调用。*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        //取消顶部标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		CameraActivity.context = getApplicationContext();
		setContentView(R.layout.activity_camera);

		cube = new RubiksCube();
		// Check if we're recreating a previously destroyed instance
		//检查我们是否重新创建了先前破坏的实例
		if (savedInstanceState != null) {
			// Restore cube from saved state
			//从保存状态恢复魔方
			int[] colors = savedInstanceState.getIntArray(STATE_FACELETS);
			cube.setFaceletColors(colors);
			Log.d(TAG, "Restored state");
		}
		currentFace = Rotator.FRONT;

		instructionContent = (TextView) findViewById(R.id.instructionContentText);
		instructionContent.setText(faceInputMethod.getInstructionText(0));

		forwardBtn = (ImageButton) findViewById(R.id.forwardBtn);
		disableButton(forwardBtn);
		forwardBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentFace == 5 && !cube.hasUnsetFacelets()) {
					solveCubeAction();
				} else {
					forwardBtnAction();
				}
			}
		});

		backBtn = (ImageButton) findViewById(R.id.backBtn);
		disableButton(backBtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				previousBtnAction();
			}
		});

		cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
		cameraBtn.setOnClickListener(
                new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cameraBtnAction();
			}
		});

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		setCubeInputMethod();

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.java_camera_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		// Use back camera
		//使用后置摄像头
		mOpenCvCameraView.setCameraIndex(0);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the current cube state
		//保存当前魔方状态
		savedInstanceState.putIntArray(STATE_FACELETS, cube.getFaceletColors());
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null) {
			mOpenCvCameraView.disableView();
		}
		if (cubeSolver != null && !cubeSolver.isCancelled()) {
			cubeSolver.cancel(true);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		//通过opencv引擎服务加载并初始化opencv类库，所谓opencv引擎服务
		//但是运行时要求安装opencv manager，所以放弃这个方法，采取其他加载方法

		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
		//mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);//直接设置为连接成功
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null) {
			mOpenCvCameraView.disableView();
		}
		if (cubeSolver != null && !cubeSolver.isCancelled()) {
			cubeSolver.cancel(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return faceInputMethod.onTouchEvent(event);
	}

	public static Context getContext() {
		return CameraActivity.context;
	}

	private void setCubeInputMethod() {
		switch (prefs.getString("cube_input_method", "manual")) {
		case "manual":
			faceInputMethod = new ManualFaceInputMethod(this);
			break;
		default:
			faceInputMethod = new ManualFaceInputMethod(this);
		}
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		this.width = width;
		this.height = height;
		LayoutCalculator lc = new LayoutCalculator(width, height);
		faceInputMethod.init(lc.calculateRectanglesCoordinates(), cube.getFaceColor(currentFace));
		xOffset = lc.getXOffset();
		padding = lc.getPadding();
		positionViews();
	}


	@Override
	public void onCameraViewStopped() {
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		frame = inputFrame.rgba();
		faceInputMethod.drawOverlay(frame);
		drawViewBackground();
		return frame;
	}

	private void drawViewBackground() {
		// draw solid rect as background for text/button on right side of layout
		//画实心矩形为布局文本/按钮在右边的背景
		Core.rectangle(frame, new Point(xOffset - padding/2, 0), new Point(width, height), BGCOLOR, -1);
	}

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS:
			{
				Log.i(TAG, "OpenCV loaded successfully");
				mOpenCvCameraView.enableView();
			} break;
			default:
			{
				super.onManagerConnected(status);
			} break;
			}
		}
	};


	private void resetFaceView() {
		// reset after creating random cube or cleared cube
		//复位后 创建随机魔方或清除魔方
		currentFace = Rotator.FRONT;
		faceInputMethod.changeFace(currentFace, cube.getFaceColor(currentFace));
		instructionContent.setText(faceInputMethod.getInstructionText(currentFace));
		forwardBtn.setImageResource(R.drawable.ic_action_forward);
		if (cube.hasUnsetFacelets()) {
			disableButton(forwardBtn);
		} else {
			enableButton(forwardBtn);
		}
		disableButton(backBtn);
	}

	private void positionViews() {
		// Position views according to calculated size of rectangles
		//根据矩形的计算值调整位置视图
		LinearLayout layout = (LinearLayout) findViewById(R.id.linearView);
		layout.setPadding(xOffset, padding, padding, padding);

		instructionContent.setMaxWidth(width - padding - xOffset);
		instructionContent.setMinWidth(width - padding - xOffset);
	}

	private void forwardBtnAction() {
		currentFace++;
		instructionContent.setText(faceInputMethod.getInstructionText(currentFace));
		ArrayList<Integer> face = cube.getFaceColor(currentFace);
		faceInputMethod.changeFace(currentFace, face);
		enableButton(backBtn);

		if (face == null || faceInputMethod.currentFaceHasUnsetFacelets()) {
			disableButton(forwardBtn);
		} else {
			enableButton(forwardBtn);
		}

		if (currentFace == 5) {
			forwardBtn.setImageResource(R.mipmap.ic_launcher);
			if (cube.hasUnsetFacelets()) {
				disableButton(forwardBtn);
			}
		}
	}

	private void previousBtnAction() {
		currentFace--;
		instructionContent.setText(faceInputMethod.getInstructionText(currentFace));
		faceInputMethod.changeFace(currentFace, cube.getFaceColor(currentFace));
		forwardBtn.setImageResource(R.drawable.ic_action_forward);
		enableButton(forwardBtn);
		if (currentFace == 0) {
			disableButton(backBtn);
		}
	}

	private void cameraBtnAction() {
		faceInputMethod.startDetectingColors();
	}

	private void solveCubeAction() {
		// Solve
		cubeSolver= new CubeSolver();
		cubeSolver.execute(cube.getSingmasterNotation());
	}

	@Override
	public void update(Observable observable, Object data) {
		if (observable instanceof ManualFaceInputMethod) {
			// got face
			if (data instanceof ArrayList<?>) {
				// checked cast for type safety
				ArrayList<Integer> face = new ArrayList<Integer>();
				for (int i=0; i<((ArrayList<?>) data).size(); i++) {
					Object item = ((ArrayList<?>) data).get(i);
					if (item instanceof Integer) {
						face.add((Integer) item);
					}
				}
				// set face
				cube.setFaceColor(currentFace, face);
				enableButton(forwardBtn);
			}
		}
	}

	private void disableButton(ImageButton button) {
		button.setEnabled(false);
		//button.setImageAlpha(100);
		button.getBackground().setAlpha(100);
	}

	private void enableButton(ImageButton button) {
		button.setEnabled(true);
		//button.setImageAlpha(255);
		button.getBackground().setAlpha(255);
	}

	private class CubeSolver extends AsyncTask<String, Integer, String> {
		long startTime;

		@Override
		protected void onPreExecute() {
			disableButton(forwardBtn);
			startTime = System.currentTimeMillis();
		}

		@Override
		protected String doInBackground(String... singmasterNotation) {
			Search search = new Search();
			String solution = search.solution(singmasterNotation[0], 21, 10000, 0, 0x0);
			return solution;
		}

		@Override
		protected void onPostExecute(String result) {
			enableButton(forwardBtn);
			long runTime = System.currentTimeMillis() - startTime;
			Log.d(TAG, "Found solution in " + runTime + "ms :" + result);
			processResult(result);
		}
	}

	private void processResult(String solution) {

		// error handling
		if (solution.startsWith("error")) {
			int errorcode = Character.getNumericValue(solution.charAt(solution.length() - 1)) - 1;
			solution = getResources().getStringArray(R.array.errors)[errorcode];
			Toast.makeText(this, solution, Toast.LENGTH_LONG).show();
			return;
		}

		// solution is empty aka cube already solved
		if (solution.isEmpty()) {
			Toast.makeText(this, getString(R.string.cube_is_solved), Toast.LENGTH_LONG).show();
			return;
		}

        Toast.makeText(this, solution, Toast.LENGTH_LONG).show();

        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(solution);
        // got valid solution, start SolutionActivity
//		Intent solutionIntent = new Intent(this, SolutionActivity.class);
//		solutionIntent.putExtra(CUBE, cube.getFaceletColors());
//		solutionIntent.putExtra(SOLUTION, solution.toLowerCase());
//		solutionIntent.putExtra(WIDTH, width);
//		solutionIntent.putExtra(HEIGHT, height);
//		startActivity(solutionIntent);
	}


}
