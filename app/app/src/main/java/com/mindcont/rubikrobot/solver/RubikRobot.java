package com.mindcont.rubikrobot.solver;

/**
 * Created by Administrator on 2016/2/26.
 */
public class RubikRobot {

    public static Search search = new Search();

    public static String changeChars(String solution) {
        String solution2 = solution.replace(" ", "");
        StringBuffer chars = new StringBuffer(solution2);
        chars.insert(0, "#");
        chars.insert(chars.length(), "!");
        return (chars.toString());
    }

    public static String getSolution(String chars) {
        String rubik_solution = search.solution(chars, 21, 100, 0, 0);
        return changeChars(rubik_solution);
    }


//    public static String getSolution(String cubeDefinitionString) {
//
//        String originSolution=search.solution(cubeDefinitionString,21, 10000, 0, 0);
//
//        StringBuffer solution = new StringBuffer(originSolution.replace(" ", ""));
//        solution.insert(0,"#");//帧起始标志位 #
//        solution.insert(originSolution.length(),"!");//帧结束标志位 ！
//        return (solution.toString());
//    }
}
