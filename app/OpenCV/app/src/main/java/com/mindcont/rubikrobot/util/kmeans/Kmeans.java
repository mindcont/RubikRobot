package com.mindcont.rubikrobot.util.kmeans;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author mindcont
 * @version V1.0
 * @Title: git
 * @Package com.mindcont.rubikrobot.util
 * @Description: k均值算法工具类
 * @date 2017/3/3:15:06
 */

public class Kmeans {
    // 输入数据文件地址
    private String filePath;
    // 分类类别个数
    private int classNum;
    // 类名称
    private ArrayList<String> classNames;
    // 聚类坐标点
    private ArrayList<Point> classPoints;
    // 所有的数据左边点
    private ArrayList<Point> totalPoints;

    private Point tempPoint;//临时点坐标
    private double[] clusterPoint = new double[4]; //簇中心点坐标 x= clusterPoint[0] y= clusterPoint[1]
    private double[] getMaxClusterDistancePoint = new double[2];

    /**
     * K均值聚类算法实现
     *
     * @param dataArray n个坐标点
     * @param classNum  分类类别个数
     * @return n个坐标点的中心点坐标，即该点离已知n点的距离和最小
     */
    public double[] getClusterPoint(ArrayList<String[]> dataArray, int classNum) {

        double tempX = 0;
        double tempY = 0;
        int count = 0;
        double error = Integer.MAX_VALUE;

        classPoints = new ArrayList<>();
        totalPoints = new ArrayList<>();
        classNames = new ArrayList<>();

        //根据 classNum 初始簇点集合
        for (int i = 0, j = 1; i < dataArray.size(); i++) {
            if (j <= classNum) {
                classPoints.add(new Point(dataArray.get(i)[0],
                        dataArray.get(i)[1], j + ""));
                classNames.add(i + "");
                j++;
            }
            totalPoints
                    .add(new Point(dataArray.get(i)[0], dataArray.get(i)[1]));
        }

        // k-means
        while (error > 0.01 * classNum) {
            for (Point p1 : totalPoints) {
                // 将所有的测试坐标点就近分类
                for (Point p2 : classPoints) {
                    p2.computerDistance(p1);
                }
                Collections.sort(classPoints);

                // 取出p1离类坐标点最近的那个点
                p1.setClassName(classPoints.get(0).getClassName());
            }

            error = 0;
            // 按照均值重新划分聚类中心点
            for (Point p1 : classPoints) {
                count = 0;
                tempX = 0;
                tempY = 0;
                for (Point p : totalPoints) {
                    if (p.getClassName().equals(p1.getClassName())) {
                        count++;
                        tempX += p.getX();
                        tempY += p.getY();
                    }
                }
                tempX /= count;
                tempY /= count;

                error += Math.abs((tempX - p1.getX()));
                error += Math.abs((tempY - p1.getY()));
                // 计算均值
                p1.setX(tempX);
                p1.setY(tempY);

            }

            for (int i = 0; i < classPoints.size(); i++) {
                tempPoint = classPoints.get(i);
                System.out.println(MessageFormat.format("聚类中心点{0}，x={1},y={2}",
                        (i + 1), tempPoint.getX(), tempPoint.getY()));
            }
            System.out.println("----------");
        }

        System.out.println("结果值收敛");
        for (int i = 0; i < classPoints.size(); i++) {
            tempPoint = classPoints.get(i);
            System.out.println(MessageFormat.format("聚类中心点{0}，x={1},y={2}",
                    (i + 1), tempPoint.getX(), tempPoint.getY()));

            System.out.print("结果值收敛" + tempPoint.getClassName());

//            System.out.println("结果值收敛",tempPoint.getClassName());

            //返回结果处理部分,只能处理 1 个或2个聚类中心点的情况
            clusterPoint[2 * i] = tempPoint.getX();
            clusterPoint[2 * i + 1] = tempPoint.getY();
        }

//        clusterPoint[0] = tempPoint.getX();
//        clusterPoint[1] = tempPoint.getY();
        return clusterPoint;
    }

    /**
     * 获取点集中相对中心点最远距离点
     *
     * @param clusterPoint 已知点集中心点
     * @return
     */
    public double[] getMaxClusterDistance(double[] clusterPoint) {

        double tempdistance, maxDistance = 0;
//        Point tempPoint = new Point();

        Point centerPoint = new Point(clusterPoint[0], clusterPoint[1]);

        //对于点集中所有的点，依次计算与点集中心的 曼哈顿距离
        for (Point p1 : totalPoints) {
            tempdistance = p1.manhattanDistance(p1, centerPoint);
            if (tempdistance > maxDistance) {
                maxDistance = tempdistance;
                tempPoint = p1;
            }

        }

        System.out.println(MessageFormat.format("相对中心点最远距离点",
                tempPoint.getX(), tempPoint.getY()));

        getMaxClusterDistancePoint[0] = tempPoint.getX();
        getMaxClusterDistancePoint[1] = tempPoint.getY();
        return getMaxClusterDistancePoint;
    }

}
