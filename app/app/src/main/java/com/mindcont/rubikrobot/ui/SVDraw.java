package com.mindcont.rubikrobot.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mindcont.rubikrobot.camera.ColorRecognition;

import java.util.Timer;
import java.util.TimerTask;

//ColorRecognition


/*定义一个画矩形框的类*/
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

    protected SurfaceHolder sh;
    Canvas canvas;
    Paint paint = new Paint();
    ColorRecognition colorRecognition = new ColorRecognition();
    private int mWidth;
    private int mHeight;
    private Timer timer;
    private TimerTask task;
    private String[] currentSurfaceColor = colorRecognition.surface1_color;

    //    test[9]= {3,3,2,5,4,3,5,3,2};
    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public SVDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        sh = getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.TRANSPARENT);
        setZOrderOnTop(true);
    }

    /**
     * 启动定时器后台线程
     */
    public void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                //在定时器线程中调用绘图方法
                Draw9Rect(currentSurfaceColor, 180, 180, 100, 100, 10);
            }
        };
        //设置定时器每隔0.01秒启动这个task,实现动画效果
        timer.schedule(task, 10, 10);
    }

    /**
     * 停止定时器线程的方法
     */
    public void stopTimer() {
        timer.cancel();
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        // TODO Auto-generated method stub
        mWidth = w;
        mHeight = h;
//        drawLine();
//        Draw9Rect(test,40,40,40,40,10);
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

//        Draw9Rect(test, 180, 180, 100, 100, 10);
        startTimer();
//        canvas = sh.lockCanvas();
//        DrawRect(40,40,40, 40,3);
//        DrawRect(40,40,40, 90,2);
//        sh.unlockCanvasAndPost(canvas);

    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
    }

    void clearDraw() {
        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.BLUE);
        sh.unlockCanvasAndPost(canvas);
    }

    public void drawLine() {
        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.RED);
        p.setStrokeWidth((float) 10.0);              //设置线宽
        p.setStyle(Paint.Style.STROKE);
        //canvas.drawPoint(100.0f, 100.0f, p);
        canvas.drawLine(0, 110, 500, 110, p);
        canvas.drawCircle(110, 110, 10.0f, p);
        sh.unlockCanvasAndPost(canvas);

    }

    public void DrawRect(int width, int height, int start_x, int start_y, String color) {

        //canvas = sh.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);

        for (int i = 0; i < 9; i++) {
            switch (color.charAt(i)) {
                case 'U':
                    paint.setColor(Color.WHITE);
                    break;
                case 'R':
                    paint.setColor(Color.RED);
                    break;
                case 'F':
                    paint.setColor(Color.GREEN);
                    break;
                case 'D':
                    paint.setColor(Color.YELLOW);
                    break;
                case 'L':
                    paint.setColor(Color.rgb(255, 255, 165));//橙色
                    break;
                case 'B':
                    paint.setColor(Color.BLUE);
                    break;
            }
        }
        //paint.setColor(color); //设置画笔颜色
        paint.setStrokeWidth((float) 10.0);              //设置线宽
        canvas.drawLine(start_x, start_y, start_x + width, start_y, paint);        //绘制直线
        canvas.drawLine(start_x, start_y, start_x, start_y + height, paint);        //绘制直线
        canvas.drawLine(start_x + width, start_y, start_x + width, start_y + height, paint);        //绘制直线
        canvas.drawLine(start_x, start_y + height, start_x + width, start_y + height, paint);        //绘制直线
        //sh.unlockCanvasAndPost(canvas);
    }

    public void Draw9Rect(String[] colors, int mHeight, int mWidth, int startx, int starty, int margin) {
        canvas = sh.lockCanvas();
        int i, m, color_flag = 0;
        for (i = 0; i < 3; i++) {
            for (m = 0; m < 3; m++) {
                DrawRect(mWidth, mHeight, startx + m * margin + m * mWidth, starty + i * margin + i * mHeight, colors[color_flag++]);
            }
        }
//        DrawRect(40,40,40, 90,color[i]);
        sh.unlockCanvasAndPost(canvas);
    }

}
