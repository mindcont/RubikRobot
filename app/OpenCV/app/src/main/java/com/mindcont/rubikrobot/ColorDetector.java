package com.mindcont.rubikrobot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.mindcont.rubikrobot.util.kmeans.Kmeans;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.mindcont.rubikrobot.util.BitmapLoadUtil.decodeFixedSizeForFile;

/**
 * Created by fenxi on 2017/2/28.
 */

public class ColorDetector extends Activity {

    private static final String TAG = "New ColorDetector ";

    // 这里的路径适合 armpc 430 板卡的存储卡，不适合手机安卓，
// 建议使用 Environment.getExternalStorageDirectory() 来获取外部存储卡根路径
//  private static String filepath = "/mnt/extsd0/RubikRobot/";
    private static String filetype = ".jpg";
    double[] centerPoint, maxClusterDistancePoint;
    Kmeans mkmeans = new Kmeans();
    private int imgId = 0;
    private String filePath;
    private ImageView mImageView;
    private Button up_button, down_button, detector_button;
    private AutoCompleteTextView resultText;
    private Bitmap rawBitmap;
    //    public ColorBlobDetector    mDetector;
    //    回调函数，用于初始化opencv库
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    try {
                        Log.i(TAG, "OpenCV loaded successfully");
//                        detectEdges(bmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private Button.OnClickListener up_buttonListener = new
            Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //控制Image View显示上一张照片
//                rawBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf((--filename > 0) ? filename : 0) + filetype);
//                mImageView.setImageBitmap(rawBitmap);

                    //将保存在本地的图片取出并缩小后显示在界面上

                    if (imgId > 0) {
                        imgId = imgId - 1;
                    } else {
                        imgId = 0;
                        Toast.makeText(ColorDetector.this, "已经是第一张了", Toast.LENGTH_LONG).show();
                    }

                    filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf(imgId) + filetype;
                    rawBitmap = decodeFixedSizeForFile(filePath, 1);

                    // 确定裁剪的位置和裁剪的大小
                    rawBitmap = Bitmap.createBitmap(rawBitmap,
                            250, 150,
                            700, 700);

                    mImageView.setImageBitmap(rawBitmap);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//                    rawBitmap.recycle();
                }
            };
    private Button.OnClickListener down_buttonListener = new
            Button.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //控制Image View显示下一张照片
//                rawBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf((++filename < 6) ? filename : 6) + filetype);
//                mImageView.setImageBitmap(rawBitmap);

                    if (imgId < 6) {
                        imgId = imgId + 1;
                    } else {
                        imgId = 6;
                        Toast.makeText(ColorDetector.this, "已经是最后一张了", Toast.LENGTH_LONG).show();
                    }

                    filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf(imgId) + filetype;
                    rawBitmap = decodeFixedSizeForFile(filePath, 1);

                    // 确定裁剪的位置和裁剪的大小
                    rawBitmap = Bitmap.createBitmap(rawBitmap,
                            250, 150,
                            700, 700);
                    mImageView.setImageBitmap(rawBitmap);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//                    rawBitmap.recycle();

                }
            };
    private Button.OnClickListener detector_buttonListener = new Button.OnClickListener() {

        @Override

        public void onClick(View v) {

            //识别当前显示的图片
//          mImageView.setImageBitmap(detectEdges(rawBitmap));
//            mImageView.setImageBitmap(detectEdges(rawBitmap));
            //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
//            rawBitmap.recycle();


            //直接检测角点，运算量太大，无反应
            mImageView.setImageBitmap(detectCorner(rawBitmap));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_detector);
//        SurfaceView surfaceView = (SurfaceView) this
//                .findViewById(R.id.surfaceView);

        mImageView = (ImageView) findViewById(R.id.mimageView);
        up_button = (Button) findViewById(R.id.up_button);
        down_button = (Button) findViewById(R.id.down_button);
        detector_button = (Button) findViewById(R.id.detector_button);
        resultText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);

        /**
         * 依次读取6张图片，识别并显示
         * 扫描顺序 上(白) 右（绿） 前（黄）下（橘红） 左（红） 后（蓝）
         */


//        rawBitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf(filename) + filetype);
        filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf(imgId) + filetype;
        rawBitmap = decodeFixedSizeForFile(filePath, 1);

        // 确定裁剪的位置和裁剪的大小,这里讲对拍摄到的图片进行剪裁
        // 因为魔方在图像中的位置大体不变，可以通过预先剪裁的方式屏蔽干扰，
        // 不同分辨率的手机需要手动修改这个参数
        rawBitmap = Bitmap.createBitmap(rawBitmap,
                250, 150,
                700, 700);

        mImageView.setImageBitmap(rawBitmap);

        //设置上一张 按钮监听，当按下按钮执行对应onClick 动作
        up_button.setOnClickListener(up_buttonListener);
        down_button.setOnClickListener(down_buttonListener);
        detector_button.setOnClickListener(detector_buttonListener);

    }

    @Override
    public void onResume() {
        super.onResume();
//        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }


    public Bitmap detectEdges(Bitmap bitmap) {

        Mat rgba = new Mat(); // 以mat 格式存储原图
        Mat lines = new Mat();////存储检测出的直线坐标的矩阵，每个element有4个通道，第1、2个通道为直线的1个端点，第3、4个通道为直线的另1个端点
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        String[] startPoint, endPoint;
        ArrayList<String[]> pointArray = new ArrayList<>();//存储识别到的所有点
        Utils.bitmapToMat(bitmap, rgba);


        //平滑图像
        Imgproc.blur(rgba, rgba, new Size(3, 3));
//        Imgproc.GaussianBlur(rgba, rgba, new Size(5, 5), 5);//高斯滤波

        //直接检测 角
//        ColorBlobDetector    mDetector = new ColorBlobDetector();
//        mDetector.process(rgba);
//        List<MatOfPoint> contours = mDetector.getContours();
//        Log.e(TAG, "Contours count: " + contours.size());
//        Imgproc.drawContours(rgba, contours, -1,  new Scalar(255, 0, 0));

        //提前对图像进行降噪避免误测
//      Mat img = Highgui.imread(Environment.getExternalStorageDirectory() + "/RubikRobot/" + String.valueOf((++filename<6)? filename : 6) + filetype);
//      Imgproc.pyrMeanShiftFiltering(img, rgba, 0,20);

        //转换为同大小的灰度图像
        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY);


        //二值化 逼近我们感兴趣物体的像素点分布最多的范围
//       threshold(Mat src, Mat dst, double thresh, double maxval, int type)
//        70 180
        Imgproc.threshold(edges, edges, 150, 155, Imgproc.THRESH_BINARY);

        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); i++) {
            System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 100) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println(rect.height);
                if (rect.height > 100) {

                    //System.out.println(rect.x +","+rect.y+","+rect.height+","+rect.width);
                    Core.rectangle(rgba, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                }
            }
        }


        //图像开运算消除外部的点
//       Mat opened = new Mat();
//       Mat se = new Mat(5,5,CV_8U,new Scalar(1));
//       Imgproc.morphologyEx(edges,opened,MORPH_OPEN,se);

        // 边缘检测算子 Canny
//        Imgproc.Canny(edges, edges, 60, 200);

        //直线检测 （灰度，返回结果，精度像素，精度弧度，阈值，最小连接点，碎线段连接的最大间隔值(gap)）
//        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 75, 90, 120);
//        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 60, 80, 100);

        //另一种 从HoughLinesP 返回lines Mat 从取直线起始点的 方法
//        int[] a = new int[(int) lines.total() * lines.channels()]; //数组a存储检测出的直线端点坐标
//        lines.get(0, 0, a);
//        for (int i = 0; i < a.length; i += 4) {
//            Core.line(edges, new Point(a[i], a[i + 1]), new Point(a[i + 2], a[i + 3]), new Scalar(
//                    255, 255, 255), 50);
//            }

//        for (int i = 0; i < lines.cols(); i++) {
//            double[] val = lines.get(0, i);
//
//            // 检测到的线 红
////            Core.line(rgba, new Point(val[0], val[1]), new Point(val[2], val[3]), new Scalar(255, 0, 0), 20);
//
//            // 检测到的点 起点 绿 终点 蓝
//            Core.circle(rgba, new Point(val[0], val[1]), 5, new Scalar(0, 255, 0), -1);
//            Core.circle(rgba,new Point(val[2], val[3]),5, new Scalar(0, 0, 255), -1);
//
//
//            Log.i(TAG, "点1: " + (int)val[0] +" "+ (int)val[1]);//startPoint
//            Log.i(TAG, "点2: " + (int)val[2] +" "+ (int)val[3]);//endPoint
//
//            //将坐标x y 值 构造为字符串
//            startPoint = (String.valueOf((int) val[0])+" " +String.valueOf((int) val[1])).split(" ");
//            endPoint = (String.valueOf((int) val[2])+" " +String.valueOf((int) val[3])).split(" ");
//
//            // 添加到 ArrayList<String[]> 类型，传入k-means 算法
//            // pointArray 和 startPoint 共享指针，追加元素会更改所有 坑！！！
//            pointArray.add(startPoint);
//            pointArray.add(endPoint);
//        }

        Toast.makeText(this, "加油，卡布达", Toast.LENGTH_LONG).show();

        // 经k-means 聚类后的中心点，作为魔方的中心位置,参数 2表示聚类 2 个中心
//        centerPoint = mkmeans.getClusterPoint(pointArray, 1);
//        Core.circle(rgba, new Point((int)centerPoint[0],(int)centerPoint[1]), 15, new Scalar(255, 255, 255), -1);
//
//        maxClusterDistancePoint = mkmeans.getMaxClusterDistance(centerPoint);
//        Core.circle(rgba, new Point((int)maxClusterDistancePoint[0],(int)maxClusterDistancePoint[1]), 15, new Scalar(255, 255, 255), -1);


        // 返回处理完成后的 bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgba, resultBitmap);

        //释放
        rgba.release();
        edges.release();
        lines.release();
//        a = null;

        return resultBitmap;
    }

    /**
     * @param bitmap
     * @return 一般对颜色空间的图像进行有效处理都是在HSV空间进行的，
     * 然后对于基本色中对应的HSV分量需要给定一个严格的范围，下面是通过实验计算的模糊范围（准确的范围在网上都没有给出）。
     * H:  0— 180   S:  0— 255  V:  0— 255
     * <p>
     * 黑	灰	白	红		橙	黄	绿	青	蓝	紫
     * hmin	0	0	0	0		11	26	35	78	100	125
     * hmax	180	180	180	10		25	34	77	99	124	155
     * smin	0	0	0	43		43	43	43	43	43	43
     * smax	255	43	30	255		255	255	255	255	255	255
     * vmin	0	46	221	46		46	46	46	46	46	46
     * vmax	46	220	255	255		255	255	255	255	255	255
     * <p>
     * 蓝    黄   红   橙   绿
     */
    private Bitmap detectCorner(Bitmap bitmap) {

        Mat mRgbMat = new Mat();
        Mat mHsvMat = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Utils.bitmapToMat(bitmap, mRgbMat);

        //转换色彩空间 hsv
        Imgproc.cvtColor(mRgbMat, mHsvMat, Imgproc.COLOR_RGB2HSV, 1);

        //图像预处理 寻找蓝色
        Scalar lowerThreshold = new Scalar(120, 100, 100); // Blue color – lower hsv values
        Scalar upperThreshold = new Scalar(179, 255, 255); // Blue color – higher hsv values
        Core.inRange(mHsvMat, lowerThreshold, upperThreshold, mHsvMat);

        //膨胀
        Imgproc.dilate(mHsvMat, mHsvMat, new Mat());

        Imgproc.findContours(mHsvMat, contours, new Mat(), Imgproc.CHAIN_APPROX_NONE, Imgproc.CHAIN_APPROX_SIMPLE);

        for (int i = 0; i < contours.size(); i++) {
            System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 100) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println(rect.height);
                if (rect.height > 100) {

                    //System.out.println(rect.x +","+rect.y+","+rect.height+","+rect.width);
                    Core.rectangle(mRgbMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                }
            }
        }

//        //转化为灰度
//        Mat graymat = new Mat(rgba.size(), CvType.CV_8UC1);
//        Imgproc.cvtColor(rgba, graymat, Imgproc.COLOR_RGB2GRAY);
//
//        Mat dstmat = new Mat();
//
//        Imgproc.Canny(graymat, graymat, 60, 200);
//
//        /// Detector parameters
//        int blockSize = 2;
//        double thresh = 44;
//        int apertureSize = 3;
//        double k = 0.04;
//
//        /// 角检测
//        Imgproc.cornerHarris(graymat, dstmat, blockSize, apertureSize, k, BORDER_DEFAULT);
//
//        /// 归一化
//        Core.normalize(dstmat, dstmat, 0, 255, NORM_MINMAX, CV_32FC1, new Mat());
//        Core.convertScaleAbs(dstmat, dstmat);
//
//        /// 围绕角点画圆
//        for (int j = 0; j < dstmat.rows(); j++) {
//            for (int i = 0; i < dstmat.cols(); i++) {
//                Log.i(TAG, " successfully: " + dstmat.get(j, i)[0]);
//                if (dstmat.get(j, i)[0] > thresh) //判断阈值，画角
//                {
//                    Core.circle(rgba, new Point(j, i), 10, new Scalar(255, 0, 0), 2, 8, 0);
//                }
//            }
//        }

        Bitmap resultBitmap = Bitmap.createBitmap(mRgbMat.cols(), mRgbMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mRgbMat, resultBitmap);

//        rgba.release();
//        dstmat.release();
        return resultBitmap;
    }

}

