package com.digdream.androidrubiksolver.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class RubiksCubeGLSurfaceView extends GLSurfaceView {
    private static final float TOUCH_SCALE_FACTOR = 180.0f / 320.0f;
    private static final int MIN_ZOOM = -80;
    private static final int MAX_ZOOM = -10;

    private RubiksCubeGLRenderer renderer;
    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;
    private float previousX = 0;
    private float previousY = 0;

    public RubiksCubeGLSurfaceView(Context context) {
        super(context);

        int size = 3;
        renderer = new RubiksCubeGLRenderer(size);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        this.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        this.setRenderer(renderer);
        this.requestFocus();
        this.setFocusableInTouchMode(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        float currentX = event.getX();
        float currentY = event.getY();
        float deltaX, deltaY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                deltaX = currentX - previousX;
                deltaY = currentY - previousY;
                if (!scaleDetector.isInProgress()) {
                    renderer.rotateCameraX(deltaY * TOUCH_SCALE_FACTOR);
                    renderer.rotateCameraY(deltaX * TOUCH_SCALE_FACTOR);
                    invalidate();
                }
        }

        previousX = currentX;
        previousY = currentY;

        return true;
    }

    public RubiksCubeGLRenderer getRenderer() {
        return renderer;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= 1 / detector.getScaleFactor();
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
            renderer.setZoom(scaleFactor);
            invalidate();
            return true;
        }
    }
}