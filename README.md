# RubikRobot 魔方机器人
本项目试图利用方糖电子全志A20双核开发板“cubieboard2双卡版”搭载安卓4.2系统运行app配合stm32单片机用来驱动舵机进行任意正确状态下三阶魔方的自动复原工作。
在项目初期，我们在[DigDream](https://github.com/DigDream/RubiksCubeRobot)下进行了仔细的研读和借用，并修改了部分文件以期适合我们的项目需求，旧版本详见reference文件夹。
项目后期，由于DigDream项目过于复杂我们不得已又对app部分进行了重新编写，详见app文件夹。

## 工程组织结构
文件夹名 | 作用 |
---------|----------|
app | android客户端 |
mcu | 舵机控制、蓝牙通信 |
picture | 图片 |
reference | 旧版app |

## 项目流程
硬件(cubieboard2双卡版)--》适配安卓4.2系统--》运行APP (主要实现色块的识别,解魔方算法)
并利用蓝牙向stm32系列单片机开发板通信--》驱动舵机--》旋转魔方--》复原完成

## 视频
[解魔方机器人](http://player.youku.com/embed/XMTQ5MTU5NzM4OA==)

##致谢
在此，我们特别感谢[DigDream](https://github.com/DigDream/RubiksCubeRobot)和其他一些致力于开放源代码的朋友们。

## License
采用[知识共享 署名-非商业性使用-相同方式共享 4.0 国际 许可](http://creativecommons.org/licenses/by-nc-sa/4.0/)协议进行许可。