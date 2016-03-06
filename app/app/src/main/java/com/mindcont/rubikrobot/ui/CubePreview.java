package com.mindcont.rubikrobot.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.mindcont.rubikrobot.R;
import com.mindcont.rubikrobot.camera.ColorRecognition;

/**
 * Created by fenxi on 2016/2/21.
 */
public class CubePreview extends Activity {

    private static final String TAG = "ARCS::CubePreview";
    //测试用，魔方定义字符串。理应由识别后结果得到
    // 扫描顺序 上(白) 右（绿） 前（黄）下（橘红） 左（红） 后（蓝）
    public String cubeDefinitionStringTest = "URRRUBULFLDFURLBLDRBUFFBURRLBDLDFDFLBUFDLRFUBDDRDBFBUL";
    //ColorRecognition
    ColorRecognition colorRecognition = new ColorRecognition();
    private int color;

    /**
     * Computes the solver string for a given cube.
     * <p/>
     * facelets
     * is the cube definition string format.<br>
     * The names of the facelet positions of the cube:
     * <pre>
     *             |************|
     *             |*U1**U2**U3*|
     *             |************|
     *             |*U4**U5**U6*|
     *             |************|
     *             |*U7**U8**U9*|
     *             |************|
     * ************|************|************|************|
     * *L1**L2**L3*|*F1**F2**F3*|*R1**R2**F3*|*B1**B2**B3*|
     * ************|************|************|************|
     * *L4**L5**L6*|*F4**F5**F6*|*R4**R5**R6*|*B4**B5**B6*|
     * ************|************|************|************|
     * *L7**L8**L9*|*F7**F8**F9*|*R7**R8**R9*|*B7**B8**B9*|
     * ************|************|************|************|
     *             |************|
     *             |*D1**D2**D3*|
     *             |************|
     *             |*D4**D5**D6*|
     *             |************|
     *             |*D7**D8**D9*|
     *             |************|
     * </pre>
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cube_preview);

//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.back);
//        Button buttonRandom = new Button(this);
//
        //构造 .solver.search() 中的方法，传入魔方定义字符串
//        Search search = new Search();
//        String solution = search.solution(colorRecognition.getRubikTotalColor(), 21, 10000, 0, 0x0);
//        //对结果的处理
//        processResult(solution);

//        buttonRandom.setText(solution);
//        buttonRandom.setBackgroundColor(getResources().getColor(R.color.red));
//        linearLayout.addView(buttonRandom);

        //up facelet
        View U1 = this.findViewById(R.id.U1);
        View U2 = this.findViewById(R.id.U2);
        View U3 = this.findViewById(R.id.U3);
        View U4 = this.findViewById(R.id.U4);
        View U5 = this.findViewById(R.id.U5);
        View U6 = this.findViewById(R.id.U6);
        View U7 = this.findViewById(R.id.U7);
        View U8 = this.findViewById(R.id.U8);
        View U9 = this.findViewById(R.id.U9);

        //left facelet
        View L1 = this.findViewById(R.id.L1);
        View L2 = this.findViewById(R.id.L2);
        View L3 = this.findViewById(R.id.L3);
        View L4 = this.findViewById(R.id.L4);
        View L5 = this.findViewById(R.id.L5);
        View L6 = this.findViewById(R.id.L6);
        View L7 = this.findViewById(R.id.L7);
        View L8 = this.findViewById(R.id.L8);
        View L9 = this.findViewById(R.id.L9);

        //front facelet
        View F1 = this.findViewById(R.id.F1);
        View F2 = this.findViewById(R.id.F2);
        View F3 = this.findViewById(R.id.F3);
        View F4 = this.findViewById(R.id.F4);
        View F5 = this.findViewById(R.id.F5);
        View F6 = this.findViewById(R.id.F6);
        View F7 = this.findViewById(R.id.F7);
        View F8 = this.findViewById(R.id.F8);
        View F9 = this.findViewById(R.id.F9);

        //right facelet
        View R1 = this.findViewById(R.id.R1);
        View R2 = this.findViewById(R.id.R2);
        View R3 = this.findViewById(R.id.R3);
        View R4 = this.findViewById(R.id.R4);
        View R5 = this.findViewById(R.id.R5);
        View R6 = this.findViewById(R.id.R6);
        View R7 = this.findViewById(R.id.R7);
        View R8 = this.findViewById(R.id.R8);
        View R9 = this.findViewById(R.id.R9);

        //back facelet
        View B1 = this.findViewById(R.id.B1);
        View B2 = this.findViewById(R.id.B2);
        View B3 = this.findViewById(R.id.B3);
        View B4 = this.findViewById(R.id.B4);
        View B5 = this.findViewById(R.id.B5);
        View B6 = this.findViewById(R.id.B6);
        View B7 = this.findViewById(R.id.B7);
        View B8 = this.findViewById(R.id.B8);
        View B9 = this.findViewById(R.id.B9);

        //down facelet
        View D1 = this.findViewById(R.id.D1);
        View D2 = this.findViewById(R.id.D2);
        View D3 = this.findViewById(R.id.D3);
        View D4 = this.findViewById(R.id.D4);
        View D5 = this.findViewById(R.id.D5);
        View D6 = this.findViewById(R.id.D6);
        View D7 = this.findViewById(R.id.D7);
        View D8 = this.findViewById(R.id.D8);
        View D9 = this.findViewById(R.id.D9);

        /**
         * 初始化显示视图，设计到魔方坐标系的确定
         *   we read the faces in the following order. a face's color is defined by its middle facelet.
         *  1. face: white - up
         *  2. face: green - front
         *  3. face: yellow - down
         *  4. face: orange - left
         *  5. face: red - right
         *  6. face: blue - back
         */

        U5.setBackgroundColor(getResources().getColor(R.color.white));
        F5.setBackgroundColor(getResources().getColor(R.color.green));
        D5.setBackgroundColor(getResources().getColor(R.color.yellow));
        L5.setBackgroundColor(getResources().getColor(R.color.orange));
        R5.setBackgroundColor(getResources().getColor(R.color.red));
        B5.setBackgroundColor(getResources().getColor(R.color.blue));

        //依次调用 getFaceletColor 方法 设置 6*9-6=48个魔方小块
        U1.setBackgroundColor(getFaceletColor(0));
        U2.setBackgroundColor(getFaceletColor(1));
        U3.setBackgroundColor(getFaceletColor(2));
        U4.setBackgroundColor(getFaceletColor(3));
//        U5.setBackgroundColor(getFaceletColor(4)); //上面中间角块，已初始化定义且不因魔方变化而改变，无需设置
        U6.setBackgroundColor(getFaceletColor(5));
        U7.setBackgroundColor(getFaceletColor(6));
        U8.setBackgroundColor(getFaceletColor(7));
        U9.setBackgroundColor(getFaceletColor(8));

        R1.setBackgroundColor(getFaceletColor(9));
        R2.setBackgroundColor(getFaceletColor(10));
        R3.setBackgroundColor(getFaceletColor(11));
        R4.setBackgroundColor(getFaceletColor(12));
//        R5.setBackgroundColor(getFaceletColor(13));
        R6.setBackgroundColor(getFaceletColor(14));
        R7.setBackgroundColor(getFaceletColor(15));
        R8.setBackgroundColor(getFaceletColor(16));
        R9.setBackgroundColor(getFaceletColor(17));

        F1.setBackgroundColor(getFaceletColor(18));
        F2.setBackgroundColor(getFaceletColor(19));
        F3.setBackgroundColor(getFaceletColor(20));
        F4.setBackgroundColor(getFaceletColor(21));
//        F5.setBackgroundColor(getFaceletColor(22));
        F6.setBackgroundColor(getFaceletColor(23));
        F7.setBackgroundColor(getFaceletColor(24));
        F8.setBackgroundColor(getFaceletColor(25));
        F9.setBackgroundColor(getFaceletColor(26));

        D1.setBackgroundColor(getFaceletColor(27));
        D2.setBackgroundColor(getFaceletColor(28));
        D3.setBackgroundColor(getFaceletColor(29));
        D4.setBackgroundColor(getFaceletColor(30));
//        D5.setBackgroundColor(getFaceletColor(31));
        D6.setBackgroundColor(getFaceletColor(32));
        D7.setBackgroundColor(getFaceletColor(33));
        D8.setBackgroundColor(getFaceletColor(34));
        D9.setBackgroundColor(getFaceletColor(35));

        L1.setBackgroundColor(getFaceletColor(36));
        L2.setBackgroundColor(getFaceletColor(37));
        L3.setBackgroundColor(getFaceletColor(38));
        L4.setBackgroundColor(getFaceletColor(39));
//        L5.setBackgroundColor(getFaceletColor(40));
        L6.setBackgroundColor(getFaceletColor(41));
        L7.setBackgroundColor(getFaceletColor(42));
        L8.setBackgroundColor(getFaceletColor(43));
        L9.setBackgroundColor(getFaceletColor(44));

        B1.setBackgroundColor(getFaceletColor(45));
        B2.setBackgroundColor(getFaceletColor(46));
        B3.setBackgroundColor(getFaceletColor(47));
        B4.setBackgroundColor(getFaceletColor(48));
//        B5.setBackgroundColor(getFaceletColor(49));
        B6.setBackgroundColor(getFaceletColor(50));
        B7.setBackgroundColor(getFaceletColor(51));
        B8.setBackgroundColor(getFaceletColor(52));
        B9.setBackgroundColor(getFaceletColor(53));

    }

    /**
     * 根据识别结果得到的魔方定义字符串 cubeDefinitionString，获取对应块的颜色
     * we read the faces in the following order. a face's color is defined by its middle facelet.
     * 1. face: white - up
     * 2. face: green - front
     * 3. face: yellow - down
     * 4. face: orange - left
     * 5. face: red - right
     * 6. face: blue - back
     */

    public int getFaceletColor(int faceletID) {

        //此处 传入识别后得到的魔方颜色定义数组
        switch (colorRecognition.getRubikTotalColor().charAt(faceletID)) {
            case 'U':
                color = getResources().getColor(R.color.white);
                break;
            case 'R':
                color = getResources().getColor(R.color.red);
                break;
            case 'F':
                color = getResources().getColor(R.color.green);
                break;
            case 'D':
                color = getResources().getColor(R.color.yellow);
                break;
            case 'L':
                color = getResources().getColor(R.color.orange);
                break;
            case 'B':
                color = getResources().getColor(R.color.blue);
                break;
        }
        return color;
    }

    /**
     * 对 solver.search()方法的返回结果的处理
     *
     * @param solution
     */
    private void processResult(String solution) {

        // error handling
        if (solution.startsWith("error")) {
            int errorcode = Character.getNumericValue(solution.charAt(solution.length() - 1)) - 1;
            solution = getResources().getStringArray(R.array.errors)[errorcode];
            Toast.makeText(this, solution, Toast.LENGTH_LONG).show();
            return;
        }

        // solution is empty aka cube already solved
        if (solution.isEmpty()) {
            Toast.makeText(this, getString(R.string.cube_is_solved), Toast.LENGTH_LONG).show();
            return;
        }
        //显示复原魔方的步骤
        Toast.makeText(this, solution, Toast.LENGTH_LONG).show();
    }

}
