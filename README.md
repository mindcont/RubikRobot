## RubikRobot 魔方机器人
本项目利用安卓app配合stm32单片机驱动舵机对任意正确状态下三阶魔方进行自动复原。
值得注意的是，对于app的编写，我们借鉴[DigDream](https://github.com/DigDream/RubiksCubeRobot)的实现方式，Android Studio工程见app文件夹。

### 预览
<img src="https://github.com/mindcont/RubikRobot/blob/master/picture/20160306.jpg" alt="在全志杯决赛上的照片" width="512">

### 工程组织结构
文件夹名 | 作用 |
---------|----------|
app | android客户端 |
mcu | 舵机控制、蓝牙通信 |
picture | 图片 |
reference | Two-Phase-Algorithm 魔方解算算法|

### 项目流程
手机（android 4.4 及以上）运行APP (主要实现色块的识别,解魔方算法) --》蓝牙向stm32系列单片机开发板通信--》驱动舵机--》旋转魔方--》复原完成

### 视频
[解魔方机器人](http://player.youku.com/embed/XMTQ5MTU5NzM4OA==)

### 致谢
在此，我们特别感谢[DigDream](https://github.com/DigDream/RubiksCubeRobot)、[原子哥](http://www.openedv.com/)和其他一些致力于开放源代码的朋友们。

### License
采用[知识共享 署名-非商业性使用-相同方式共享 4.0 国际 许可](http://creativecommons.org/licenses/by-nc-sa/4.0/)协议进行许可。
