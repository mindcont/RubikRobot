package com.digdream.androidrubiksolver.input;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.view.MotionEvent;

public interface FaceInputMethod {
	public void init(ArrayList<Rect> rectangles, ArrayList<Integer> face);
	public void drawOverlay(Mat frame);
	public boolean onTouchEvent(MotionEvent event);
	public String getInstructionText(Integer faceId);
	public String getInstructionTitle(Integer faceId);
	public void changeFace(Integer faceId, ArrayList<Integer> face);
	public boolean currentFaceHasUnsetFacelets();
	public void startDetectingColors();
}
