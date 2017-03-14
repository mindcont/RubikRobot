package com.mindcont.rubikrobot.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @author mindcont
 * @version V1.0
 * @Title: RubikRobot
 * @Package com.mindcont.rubikrobot.util
 * @Description: 图像 下采样
 * @date 2017/3/3:10:02
 */

public class BitmapLoadUtil {
    /**
     * 对一个Resources的资源文件进行指定长宽来加载进内存, 并把这个bitmap对象返回
     *
     * @param res       资源文件对象
     * @param resId     要操作的图片id
     * @param reqWidth  最终想要得到bitmap的宽度
     * @param reqHeight 最终想要得到bitmap的高度
     * @return 返回采样之后的bitmap对象
     */
    public static Bitmap decodeFixedSizeForResource(Resources res, int resId, int reqWidth, int reqHeight) {

        // 首先先指定加载的模式 为只是获取资源文件的大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        //Calculate Size  计算要设置的采样率 并把值设置到option上
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 关闭只加载属性模式, 并重新加载的时候传入自定义的options对象
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从路径中文件进行指定长宽来加载进内存, 并把这个bitmap对象返回
     *
     * @param filePath     资源文件对象
     * @param inSampleSize 下采样比例
     * @return 返回采样之后的bitmap对象
     */
    public static Bitmap decodeFixedSizeForFile(String filePath, int inSampleSize) {

        double start_time = System.currentTimeMillis();

        // 首先先指定加载的模式 为只是获取资源文件的大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //二次采样开始
        //二次采样时我需要将图片加载出来显示，不能只加载图片的框架，因此inJustDecodeBounds属性要设置为false
        options.inJustDecodeBounds = false;

        //Calculate Size  计算要设置的采样率 并把值设置到option上 设置缩放比例
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        options.inSampleSize = 2;//这里长宽各缩小2倍，像素缩小4倍

        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        double end_time = System.currentTimeMillis();
        Log.v("BitmapTest", "UI time consume:" + (end_time - start_time));

        //加载图片并返回
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 从路径中文件进行指定长宽来加载进内存, 并把这个bitmap对象返回
     *
     * @param filePath  资源文件对象
     * @param reqWidth  最终想要得到bitmap的宽度
     * @param reqHeight 最终想要得到bitmap的高度
     * @return 返回采样之后的bitmap对象
     */
    public static Bitmap decodeFixedSizeForFile(String filePath, int reqWidth, int reqHeight) {

        double start_time = System.currentTimeMillis();

        // 首先先指定加载的模式 为只是获取资源文件的大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //二次采样开始
        //二次采样时我需要将图片加载出来显示，不能只加载图片的框架，因此inJustDecodeBounds属性要设置为false
        options.inJustDecodeBounds = false;

        //Calculate Size  计算要设置的采样率 并把值设置到option上 设置缩放比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

//        options.inSampleSize = 2;//这里长宽各缩小2倍，像素缩小4倍
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        double end_time = System.currentTimeMillis();
        Log.v("BitmapTest", "UI time consume:" + (end_time - start_time));

        //加载图片并返回
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 一个计算工具类的方法, 传入图片的属性对象和 想要实现的目标大小. 通过计算得到采样值
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //Raw height and width of image
        //原始图片的宽高属性
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        // 如果想要实现的宽高比原始图片的宽高小那么就可以计算出采样率, 否则不需要改变采样率
        if (reqWidth < height || reqHeight < width) {
            int halfWidth = width / 2;
            int halfHeight = height / 2;
            // 判断原始长宽的一半是否比目标大小小, 如果小那么增大采样率2倍, 直到出现修改后原始值会比目标值大的时候
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}