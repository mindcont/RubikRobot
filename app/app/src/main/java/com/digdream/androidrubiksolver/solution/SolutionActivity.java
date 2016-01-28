package com.digdream.androidrubiksolver.solution;

import java.util.ArrayList;

import org.opencv.core.Rect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.digdream.androidrubiksolver.R;
import com.digdream.androidrubiksolver.cube.ColorConverter;
import com.digdream.androidrubiksolver.cube.Facelet;
import com.digdream.androidrubiksolver.cube.LayoutCalculator;
import com.digdream.androidrubiksolver.cube.RubiksCube;
import com.digdream.androidrubiksolver.ui.CameraActivity;
import com.digdream.androidrubiksolver.bluetooth.BTSerialComm;
public class SolutionActivity extends Activity {

	private RubiksCube cube;
	private String solution;
	private String[] solutions;
	private SolutionDisplay solver;
	private int currentStep;
	private int totalSteps;
	private int width;
	private int height;
	private ArrayList<Rect> rectangles;
	private Canvas canvas;
	private TextView solutionTitle;
	private TextView solutionText;
	private Facelet[] currentFacelets;
	private ImageView imageView;

	private RotateAnimation rotate90cw;
	private RotateAnimation rotate180;
	private RotateAnimation rotate90ccw;


	private ArrayList<RectF> rects;

	public SolutionActivity() {
		rects = new ArrayList<RectF>();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_solution);

		Intent intent = getIntent();

		cube = new RubiksCube();
		cube.setFaceletColors(intent.getIntArrayExtra(CameraActivity.CUBE));
		solver = new SolutionDisplay(cube);

		// define animations
		rotate90cw = new RotateAnimation(0, 90,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate90cw.setRepeatCount(-1);
		rotate90cw.setDuration(3000);

		rotate180 = new RotateAnimation(0, 180,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate180.setRepeatCount(-1);
		rotate180.setDuration(6000);

		rotate90ccw = new RotateAnimation(0, -90,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotate90ccw.setRepeatCount(-1);
		rotate90ccw.setDuration(3000);

		solution = intent.getStringExtra(CameraActivity.SOLUTION);
		width = intent.getIntExtra(CameraActivity.WIDTH, 0);
		height = intent.getIntExtra(CameraActivity.HEIGHT, 0);

		//设置显示解决方案时的图标显示
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.getLayoutParams().width = height;

		solutionTitle = (TextView) findViewById(R.id.solutionTitle);
		solutionText = (TextView) findViewById(R.id.solutionText);

		// create canvas
		int h = (int) Math.sqrt((2*height*height));
		Bitmap bitmap = Bitmap.createBitmap(h, h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		imageView.setImageBitmap(bitmap);


		// FIXME: correct calculation of previous rotation
		ImageButton prevBtn = (ImageButton) findViewById(R.id.solPrevBtn);
		//		prevBtn.setOnClickListener(new View.OnClickListener() {
		//			@Override
		//			public void onClick(View v) {
		//				if (currentStep > 0) {
		//					currentStep--;
		//					drawSolutionStep();
		//				}
		//			}
		//		});

		ImageButton forwardBtn = (ImageButton) findViewById(R.id.solFwdBtn);
		forwardBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (currentStep < solutions.length - 1) {
					currentStep++;
					drawSolutionStep();
				}
			}
		});


		// Rectangles
		LayoutCalculator lc = new LayoutCalculator(width, height);
		rectangles = lc.calculateRectanglesCoordinates();

		// precalculate android.graphics.RectF
		for (Rect rect : rectangles) {
			rects.add(new RectF((float) rect.tl().x+(h-height)/2, (float) rect.tl().y+(h-height)/2,
					(float) rect.br().x+(h-height)/2, (float) rect.br().y+(h-height)/2));
		}

		// split solution string
		solutions = solution.split(" ");
		totalSteps = solutions.length;
		currentStep = 0;

		// present 1st solution step
		drawSolutionStep();
	}


	private void drawSolutionStep() {
		currentFacelets = solver.getNextStep(solutions[currentStep]);
		drawCurrentFace();

		solutionTitle.setText(getString(R.string.solutionStepTitle, currentStep + 1, totalSteps));
		solutionText.setText(Html.fromHtml(createSolutionText()));

		// set animation
		switch (solutions[currentStep].charAt(1)) {
		case '1':
			imageView.setAnimation(rotate90cw);
			break;
		case '2':
			imageView.setAnimation(rotate180);
			break;
		case '3':
			imageView.setAnimation(rotate90ccw);
			break;
		default:
			imageView.setAnimation(null);
		}

	}

	private String createSolutionText() {
		int rotationIndex = Character.getNumericValue(solutions[currentStep].charAt(1)) - 1;
		String rotationText = getResources().getStringArray(R.array.rotationTexts)[rotationIndex];

		// concatenate solution steps into single string, highlighting current step
        // 连接解决方案的步骤为单个字符串，突出当前步骤
		StringBuilder solution = new StringBuilder();
		for (int i = 0; i < solutions.length; i++) {
			if (currentStep == i) {
				solution.append("<b><i>" + solutions[i] + "</i></b>");// <b> 标签规定粗体文本 <i> 标签显示斜体文本效果。
			} else {
				solution.append(solutions[i]);
			}
			solution.append(" | ");//空格
		}

		// create concrete solution advice, e.g. "Rotate b 90° clockwise"
        // 创建具体的解决方案，例如 “旋转 b(后) 90° 时针方向(顺/逆)

        //原始代码
//		solution.append("<br><br>");//换行
//		solution.append(getString(R.string.rotate) + " ");
//		solution.append(translateRotationToString(solutions[currentStep].charAt(0)));
//		solution.append(" " + rotationText);//空格

        /*下位机设定的开始标志位为'#',结束标志位为‘！’字母均为大写字母，
        不涉及到同时旋转两层或者旋转整个魔方的情况。
        格式示例：#UB2R'L2!*/

		solution.append("<br><br>");//换行两次

        solution.append("#");//下位机通信协议 开始标志位#
		solution.append(getString(R.string.rotate));
		solution.append(translateRotationToString(solutions[currentStep].charAt(0)));
		solution.append(rotationText);//空格
        solution.append("!");//下位机通信协议 结束标志位！

		return solution.toString();
//		SendData();
	}

	private String translateRotationToString(char rotation) {
		int sideIndex = 0;
		switch (rotation) {
		case 'f':
			sideIndex = 0;
			break;
		case 'r':
			sideIndex = 1;
			break;
		case 'b':
			sideIndex = 2;
			break;
		case 'l':
			sideIndex = 3;
			break;
		case 'd':
			sideIndex = 4;
			break;
		case 'u':
			sideIndex = 5;
			break;
		default:
			sideIndex = 0;
		}
		return getResources().getStringArray(R.array.rotationSide)[sideIndex];
	}

	// show cube faces
	private void drawCurrentFace() {
		for (int i=0; i < rectangles.size(); i++) {
			Paint paint = new Paint();
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			paint.setStrokeWidth(5);
			paint.setColor(ColorConverter.getAndroidColor(currentFacelets[i].getColor()));
			canvas.drawRect(rects.get(i), paint);
		}

	}

	// show rotation arrow

}
