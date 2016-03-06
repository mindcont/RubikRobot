package com.mindcont.rubikrobot.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by Administrator on 2016/2/20.
 */
public class ColorRecognition {

    private static final int[] red_std = {140, 32, 41};
    private static final int[] green_std = {0, 162, 57};
    private static final int[] blue_std = {33, 36, 123};
    private static final int[] yellow_std = {173, 223, 74};
    private static final int[] orange_std = {255, 77, 66};
    private static final int[] white_std = {200, 200, 200};
    private static final int[] Xaxis_color = {920, 1440, 2016};
    private static final int[] Yaxis_color = {400, 970, 1500};
    public static String surface1_pixel[] = new String[9];
    public static String surface2_pixel[] = new String[9];
    public static String surface3_pixel[] = new String[9];
    public static String surface4_pixel[] = new String[9];
    public static String surface5_pixel[] = new String[9];
    public static String surface6_pixel[] = new String[9];
    public static String surface1_color[] = new String[9];
    public static String surface2_color[] = new String[9];
    public static String surface3_color[] = new String[9];
    public static String surface4_color[] = new String[9];
    public static String surface5_color[] = new String[9];
    public static String surface6_color[] = new String[9];
    public static String face_pixel[] = new String[9];
    private static String filepath = "/mnt/extsd0/";
    private static String filetppe = ".jpg";
    private static int filename = 0;
    Bitmap rawBitmap;

    public static String colorRecog(int photo_color) {
        int color_diff[] = new int[6];
        int key;
        String color = "";

        int R = ((photo_color & 0xff0000) >> 16);
        int G = ((photo_color & 0xff00) >> 8);
        int B = ((photo_color & 0xff));
        color_diff[0] = (Math.abs(R - red_std[0]) + Math.abs(G - red_std[1]) + Math.abs(B - red_std[2]));
        color_diff[1] = (Math.abs(R - green_std[0]) + Math.abs(G - green_std[1]) + Math.abs(B - green_std[2]));
        color_diff[2] = (Math.abs(R - blue_std[0]) + Math.abs(G - blue_std[1]) + Math.abs(B - blue_std[2]));
        color_diff[3] = (Math.abs(R - yellow_std[0]) + Math.abs(G - yellow_std[1]) + Math.abs(B - yellow_std[2]));
        color_diff[4] = (Math.abs(R - orange_std[0]) + Math.abs(G - orange_std[1]) + Math.abs(B - orange_std[2]));
        color_diff[5] = (Math.abs(R - white_std[0]) + Math.abs(G - white_std[1]) + Math.abs(B - white_std[2]));
        key = getMinKey(color_diff);

        switch (key) {
            case 0: {
                color = "red";
            }
            break;
            case 1: {
                color = "green";
            }
            break;
            case 2: {
                color = "blue";
            }
            break;
            case 3: {
                color = "yellow";
            }
            break;
            case 4: {
                color = "orange";
            }
            break;
            case 5: {
                color = "white";
            }
            break;

        }
        return color;

    }


    public static int getMinKey(int color_diff[]) {
        int min = color_diff[0];
        int min_key = 0;
        for (int i = 1; i < color_diff.length; i++) {
            if (min > color_diff[i]) {
                min = color_diff[i];
                min_key = i;
            }
        }
        return min_key;

    }

    public static int getXaxisColor1() {
        return Xaxis_color[0];
    }

    public static int getXaxisColor2() {
        return Xaxis_color[1];
    }

    public static int getXaxisColor3() {
        return Xaxis_color[2];
    }

    public static int getYaxisColor1() {
        return Yaxis_color[0];
    }

    public static int getYaxisColor2() {
        return Yaxis_color[1];
    }

    public static int getYaxisColor3() {
        return Yaxis_color[2];
    }

    public void getPhotoColor(int filename) {
        rawBitmap = BitmapFactory.decodeFile(filepath + String.valueOf(filename) + filetppe);

        switch (filename) {

            case 1: {
                getPhotoPixel(surface6_pixel, 1);
            }
            break;
            case 2: {
                getPhotoPixel(surface3_pixel, 2);
            }
            break;
            case 3: {
                getPhotoPixel(surface2_pixel, 3);
            }
            break;
            case 4: {
                getPhotoPixel(surface5_pixel, 4);
            }
            break;
            case 5: {
                getPhotoPixel(surface4_pixel, 5);
            }
            break;
            case 6: {
                getPhotoPixel(surface1_pixel, 6);
            }
            break;
        }
    }


    public void getPhotoPixel(String surface_pixel[], int filename) {
        surface_pixel[0] = colorRecog(rawBitmap.getPixel(getXaxisColor1(), getYaxisColor1()));
        surface_pixel[1] = colorRecog(rawBitmap.getPixel(getXaxisColor2(), getYaxisColor1()));
        surface_pixel[2] = colorRecog(rawBitmap.getPixel(getXaxisColor3(), getYaxisColor1()));
        surface_pixel[3] = colorRecog(rawBitmap.getPixel(getXaxisColor1(), getYaxisColor2()));
        surface_pixel[4] = colorRecog(rawBitmap.getPixel(getXaxisColor2(), getYaxisColor2()));
        surface_pixel[5] = colorRecog(rawBitmap.getPixel(getXaxisColor3(), getYaxisColor2()));
        surface_pixel[6] = colorRecog(rawBitmap.getPixel(getXaxisColor1(), getYaxisColor3()));
        surface_pixel[7] = colorRecog(rawBitmap.getPixel(getXaxisColor2(), getYaxisColor3()));
        surface_pixel[8] = colorRecog(rawBitmap.getPixel(getXaxisColor3(), getYaxisColor3()));

        switch (filename) {
            case 1: {
                adjustColorOrder1(surface_pixel);

            }
            break;

            case 2: {
                adjustColorOrder2(surface_pixel);
            }
            break;

            case 3: {
                adjustColorOrder3(surface_pixel);
            }
            break;

            case 4: {
                adjustColorOrder4(surface_pixel);
            }
            break;

            case 5: {
                adjustColorOrder5(surface_pixel);
            }
            break;

            case 6: {
            }
            break;

        }

    }

    public void adjustColorOrder1(String surface_pixel[]) {


        for (int i = 0; i < surface_pixel.length; i++) {
            face_pixel[i] = surface_pixel[i];
        }
        surface_pixel[0] = face_pixel[6];
        surface_pixel[1] = face_pixel[3];
        surface_pixel[2] = face_pixel[0];
        surface_pixel[3] = face_pixel[7];
        surface_pixel[4] = face_pixel[4];
        surface_pixel[5] = face_pixel[1];
        surface_pixel[6] = face_pixel[8];
        surface_pixel[7] = face_pixel[5];
        surface_pixel[8] = face_pixel[2];

    }

    public void adjustColorOrder2(String surface_pixel[]) {

        for (int i = 0; i < surface_pixel.length; i++) {
            face_pixel[i] = surface_pixel[i];
        }
        surface_pixel[0] = face_pixel[2];
        surface_pixel[1] = face_pixel[5];
        surface_pixel[2] = face_pixel[8];
        surface_pixel[3] = face_pixel[1];
        surface_pixel[4] = face_pixel[4];
        surface_pixel[5] = face_pixel[7];
        surface_pixel[6] = face_pixel[0];
        surface_pixel[7] = face_pixel[3];
        surface_pixel[8] = face_pixel[6];
    }

    public void adjustColorOrder3(String surface_pixel[]) {

        for (int i = 0; i < surface_pixel.length; i++) {
            face_pixel[i] = surface_pixel[i];
        }
        surface_pixel[0] = face_pixel[2];
        surface_pixel[1] = face_pixel[5];
        surface_pixel[2] = face_pixel[8];
        surface_pixel[3] = face_pixel[1];
        surface_pixel[4] = face_pixel[4];
        surface_pixel[5] = face_pixel[7];
        surface_pixel[6] = face_pixel[0];
        surface_pixel[7] = face_pixel[3];
        surface_pixel[8] = face_pixel[6];


    }

    public void adjustColorOrder4(String surface_pixel[]) {

        for (int i = 0; i < surface_pixel.length; i++) {
            face_pixel[i] = surface_pixel[i];
        }
        surface_pixel[0] = face_pixel[2];
        surface_pixel[1] = face_pixel[5];
        surface_pixel[2] = face_pixel[8];
        surface_pixel[3] = face_pixel[1];
        surface_pixel[4] = face_pixel[4];
        surface_pixel[5] = face_pixel[7];
        surface_pixel[6] = face_pixel[0];
        surface_pixel[7] = face_pixel[3];
        surface_pixel[8] = face_pixel[6];

    }

    public void adjustColorOrder5(String surface_pixel[]) {

        for (int i = 0; i < surface_pixel.length; i++) {
            face_pixel[i] = surface_pixel[i];
        }

        surface_pixel[0] = face_pixel[8];
        surface_pixel[1] = face_pixel[7];
        surface_pixel[2] = face_pixel[6];
        surface_pixel[3] = face_pixel[5];
        surface_pixel[4] = face_pixel[4];
        surface_pixel[5] = face_pixel[3];
        surface_pixel[6] = face_pixel[2];
        surface_pixel[7] = face_pixel[1];
        surface_pixel[8] = face_pixel[0];
    }

    // 扫描顺序 上(白) 右（绿） 前（黄）下（橘红） 左（红） 后（蓝）
    private String getUpColor() {
        return surface1_pixel[4];
    }

    private String getRightColor() {
        return surface2_pixel[4];
    }

    private String getFrontColor() {
        return surface3_pixel[4];
    }

    private String getDowmColor() {
        return surface4_pixel[4];
    }

    private String getLeftColor() {
        return surface5_pixel[4];
    }

    private String getBackColor() {
        return surface6_pixel[4];
    }


    public void changeColorToString(String[] surface_pixel, String[] surface_color) {
        final String upColor = getUpColor();
        final String dowmColor = getDowmColor();
        final String leftColor = getLeftColor();
        final String rightColor = getRightColor();
        final String frontColor = getFrontColor();
        final String backColor = getBackColor();

        for (int i = 0; i < surface_pixel.length; i++) {
            if (surface_pixel[i] == upColor) {
                surface_color[i] = "U";
            } else if (surface_pixel[i] == dowmColor) {
                surface_color[i] = "D";
            } else if (surface_pixel[i] == leftColor) {
                surface_color[i] = "L";
            } else if (surface_pixel[i] == rightColor) {
                surface_color[i] = "R";
            } else if (surface_pixel[i] == frontColor) {
                surface_color[i] = "F";
            } else if (surface_pixel[i] == backColor) {
                surface_color[i] = "B";
            }

        }
    }

    public String getRubikTotalColor() {

        String rubikTotalColor = "";

        getPhotoColor(1);
        getPhotoColor(2);
        getPhotoColor(3);
        getPhotoColor(4);
        getPhotoColor(5);
        getPhotoColor(6);

        changeColorToString(surface1_pixel, surface1_color);
        changeColorToString(surface2_pixel, surface2_color);
        changeColorToString(surface3_pixel, surface3_color);
        changeColorToString(surface4_pixel, surface4_color);
        changeColorToString(surface5_pixel, surface5_color);
        changeColorToString(surface6_pixel, surface6_color);

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface1_color[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface2_color[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface3_color[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface4_color[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface5_color[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalColor += surface6_color[i];
        }

        return rubikTotalColor;

    }

    public String getRubikTotalOrginalColor() {

//        String rubikTotalOrginalColor = "URRRUBULFLDFURLBLDRBUFFBURRLBDLDFDFLBUFDLRFUBDDRDBFBUL";
        String rubikTotalOrginalColor = "";

        getPhotoColor(1);
        getPhotoColor(2);
        getPhotoColor(3);
        getPhotoColor(4);
        getPhotoColor(5);
        getPhotoColor(6);

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface1_pixel[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface2_pixel[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface3_pixel[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface4_pixel[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface5_pixel[i];
        }

        for (int i = 0; i < 9; i++) {
            rubikTotalOrginalColor += surface6_pixel[i];
        }

        return rubikTotalOrginalColor;

    }

}
