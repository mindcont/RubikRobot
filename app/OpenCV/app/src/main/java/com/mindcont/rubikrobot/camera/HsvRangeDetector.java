package com.mindcont.rubikrobot.camera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mindcont.rubikrobot.R;
import com.mindcont.rubikrobot.util.LayoutCalculator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.mindcont.rubikrobot.util.BitmapLoadUtil.decodeFixedSizeForFile;
import static org.opencv.core.CvType.CV_8UC1;

/**
 * @author mindcont
 * @version V1.0
 * @Title: git
 * @Package com.mindcont.rubikrobot.camera
 * @Description: TODO(添加描述)
 * @date 2017/3/5:14:30
 */

/**
 * 一般对颜色空间的图像进行有效处理都是在HSV空间进行的，
 * 然后对于基本色中对应的HSV分量需要给定一个严格的范围，
 * 下面是通过实验计算的模糊范围（准确的范围在网上都没有给出）。
 * <p>
 * H:  0— 180   S:  0— 255  V:  0— 255  (传统值)
 * <p>
 * 黑	灰	白	红		橙	黄	绿	青	蓝	紫
 * hmin	0	0	0	0		11	26	35	78	100	125
 * hmax	180	180	180	10		25	34	77	99	124	155
 * smin	0	0	0	43		43	43	43	43	43	43
 * smax	255	43	30	255		255	255	255	255	255	255
 * vmin	0	46	221	46		46	46	46	46	46	46
 * vmax	46	220	255	255		255	255	255	255	255	255
 * <p>
 * <p>
 * <p>
 * <p>
 * 扫描顺序 上(白) 右（绿） 前（黄）下（橘红） 左（红） 后（蓝）
 * <p>
 * <p>
 * 绿	黄	橘	蓝	红	 白	 （根据本魔方修订值）
 * minH	34	19	3	100   0      15
 * maxH	79	35	15	122   2     180
 * minS	44	84	172	43    107    19
 * maxS	255	255	255	255   255   94
 * minV	86	161	154	0      40   189
 * maxV	255	255	255	255   255   211
 */

public class HsvRangeDetector extends Activity {

    private static final String TAG = "HsvRangeDetector ";
    private static String filetype = ".jpg";
    Mat mRgba;
    // 声明控件
    private TextView minHue, maxHue, minSat, maxSat, minValue, maxValue;
    private SeekBar minHueSeek, maxHueSeek, minSatSeek, maxSatSeek, minValueSeek, maxValueSeek;
    private Button hsvUpButton, hsvDownButton, processButton;
    private int minHueValue, maxHueValue, minSatValue, maxSatValue, minValValue, maxValValue;
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            minHueValue = minHueSeek.getProgress();
            maxHueValue = maxHueSeek.getProgress();
            minSatValue = minSatSeek.getProgress();
            maxSatValue = maxSatSeek.getProgress();
            minValValue = minValueSeek.getProgress();
            maxValValue = maxValueSeek.getProgress();

            minHue.setText("minHue : " + minHueValue);
            maxHue.setText("maxHue : " + maxHueValue);
            minSat.setText("minSat : " + minSatValue);
            maxSat.setText("maxSat : " + maxSatValue);
            minValue.setText("minVal : " + minValValue);
            maxValue.setText("maxVal : " + maxValValue);

//            hsv_imageView.setImageBitmap(updateHSVFromCV(rawBitmap));
            hsv_imageView.setImageBitmap(findRectangle(rawBitmap));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    private ImageView rgb_imageView, hsv_imageView;
    private Bitmap rawBitmap;
    private int imgId = 0;
    private String filePath;
    // 确定裁剪的位置和裁剪的大小
//    private int cutRect[] = {450, 200, 850, 850};
//    private  int cutRect[] = {60,420,500,500} ;
    private int cutRect[] = {420, 140, 500, 500};

    private int maxRows;
    private int maxCols;
    private int rowStart;
    private int colStart;
    private ArrayList<Rect> rectangles;
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
    private Button.OnClickListener hsvUpButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //将保存在本地的图片取出并缩小后显示在界面上

            if (imgId > 0) {
                imgId = imgId - 1;
            } else {
                imgId = 0;
                Toast.makeText(HsvRangeDetector.this, "已经是第一张了", Toast.LENGTH_LONG).show();
            }

            filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/1/" + String.valueOf(imgId) + filetype;
            rawBitmap = decodeFixedSizeForFile(filePath, 1);


            rawBitmap = Bitmap.createBitmap(rawBitmap,
                    cutRect[0], cutRect[1],
                    cutRect[2], cutRect[3]);

            rgb_imageView.setImageBitmap(rawBitmap);
//            hsv_imageView.setImageBitmap(rawBitmap);
        }
    };
    private Button.OnClickListener hsvDownButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (imgId < 5) {
                imgId = imgId + 1;
            } else {
                imgId = 5;
                Toast.makeText(HsvRangeDetector.this, "已经是最后一张了", Toast.LENGTH_LONG).show();
            }

            filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/1/" + String.valueOf(imgId) + filetype;
            rawBitmap = decodeFixedSizeForFile(filePath, 1);

            rawBitmap = Bitmap.createBitmap(rawBitmap,
                    cutRect[0], cutRect[1],
                    cutRect[2], cutRect[3]);

            rgb_imageView.setImageBitmap(rawBitmap);
//            hsv_imageView.setImageBitmap(rawBitmap);
        }
    };
    private Button.OnClickListener processButtonListener = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(HsvRangeDetector.this, "处理中", Toast.LENGTH_LONG).show();
//            hsv_imageView.setImageBitmap(updateHSV(rawBitmap));
//            hsv_imageView.setImageBitmap(updateHSVFromCV(rawBitmap));

            hsv_imageView.setImageBitmap(findRectangle(rawBitmap));
//            hsv_imageView.setImageBitmap(getFaceColor(rawBitmap));
//            画框
//            filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/1/" + String.valueOf(imgId) + filetype;
//            hsv_imageView.setImageBitmap(getColor(filePath));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hsv_detector);

        hsvUpButton = (Button) findViewById(R.id.hsv_up_button);
        hsvDownButton = (Button) findViewById(R.id.hsv_down_button);
        processButton = (Button) findViewById(R.id.processButton);
        rgb_imageView = (ImageView) findViewById(R.id.rgb_imageView);
        hsv_imageView = (ImageView) findViewById(R.id.hsv_imageView);

        // 用于显示 hsv 颜色空间的最大值、最小值
        minHue = (TextView) findViewById(R.id.minHue);
        maxHue = (TextView) findViewById(R.id.maxHue);
        minSat = (TextView) findViewById(R.id.minSat);
        maxSat = (TextView) findViewById(R.id.maxSat);
        minValue = (TextView) findViewById(R.id.minValue);
        maxValue = (TextView) findViewById(R.id.maxValue);

        // 滑动块 调节hsv
        minHueSeek = (SeekBar) findViewById(R.id.minHueSeek);
        maxHueSeek = (SeekBar) findViewById(R.id.maxHueSeek);
        minSatSeek = (SeekBar) findViewById(R.id.minSatSeek);
        maxSatSeek = (SeekBar) findViewById(R.id.maxSatSeek);
        minValueSeek = (SeekBar) findViewById(R.id.minValueSeek);
        maxValueSeek = (SeekBar) findViewById(R.id.maxValueSeek);

        //设置 SeekBar 监听
        //H:  0— 180   S:  0— 255  V:  0— 255 (in opencv)
        minHueSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        maxHueSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        minSatSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        maxSatSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        minValueSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        maxValueSeek.setOnSeekBarChangeListener(seekBarChangeListener);

        // 设置按钮监听
        hsvUpButton.setOnClickListener(hsvUpButtonListener);
        hsvDownButton.setOnClickListener(hsvDownButtonListener);
        processButton.setOnClickListener(processButtonListener);

        filePath = Environment.getExternalStorageDirectory() + "/RubikRobot/1/" + String.valueOf(imgId) + filetype;
        rawBitmap = decodeFixedSizeForFile(filePath, 1);

        // 确定裁剪的位置和裁剪的大小,这里讲对拍摄到的图片进行剪裁
        // 因为魔方在图像中的位置大体不变，可以通过预先剪裁的方式屏蔽干扰，
        // 不同分辨率的手机需要手动修改这个参数
        rawBitmap = Bitmap.createBitmap(rawBitmap,
                cutRect[0], cutRect[1],
                cutRect[2], cutRect[3]);
        rgb_imageView.setImageBitmap(rawBitmap);
        hsv_imageView.setImageBitmap(rawBitmap);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    private Bitmap updateHSVWithNoCV(Bitmap src) {

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        // 获得SeekBar 的值
        // Hue (0 .. 360) Saturation (0...1) Value (0...1)
        int progressHue = minHueSeek.getProgress() - 256;
        int progressSat = minSatSeek.getProgress() - 256;
        int progressVal = minValueSeek.getProgress() - 256;
        float settingHue = (float) progressHue * 360 / 256;
        float settingSat = (float) progressSat / 256;
        float settingVal = (float) progressVal / 256;

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Convert from Color to HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);

                // Adjust HSV
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] = 0.0f;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] = 360.0f;
                }

                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                // Convert back from HSV to Color
                mapDestColor[index] = Color.HSVToColor(pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.RGB_565);

    }

    ;

    /**
     * 本函数通过滑块找魔方颜色阈值，为下一步识别做准备
     * @param src
     * @return
     */
    private Bitmap updateHSVFromCV(Bitmap src) {

        Mat mRgbMat = new Mat();
        Mat mHsvMat = new Mat();
        Mat dst = new Mat();

        Utils.bitmapToMat(src, mRgbMat);

        //平滑图像
        Imgproc.GaussianBlur(mRgbMat, mRgbMat, new Size(5, 5), 5);//高斯滤波

        //图像预处理
        minHueValue = minHueSeek.getProgress();minHue.setText("minHue : " + minHueValue);
        maxHueValue = maxHueSeek.getProgress();maxHue.setText("maxHue : " + maxHueValue);
        minSatValue = minSatSeek.getProgress();minSat.setText("minSat : " + minSatValue);
        maxSatValue = maxSatSeek.getProgress();maxSat.setText("maxSat : " + maxSatValue);
        minValValue = minValueSeek.getProgress();minValue.setText("minVal : " + minValValue);
        maxValValue = maxValueSeek.getProgress();maxValue.setText("maxVal : " + maxValValue);

        // 转换色彩空间 hsv
        Imgproc.cvtColor(mRgbMat, mHsvMat, Imgproc.COLOR_RGB2HSV);

        // 按滑块调节阈值过滤
        Core.inRange(mHsvMat, new Scalar(minHueValue, minSatValue, minValValue), new Scalar(maxHueValue, maxSatValue, maxValValue),
                mHsvMat);

        // 返回过滤结果
        Bitmap resultBitmap = Bitmap.createBitmap(mRgbMat.cols(), mRgbMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mHsvMat, resultBitmap);

        mRgbMat.release();
        mHsvMat.release();

        return resultBitmap;
    }

    private Bitmap findRectangle (Bitmap src){

        Mat rgba = new Mat();
        Mat gray = new Mat();
        Mat blur = new Mat();
        Mat canny = new Mat();
        Mat dilated = new Mat();


        Utils.bitmapToMat(src, rgba);


        Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(gray, blur, new Size(3, 3), 0);//高斯滤波

        //测试canny算子 阈值
//        minHueValue = minHueSeek.getProgress();minHue.setText("minHue : " + minHueValue);
//        maxHueValue = maxHueSeek.getProgress();maxHue.setText("maxHue : " + maxHueValue);
//        Imgproc.Canny(blur, canny, minSatValue, maxSatValue);

        Imgproc.Canny(blur, canny, 0, 70);

        Imgproc.dilate(canny, dilated, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5,5)));

        //         find contours
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

//        (contours, hierarchy) = cv2.findContours(dilated.copy(),
//                cv2.RETR_TREE,
//                cv2.CHAIN_APPROX_SIMPLE)

        Mat hierarchy = new Mat();
        Imgproc.findContours(dilated, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Log.e(TAG, "contours.size   " +Integer.toString(contours.size()));

        for( int i = 0; i < contours.size(); i++ )
        {
            Rect brect = Imgproc.boundingRect(contours.get(i));
            double k = (brect.width+0.0)/brect.height;

            if(brect.area() > 50000  && k > 0.8 && k < 1.2)
            {
                System.out.println(brect.area());
                RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(i).toArray()));

                Core.circle(rgba, rect.center, 25, new Scalar(255, 255, 255), -1);

                Rect brect2 = rect.boundingRect();
                Log.e(TAG, "brect2   " +Integer.toString(brect2.x));
                Core.line(rgba,new Point(brect2.x, brect2.y), new Point(brect2.x + brect2.width, brect2.y + brect2.height), new Scalar(
                    0, 255, 0), 15);
                Core.rectangle(rgba, new Point(brect2.x, brect2.y), new Point(brect2.x + brect2.width, brect2.y + brect2.height), new Scalar(0, 255, 0));
            }
        }

        Bitmap resultBitmap = Bitmap.createBitmap(rgba.cols(), rgba.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(rgba, resultBitmap);

        return  resultBitmap;

    }
    /**
     * 识别魔方单个面，返回特定顺序的颜色字符串，形如 "URRRUBULF"
     * 扫描顺序 上(白U) 右（绿R） 前（黄F）下（橘红D） 左（L红） 后（蓝B）
     *
     * @return
     */
//    private Bitmap getFaceColor(String imgPath) {
    private Bitmap getFaceColor(Bitmap src) {
        //特定的颜色空间，
        int hsvColor[][] = {
                {0,180,0,35,221,255}, //白
                {63, 95, 66, 255, 162, 255}, //绿
                {23, 61, 66, 255, 162, 255}, //黄
                {0, 15, 125, 255, 201, 255}, //橘
                {0, 3, 163, 255, 144, 255}, //红
                {100, 157, 27, 255, 113, 255}, //蓝
        };

        int[][] centerPointArray = new int[9][3];

        //颜色表
        ArrayList<String> list = new ArrayList<String>() {{
            add("白");
            add("绿");
            add("黄");
            add("橘");
            add("红");
            add("蓝");
        }};

        Mat mRgbMat = new Mat();
        Mat mHsvMat = new Mat();
        Mat dst = new Mat();
        int sumDetectNum = 0;
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //存放 面 识别结果
        String cubeFaceDefinition = "";

        //从文件路径中加载图片并下采样
//        rawBitmap = decodeFixedSizeForFile(imgPath, 2);

        // 确定裁剪的位置和裁剪的大小
//        rawBitmap = Bitmap.createBitmap(rawBitmap,
//                cutRect[0], cutRect[1],
//                cutRect[2], cutRect[3]);
        Utils.bitmapToMat(rawBitmap, mRgbMat);

        //平滑图像
        Imgproc.GaussianBlur(mRgbMat, mRgbMat, new Size(5, 5), 5);//高斯滤波
        // 转换色彩空间 hsv
//        Imgproc.cvtColor(mRgbMat, mHsvMat, Imgproc.COLOR_RGB2HSV, 1);

        //依次检测
        // 上(白U) 右（绿R） 前（黄F）下（橘红D） 左（L红） 后（蓝B）
        int centerPointIndex = 0;
        for (int i = 0; i < hsvColor.length; i++) {


            // 转换色彩空间 hsv
            Imgproc.cvtColor(mRgbMat, mHsvMat, Imgproc.COLOR_RGB2HSV, 1);

            // 依次从hsvColor空间取出 每个颜色的上下阈值
            Scalar lowerThreshold = new Scalar(hsvColor[i][0], hsvColor[i][2], hsvColor[i][4]);
            Scalar upperThreshold = new Scalar(hsvColor[i][1], hsvColor[i][3], hsvColor[i][5]);
            Core.inRange(mHsvMat, lowerThreshold, upperThreshold, mHsvMat);
            //膨胀
            Imgproc.dilate(mHsvMat, mHsvMat, new Mat());

            //腐蚀
            Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(9, 9));
            Imgproc.erode(mHsvMat, mHsvMat, erode);
            //膨胀
            Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(9, 9));
            Imgproc.dilate(mHsvMat, mHsvMat, dilate);

            //检测轮廓
            Imgproc.findContours(mHsvMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_KCOS);

            int colorCount = 0;

            for (int j = 0; j < contours.size(); j++) {
//                System.out.println(Imgproc.contourArea(contours.get(j)));

                if (Imgproc.contourArea(contours.get(j)) > 100) {
                    Rect rect = Imgproc.boundingRect(contours.get(j));
//                    System.out.println(rect.height);

                    if (rect.height > 90) {

                        //每种颜色的中心点坐标，不止一个
//                        System.out.println(rect.x +","+rect.y+","+rect.height+","+rect.width);
                        Core.rectangle(mRgbMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                        Core.circle(mRgbMat, new Point(rect.x + rect.width / 2, rect.y + rect.height / 2), 15, new Scalar(0, 255, 0), -1);

                        System.out.println("x :" + (int) (rect.x + rect.width / 2) + " y :" + (int) (rect.y + rect.height / 2) + " 对应颜色 ：" + list.get(i));

//                        centerPointIndex = centerPointIndex + colorCount;
//                        centerPointArray[centerPointIndex][0] = (int)(rect.x + rect.width / 2);
//                        centerPointArray[centerPointIndex][1] = (int)(rect.y + rect.height / 2);
//                        centerPointArray[centerPointIndex][2] = i;

                        colorCount = colorCount + 1;
                    }
                }
            }
            contours.clear();
            mHsvMat.release();
//            mRgbMatClone.release();
            System.out.println("本张图片该颜色数量为 :" + colorCount);
            sumDetectNum = sumDetectNum + colorCount;
            // 0白 1绿 2黄 3橘 4红 5蓝
            Toast.makeText(HsvRangeDetector.this, "处理中", Toast.LENGTH_LONG).show();

        }
        System.out.println(" 本张图片共检出颜色总数为 ----------------:" + sumDetectNum);

        Bitmap resultBitmap = Bitmap.createBitmap(mRgbMat.cols(), mRgbMat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mRgbMat, resultBitmap);

        mRgbMat.release();
//        mHsvMat.release();

        return resultBitmap;
//        return  cubeFaceDefinition;
    }

    ;


    private Bitmap getColor(String imgPath) {

        mRgba = new Mat();
        rawBitmap = decodeFixedSizeForFile(imgPath, 1);

        rawBitmap = Bitmap.createBitmap(rawBitmap,
                cutRect[0], cutRect[1],
                cutRect[2], cutRect[3]);
        Utils.bitmapToMat(rawBitmap, mRgba);

        // Rectangles
        LayoutCalculator lc = new LayoutCalculator(mRgba.width(), mRgba.height());
        rectangles = lc.calculateRectanglesCoordinates();

        maxRows = rectangles.get(0).height - 1;
        maxCols = rectangles.get(0).width - 1;

        rowStart = (int) Math.ceil(maxRows / 3.0);
        colStart = (int) Math.ceil(maxCols / 3.0);

        for (int i = 0; i < rectangles.size(); i++) {

//            System.out.println(rectangles.get(i).x+" "+rectangles.get(i).y);
            Core.rectangle(mRgba, new Point(rectangles.get(i).x, rectangles.get(i).y), new Point(rectangles.get(i).x + rectangles.get(i).width, rectangles.get(i).y + rectangles.get(i).height), new Scalar(255, 255, 255), 5);
//            Core.putText(mRgba, new String().valueOf(i), new Point(rectangles.get(i).x+rectangles.get(i).width/2, rectangles.get(i).y+rectangles.get(i).height/2), Core.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 0, 255), 5);
            dectectColor(mRgba.submat(rectangles.get(i)));
        }


        Bitmap resultBitmap = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mRgba, resultBitmap);
        return resultBitmap;
    }

    ;

    private void dectectColor(Mat src) {

        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2HSV);


        double hue = src.get(maxRows / 2, maxCols / 2)[0];
        double lum = src.get(maxRows / 2, maxCols / 2)[1];
        for (int row = rowStart; row <= maxRows; row = row + rowStart) {
            for (int col = colStart; col <= maxCols; col = col + colStart) {
                hue += src.get(row, col)[0];
                lum += src.get(row, col)[1];
                Core.circle(mRgba, new Point(row, col), 10, new Scalar(255, 0, 0));
//                System.out.println("row： "+ row + "col " + col);
            }
        }
        hue /= 5;
        lum /= 5;

        System.out.println("hue： " + hue + "lum " + lum);

    }

    ;
}

