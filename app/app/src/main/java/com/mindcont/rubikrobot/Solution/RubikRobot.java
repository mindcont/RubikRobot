package com.mindcont.rubikrobot.Solution;

/**
 * Created by Administrator on 2016/2/26.
 */
public class RubikRobot {

    public static Search search=new Search();

    //将魔方复原指令按照自定义协议包装，开始字符为'#',结束字符为'!'
    public static String Change_chars(String solution)
    {
        String solution2=solution.replace(" ","");
        StringBuffer chars=new StringBuffer(solution2);
        chars.insert(0,"#");
        chars.insert(chars.length(),"!");
        return (chars.toString());
    }

    public static String Get_Solution(String chars)
    {
        String rubik_solution=search.solution(chars,21,100,0,0);
        return Change_chars(rubik_solution);
    }



}
