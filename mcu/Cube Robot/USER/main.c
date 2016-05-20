/*程序描述：魔方机器人下位机控制程序
 *主控芯片：STM32F103ZET6,flash:512kb, ram:64kb
 *软件平台：KEIL5
 *备    注：MG995舵机度数增大的方向为逆时针
 *     
 */
#include "stm32f10x.h"
#include "led.h"
#include "delay.h"
#include "key.h"
#include "sys.h"
#include "usart.h"
#include "timer.h"
#include "motor.h"
#include "movement.h"
#include "instruction.h"
#include "exti.h"
	
 int main(void)
 {	
		delay_init();
		NVIC_Configuration();        /*设置NVIC中断分组2:2位抢占优先级，2位响应优先级*/
		LED_Init();
		MotorPin_Init();             /*舵机初始化信号线引脚*/		     
		USART1_Config();
		Init_TotalArray();           /*计算执行数组*/
	  Exti_Init();
    TIM3_Int_Init(100,100);	 
	  TIM4_Int_Init(10000,7199);   /*定时1S*/
		//TIM_Cmd(TIM3, DISABLE);                        /*接收到指令之后，再打开TIM3*/		
	  //while(!rece_flag);
	 
		while(1)
		{                                               /*警告：外部中断ABCDEF的顺序不要颠倒*/
						
				if(rece_flag==1)
					{
						
						  if(rece_string[0]=='#')
							{
							      TIM_Cmd(TIM3, DISABLE);         /*先关闭TIM3，避免全局变量被修改*/	
										motor_speed=250;
										SolvecubeArray_ToBufferArray();
							}
					
				    change();		
						TIM_Cmd(TIM3, ENABLE);     /*接收到指令之后，再打开TIM3*/					 			
						rece_flag=0;
					
					}
					
				if(flag_vpwm==1)	  
					{
						vpwm();				             /*插补角度*/
						flag_vpwm=0;
					}	
					
		}
		
}		
		
		
		
		
		
		
	   
 
