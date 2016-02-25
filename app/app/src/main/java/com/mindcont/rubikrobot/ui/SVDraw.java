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


/*定义一个画矩形框的类*/
public class SVDraw extends SurfaceView implements SurfaceHolder.Callback {

    protected SurfaceHolder sh;
    Canvas canvas;
    Paint paint = new Paint();
    private int mWidth;
    private int mHeight;
    private int[] test = {3, 3, 2, 5, 4, 3, 5, 3, 2};

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

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int w, int h) {
        // TODO Auto-generated method stub
        mWidth = w;
        mHeight = h;
//        drawLine();
//        Draw9Rect(test,40,40,40,40,10);
    }

    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        Draw9Rect(test, 120, 120, 120, 120, 10);
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

    public void DrawRect(int width, int height, int start_x, int start_y, int colorNum) {

        //canvas = sh.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT);
        switch (colorNum) {
            case 0:
                paint.setColor(Color.rgb(255, 255, 165));//橙色
                break;
            case 1:
                paint.setColor(Color.BLUE);
                break;
            case 2:
                paint.setColor(Color.RED);
                break;
            case 3:
                paint.setColor(Color.GREEN);
                break;
            case 4:
                paint.setColor(Color.WHITE);
                break;
            case 5:
                paint.setColor(Color.YELLOW);
                break;

        }
        //paint.setColor(color); //设置画笔颜色
        paint.setStrokeWidth((float) 2.0);              //设置线宽
        canvas.drawLine(start_x, start_y, start_x + width, start_y, paint);        //绘制直线
        canvas.drawLine(start_x, start_y, start_x, start_y + height, paint);        //绘制直线
        canvas.drawLine(start_x + width, start_y, start_x + width, start_y + height, paint);        //绘制直线
        canvas.drawLine(start_x, start_y + height, start_x + width, start_y + height, paint);        //绘制直线
        //sh.unlockCanvasAndPost(canvas);
    }

    public void Draw9Rect(int[] color,int mHeight, int mWidth, int startx, int starty,int margin){
        canvas = sh.lockCanvas();
        int i,m,color_flag = 0;
        for(i=0;i<3;i++) {
            for(m=0;m<3;m++){
                DrawRect(mWidth,mHeight,startx+m*margin+m*mWidth,starty+i*margin+i*mHeight,color[color_flag++]);
            }

        }
//        DrawRect(40,40,40, 90,color[i]);
        sh.unlockCanvasAndPost(canvas);
    }

}
