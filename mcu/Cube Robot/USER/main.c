/*程序描述：魔方机器人下位机控制程序
 *主控芯片：STM32F103ZET6,flash:512kb, ram:64kb
 *备    注：MG995舵机度数增大的方向为逆时针
 *当速度值为500时，解算示例为#B'R2U2B2DU2F2UB2R2U'L2U'L'U'FRB2RD'!，运行时间为2分14秒，当速度值为300时，运行时间为1分20秒         
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
	  Exti2_Init();
    TIM3_Int_Init(10,71);	 
		TIM_Cmd(TIM3, DISABLE);     /*接收到指令之后，再打开TIM3*/		
	  while(!rece_flag);
	 
		while(1)
		{                                        /*警告：外部中断ABCDEF的顺序不要颠倒*/
						
				if(rece_flag==1)
					{
						
						  TIM_Cmd(TIM3, DISABLE);         /*先关闭TIM3，避免全局变量被修改*/	
						  switch(rece_string[0])
							{
									case 'Z':/*外部中断到来，转到第一个需要拍照的面*/
									{
										PicArray_ToBufferArray(firpic_position,2);
									}break;
									case 'A':/*串口中断到来，转到第二个拍照的面*/
									{
										PicArray_ToBufferArray(secpic_position,0);
									}break;
									case 'B':/*串口中断到来，转到第三个拍照的面*/
									{
										PicArray_ToBufferArray(thirpic_position,2);
										
									}break;
									
									case 'C':/*串口中断到来，转到第四个拍照的面*/
									{
										PicArray_ToBufferArray(fourpic_position,0);
										
									}break;
									case 'D':/*串口中断到来，转到第五个拍照的面*/
									{
										PicArray_ToBufferArray(fifpic_position,3);
										
									}break;
									
									case 'E':/*串口中断到来，转到第六个拍照的面*/
									{
										PicArray_ToBufferArray(sixpic_position,0);
										
									}break;
									case 'F':/*串口中断到来，拍照完之后回到初始位置，等待解算*/
									{
										PicArray_ToBufferArray(retuinit_position,4);
										
									}break;
									case'#':/*执行解算魔方*/
									{
										SolvecubeArray_ToBufferArray();
									}break;
									default:
									{
									
							    }break;

							}
					
				    change();		
						TIM_Cmd(TIM3, ENABLE);     /*接收到指令之后，再打开TIM3*/					 			
						rece_flag=0;
					
					}
					
				if (flag_vpwm==1)	  
					{
						vpwm();				             /*插补角度*/
						flag_vpwm=0;
					}	
					
		}
		
}		
		
		
		
		
		
		
	   
 
