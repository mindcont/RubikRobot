/*  文件名：motor.c
 *文件描述：主要存放舵机数据线引脚GPIO的初始化和舵机速度控制插补算法
 *备    注：change函数n为速度值，经过实际测试，发现当n为50或者更小时，动作会出现错乱
 *		      因为舵机转动有一定的机械滞后性，转动需要一定的时间，所以n值不能太小，测试
 *			    中发现当n值为500或者1000时，舵机转动良好
 */
#include "stm32f10x.h"
#include "motor.h"
#include "sys.h"
#include "instruction.h"
#include "movement.h"
#include "usart.h"
#include "exti.h"
#include "timer.h"

int point_now=-1;    /*正在执行的数组*/
u8 point_aim=0;	     /*正在执行数组的下一行数组*/
u16 n=300;		       /*设定的插补次数*/
u16 motor_speed=300;
u16 m;		           /*当前的插补次数*/
double dp;
double dp0[8];	  /*插补增量*/
u16 pwm[8]={1490,1455,1530,1480,1950,1890,1910,830};



u16 pos[500][8];


/*
 *函 数 名:change()  
 *功能描述:初位置末尾置更替
 *		   有效的数据是插补增量，和插补次数，知道这两个量，和当前初位置即可
 *输    入：无
 *输    出：无
 *调    用：被 vpwm()调用		 
 *备    注：要实现匀角速度，须在每一次进入此函数时对8个舵机赋予不同的插补量，
 */	
 void change(void)
{	
	 u8 s;                            /*舵机的个数*/
	 n=motor_speed;     		          /*速度值*/
	 m=0;				  
   
   if(point_aim==lines_num)	        /*（pos[][]的行数-1）*/
   {
		 
				TIM_Cmd(TIM3, DISABLE);  /*关掉TIMx*/					 
				point_aim=0;
				point_now=-1;
				switch(movement_tag)
				 {
							 case 'Z':
							 {
								  USART_SendChar('1');
								  //TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
								 
							 }break;
							 
							 case 'A':
							 {
								  USART_SendChar('2');
								  //TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
							 }break;
						 
							 case 'B':
							 {
								  USART_SendChar('3');
								 // TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
							 }break;
						 
							 case 'C':
							 {
								 USART_SendChar('4');
								 // TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
							 }break;
						 
							 case 'D':
							 {
								  USART_SendChar('5');
								 // TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
							 }break;
						 
							 case 'E':
							 {
								  USART_SendChar('6');
								 // TIM4_Set_Time(60000); /*定时1S*/
								  TIM_Cmd(TIM4, ENABLE);/*开启定时器*/
							 }break;
	
							 default:
							 {
								 
							 }break;
				 
				 }			 
   }
	 
   else
   {
			point_aim++;
			point_now++;	
			for(s=0;s<8;s++)			   /*计算新一行数组的插补增量*/
			{

					if(pos[point_aim][s]>pos[point_now][s])
						{
								dp=pos[point_aim][s]-pos[point_now][s];
								dp0[s]=dp/n;
						}
					if(pos[point_aim][s]<=pos[point_now][s])
						{
							dp=pos[point_now][s]-pos[point_aim][s];
							dp0[s]=dp/n;
							dp0[s]=-dp0[s];
						}
			 }		 
		 
   }
	 
}


/*
 *函 数 名：pwm[]数组更新函数  
 *功能描述：数据插补，插补时间间隔为20ms/16，由Timer1控制，使舵机平滑实现速度控制
 *		  另一个功能是执行完一行后去更新下一行数据，即调用change()
 *输    入：无 
 *输    出：无
 *调    用：被main.c调用
 *备    注：
 */	
void vpwm(void)		 
{				                                                                                                                                                                                                                                                                                                                                                                                                 
			u8 j=0;
			u8 how=0;
			static u8 flag_how;
			static u8 flag_Tover;

			m++;							      /*用来累加插补过的次数*/
			if(m==n)						    /*n是本行作业要插补的总次的执行数*/
					flag_Tover=1;				/*一行数据时间已经完成*/
			
			for(j=0;j<8;j++)
			{
					if(((pwm[j]-pos[point_aim][j])<5)||((pos[point_aim][j]-pwm[j])<5))	
					{						   	    /*检测靠近终点位置*/
						 how++;				   	/*是，则累加一个*/
						 pwm[j]=pos[point_aim][j];/*并且直接过度到终点位置*/
					}	
					else						    /*不靠近终点，继续插补*/
						pwm[j]=pos[point_now][j]+m*dp0[j];

			} 
			if(how==8)
				flag_how=1;	  				/*舵机都到达终点*/
			 how=0; 

			if((flag_Tover==1)&&(flag_how==1))
			{								       /*从插补次数，和脉宽宽度两方面都到达终点，本作业行完成*/
				 flag_Tover=0;
				 flag_how=0;
				 change();			 	   /*行完成标志置*/
			}
			return;
			
}


/* 功能描述：初始化和信号线连接的GPIO引脚
 * 备    注：PA.0--------PA.7
 */
void MotorPin_Init(void)
{
	 GPIO_InitTypeDef  GPIO_InitStructure;
	 	
	 RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOA, ENABLE);   /*使能PinA端口时钟*/
		
	 GPIO_InitStructure.GPIO_Pin = GPIO_Pin_0|GPIO_Pin_1|GPIO_Pin_2|GPIO_Pin_3|GPIO_Pin_4|GPIO_Pin_5|GPIO_Pin_6|GPIO_Pin_7;	//PA.0---PA.7 端口配置
	 GPIO_InitStructure.GPIO_Mode = GPIO_Mode_Out_PP; 		   /*推挽输出*/
	 GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz;		   /*IO口速度为50MHz*/
	
	 GPIO_Init(GPIOA, &GPIO_InitStructure);					         /*根据设定参数初始化GPIOA*/
	 GPIO_ResetBits(GPIOA,GPIO_Pin_0|GPIO_Pin_1|GPIO_Pin_2|GPIO_Pin_3|GPIO_Pin_4|GPIO_Pin_5|GPIO_Pin_6|GPIO_Pin_7);						 //PB.5 输出高
	
	
}

