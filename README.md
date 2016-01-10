# RobikRobot 魔方机器人
本项目试图利用方糖电子全志A20双核开发板“cubieboard2双卡版”搭载安卓4.2系统配合stm32单片机用来驱动舵机进行任意正确状态下三阶魔方的自动复原工作。
我们在DigDream<https://github.com/DigDream/RubiksCubeRobot>下进行了仔细的研读和借用，并修改了部分文件以适合我们的项目需求。

在此，我们特别感谢DigDream和其他一些致力于开放源代码的朋友们。

## 工程组织结构
文件夹名 | 实现功能 | 贡献作者 | 出处
---------|----------|----------|------------------------------|
android-ui | 实现控件的动态效果 | markushi |https://github.com/markushi/android-ui
MaterialTabsModule | 实现自定义选项卡 | neokree |https://github.com/neokree/MaterialTabs
openCVLibrary2410 | OpenCV用于安卓的开源计算机视觉库 | OpenCV |http://opencv.org
app | 页面布局、识别、算法 | 修改自DigDream |https://github.com/mindcont/RobikRobot

## 项目流程
硬件(cubieboard2双卡版)--》适配安卓4.2系统--》运行APP (主要实现色块的识别,解魔方算法)
并利用蓝牙向stm32系列单片机开发板通信--》驱动舵机--》旋转魔方