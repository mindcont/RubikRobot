package com.mindcont.rubikrobot.Camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by Liu bing on 2016/2/20.
 */
public class ColorRecognition {


    private static String filepath="/mnt/extsd0/";//通过测试得到的ARMPC C30 SD卡路径
    private static String filetppe=".jpg";
    private static int filename =0;

    Bitmap rawBitmap;

    //通过测试得到的魔方六种颜色对应的RGB值
    private static final int [] red_std={140,32,41};
    private static final int []green_std={0,162,57};
    private static final int [] blue_std={33,36,123};
    private static final int [] yellow_std={173,223,74};
    private static final int [] orange_std={255,77,66};
    private static final int [] white_std={200,200,200};


    private static final int []Xaxis_color={920,1440,2016};//通过测试得到的魔方颜色块对应的三个X轴坐标

    private static final int []Yaxis_color={400,970,1500};//通过测试得到的魔方颜色块对应的三个Y轴坐标

    //红、绿、蓝、黄、橙、白形式的颜色
    public static String surface1_pixel[]=new String[9];//魔方上面的9个颜色
    public static String surface2_pixel[]=new String[9];//魔方右面的9个颜色
    public static String surface3_pixel[]=new String[9];//魔方前面的9个颜色
    public static String surface4_pixel[]=new String[9];//魔方下面的9个颜色
    public static String surface5_pixel[]=new String[9];//魔方左面的9个颜色
    public static String surface6_pixel[]=new String[9];//魔方后面的9个颜色

    //U、D、L、R、F、B形式的颜色
    public static String surface1_color[]=new String[9];//魔方上面的9个颜色
    public static String surface2_color[]=new String[9];//魔方右面的9个颜色
    public static String surface3_color[]=new String[9];//魔方前面的9个颜色
    public static String surface4_color[]=new String[9];//魔方下面的9个颜色
    public static String surface5_color[]=new String[9];//魔方左面的9个颜色
    public static String surface6_color[]=new String[9];//魔方后面的9个颜色
    public static String rubiktotalcolor;   //红、绿、蓝、黄、橙、白形式的54种颜色
    public static String face_pixel[]=new String[9];

    //得到int型颜色值所对应的颜色
    public static String ColorRecog (int photo_color)
    {
        int color_diff[]=new int [6];
        int key;
        String color="";

        int R = ((photo_color & 0xff0000) >> 16);
        int G = ((photo_color & 0xff00) >> 8);
        int B = ((photo_color & 0xff));
        color_diff[0]=(Math.abs(R-red_std[0])+Math.abs(G-red_std[1])+Math.abs(B-red_std[2]));
        color_diff[1]=(Math.abs(R - green_std[0])+Math.abs(G-green_std[1])+Math.abs(B-green_std[2]));
        color_diff[2]=(Math.abs(R-blue_std[0])+Math.abs(G-blue_std[1])+Math.abs(B-blue_std[2]));
        color_diff[3]=(Math.abs(R-yellow_std[0])+Math.abs(G-yellow_std[1])+Math.abs(B-yellow_std[2]));
        color_diff[4]=(Math.abs(R-orange_std[0])+Math.abs(G-orange_std[1])+Math.abs(B-orange_std[2]));
        color_diff[5]=(Math.abs(R - white_std[0]) + Math.abs(G - white_std[1]) + Math.abs(B - white_std[2]));
        key=Get_MinKey(color_diff);

        switch (key)
        {
            case 0 :
            {
                color="red";
            }break;
            case 1 :
            {
                color="green";
            }break;
            case 2 :
            {
                color="blue";
            }break;
            case 3 :
            {
                color="yellow";
            }break;
            case 4 :
            {
                color="orange";
            }break;
            case 5 :
            {
                color="white";
            }break;


        }
        return  color;


    }


    //获取一个数组中最小值所在的数组标号
    public static int Get_MinKey(int color_diff[])
    {
        int min=color_diff[0];
        int min_key=0;
        for(int i=1;i<color_diff.length;i++)
        {
            if(min>color_diff[i])
            {
                min=color_diff[i];
                min_key=i;
            }
        }
        return  min_key;

    }

    public static int Get_XaxisColor1 ()
    {
        return Xaxis_color[0];
    }

    public static int Get_XaxisColor2 ()
    {
        return Xaxis_color[1];
    }

    public static int Get_XaxisColor3 ()
    {
        return Xaxis_color[2];
    }

    public static int Get_YaxisColor1 ()
    {
        return Yaxis_color[0];
    }

    public static int Get_YaxisColor2 ()
    {
        return Yaxis_color[1];
    }

    public static int Get_YaxisColor3 ()
    {
        return Yaxis_color[2];
    }



    //从SD中名称为1,2,3,4,5,6共6张图片中获得魔方54块颜色块颜色
    public void Get_Photocolor(int filename)
    {
        rawBitmap = BitmapFactory.decodeFile(filepath + String.valueOf(filename) + filetppe);

        switch (filename) {

            case 1: {
                Get_PhotoPixel(surface6_pixel, 1);
            }break;
            case 2: {
                Get_PhotoPixel(surface3_pixel, 2);
            }break;

            case 3: {
                Get_PhotoPixel(surface2_pixel, 3);
            }break;

            case 4: {
                Get_PhotoPixel(surface5_pixel, 4);
            }break;

            case 5: {
                Get_PhotoPixel(surface4_pixel, 5);
            }break;

            case 6: {
                Get_PhotoPixel(surface1_pixel,6);
            }break;

        }


    }



    public void Get_PhotoPixel(String surface_pixel[],int filename)
    {

        surface_pixel[0]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor1(), Get_YaxisColor1()));
        surface_pixel[1]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor2(), Get_YaxisColor1()));
        surface_pixel[2]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor3(), Get_YaxisColor1()));
        surface_pixel[3]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor1(), Get_YaxisColor2()));
        surface_pixel[4]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor2(), Get_YaxisColor2()));
        surface_pixel[5]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor3(), Get_YaxisColor2()));
        surface_pixel[6]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor1(), Get_YaxisColor3()));
        surface_pixel[7]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor2(), Get_YaxisColor3()));
        surface_pixel[8]= ColorRecog(rawBitmap.getPixel(Get_XaxisColor3(), Get_YaxisColor3()));
   /*
        surface_pixel[0]= ColorRecog(Get_AverColor (Get_XaxisColor1(),Get_YaxisColor1()));
        surface_pixel[1]= ColorRecog(Get_AverColor (Get_XaxisColor2(),Get_YaxisColor1()));
        surface_pixel[2]= ColorRecog(Get_AverColor (Get_XaxisColor3(),Get_YaxisColor1()));
        surface_pixel[3]= ColorRecog(Get_AverColor (Get_XaxisColor1(),Get_YaxisColor2()));
        surface_pixel[4]= ColorRecog(Get_AverColor (Get_XaxisColor2(),Get_YaxisColor2()));
        surface_pixel[5]= ColorRecog(Get_AverColor (Get_XaxisColor3(),Get_YaxisColor2()));
        surface_pixel[6]= ColorRecog(Get_AverColor (Get_XaxisColor1(),Get_YaxisColor3()));
        surface_pixel[7]= ColorRecog(Get_AverColor (Get_XaxisColor2(),Get_YaxisColor3()));
        surface_pixel[8]= ColorRecog(Get_AverColor (Get_XaxisColor3(),Get_YaxisColor3()));
    */
        switch (filename)
        {
            case 1:
            {
                Adjust_ColorOrder1(surface_pixel);
            }break;

            case 2:
            {
                Adjust_ColorOrder2(surface_pixel);
            }break;

            case 3:
            {
                Adjust_ColorOrder3(surface_pixel);
            }break;

            case 4:
            {
                Adjust_ColorOrder4(surface_pixel);
            }break;

            case 5:
            {
                Adjust_ColorOrder5(surface_pixel);
            }break;

            case 6:
            {
            }break;

        }

    }

    //由于进行颜色识别和魔方复原魔方的状态发生了改变，所以每一面对应的颜色块也要发生改变
    public void Adjust_ColorOrder1 (String surface_pixel[])
    {


        for(int i=0;i<surface_pixel.length;i++)
        {
            face_pixel[i]=surface_pixel[i];
        }
        surface_pixel[0]=face_pixel[6];
        surface_pixel[1]=face_pixel[3];
        surface_pixel[2]=face_pixel[0];
        surface_pixel[3]=face_pixel[7];
        surface_pixel[4]=face_pixel[4];
        surface_pixel[5]=face_pixel[1];
        surface_pixel[6]=face_pixel[8];
        surface_pixel[7]=face_pixel[5];
        surface_pixel[8]=face_pixel[2];


    }

    public void Adjust_ColorOrder2 (String surface_pixel[])
    {


        for(int i=0;i<surface_pixel.length;i++)
        {
            face_pixel[i]=surface_pixel[i];
        }
        surface_pixel[0]=face_pixel[2];
        surface_pixel[1]=face_pixel[5];
        surface_pixel[2]=face_pixel[8];
        surface_pixel[3]=face_pixel[1];
        surface_pixel[4]=face_pixel[4];
        surface_pixel[5]=face_pixel[7];
        surface_pixel[6]=face_pixel[0];
        surface_pixel[7]=face_pixel[3];
        surface_pixel[8]=face_pixel[6];

    }

    public void Adjust_ColorOrder3 (String surface_pixel[])
    {

        for(int i=0;i<surface_pixel.length;i++)
        {
            face_pixel[i]=surface_pixel[i];
        }
        surface_pixel[0]=face_pixel[2];
        surface_pixel[1]=face_pixel[5];
        surface_pixel[2]=face_pixel[8];
        surface_pixel[3]=face_pixel[1];
        surface_pixel[4]=face_pixel[4];
        surface_pixel[5]=face_pixel[7];
        surface_pixel[6]=face_pixel[0];
        surface_pixel[7]=face_pixel[3];
        surface_pixel[8]=face_pixel[6];



    }

    public void Adjust_ColorOrder4 (String surface_pixel[])
    {

        for(int i=0;i<surface_pixel.length;i++)
        {
            face_pixel[i]=surface_pixel[i];
        }
        surface_pixel[0]=face_pixel[2];
        surface_pixel[1]=face_pixel[5];
        surface_pixel[2]=face_pixel[8];
        surface_pixel[3]=face_pixel[1];
        surface_pixel[4]=face_pixel[4];
        surface_pixel[5]=face_pixel[7];
        surface_pixel[6]=face_pixel[0];
        surface_pixel[7]=face_pixel[3];
        surface_pixel[8]=face_pixel[6];


    }

    public void Adjust_ColorOrder5 (String surface_pixel[])
    {

        for(int i=0;i<surface_pixel.length;i++)
        {
            face_pixel[i]=surface_pixel[i];
        }

        surface_pixel[0]=face_pixel[8];
        surface_pixel[1]=face_pixel[7];
        surface_pixel[2]=face_pixel[6];
        surface_pixel[3]=face_pixel[5];
        surface_pixel[4]=face_pixel[4];
        surface_pixel[5]=face_pixel[3];
        surface_pixel[6]=face_pixel[2];
        surface_pixel[7]=face_pixel[1];
        surface_pixel[8]=face_pixel[0];

    }


    private String Get_UpColor()
    {
        return surface1_pixel[4];
    }

    private String Get_DowmColor()
    {
        return surface4_pixel[4];
    }

    private String Get_LeftColor()
    {
        return surface5_pixel[4];
    }

    private String Get_RightColor()
    {
        return surface2_pixel[4];
    }

    private String Get_FrontColor()
    {
        return surface3_pixel[4];
    }

    private String Get_BackColor()
    {
        return surface6_pixel[4];
    }

    //获取魔方全部颜色状态
    public String Get_RubikTotalColor()
    {
        String rubiktotalcolor="";

        Get_Photocolor(1);
        Get_Photocolor(2);
        Get_Photocolor(3);
        Get_Photocolor(4);
        Get_Photocolor(5);
        Get_Photocolor(6);
        Get_RubikTotalOrgiColor();
        Change_Color(surface1_pixel,surface1_color);
        Change_Color(surface2_pixel,surface2_color);
        Change_Color(surface3_pixel,surface3_color);
        Change_Color(surface4_pixel,surface4_color);
        Change_Color(surface5_pixel,surface5_color);
        Change_Color(surface6_pixel,surface6_color);

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface1_color[i];
        }

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface2_color[i];
        }

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface3_color[i];
        }

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface4_color[i];
        }

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface5_color[i];
        }

        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface6_color[i];
        }

        return rubiktotalcolor;

    }

    //把红、绿、蓝、黄、橙、白形式的颜色变换为U、D、L、R、F、B形式的颜色
    public void Change_Color(String [] surface_pixel,String [] surface_color)
    {
        final String u_color=Get_UpColor();
        final String d_color=Get_DowmColor();
        final String l_color=Get_LeftColor();
        final String r_color=Get_RightColor();
        final String f_color=Get_FrontColor();
        final String b_color=Get_BackColor();

        for(int i=0;i<surface_pixel.length;i++)
        {
          if(surface_pixel[i]==u_color)
          {
              surface_color[i]="U";
          }
          else if(surface_pixel[i]==d_color)
          {
              surface_color[i]="D";
          }
          else if(surface_pixel[i]==l_color)
          {
              surface_color[i]="L";
          }
          else if(surface_pixel[i]==r_color)
          {
              surface_color[i]="R";
          }
          else if(surface_pixel[i]==f_color)
          {
              surface_color[i]="F";
          }
          else if(surface_pixel[i]==b_color)
          {
              surface_color[i]="B";
          }


        }
    }

    //获取红、绿、蓝、黄、橙、白形式的54种颜色，放在rubiktotalcolor中
    public void Get_RubikTotalOrgiColor()
    {
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface1_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface2_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface3_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface4_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface5_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";
        for(int i=0;i<9;i++)
        {
            rubiktotalcolor+=surface6_pixel[i]+" ";
        }
        rubiktotalcolor+="\r\n";

    }

    //获取特定像素点的平均颜色值
    public  int Get_AverColor (int x_position,int y_position)
    {
        long color_sum=0;
        double sum_color;
        for(int x_bias=0;x_bias<3;x_bias++)
        {
                for(int y_bias=0;y_bias<3;y_bias++)
                {
                    color_sum+=rawBitmap.getPixel(x_position+x_bias, y_position+y_bias);
                }
        }
        sum_color=color_sum/9;
        return ((int)sum_color);
    }

}