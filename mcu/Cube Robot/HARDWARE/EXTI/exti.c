#include "stm32f10x.h"
#include "exti.h"
#include "key.h"
#include "led.h"
#include "delay.h"
#include "movement.h"
#include "usart.h"
#include "motor.h"

u8 exti_flag=0;
u8 movement_tag='Q';

/*外部中断2初始化*/
void Exti_Init(void)
{
	 	EXTI_InitTypeDef EXTI_InitStructure;
	  KEY_Init();   	                                            /*引脚GPIO初始化*/
	
    RCC_APB2PeriphClockCmd(RCC_APB2Periph_AFIO,ENABLE);	        /*使能复用功能时钟*/
	
	  GPIO_EXTILineConfig(GPIO_PortSourceGPIOE,GPIO_PinSource2);  /*打开外部中断*/
	  EXTI_InitStructure.EXTI_Line=EXTI_Line2;
  	EXTI_InitStructure.EXTI_Mode = EXTI_Mode_Interrupt;	
  	EXTI_InitStructure.EXTI_Trigger = EXTI_Trigger_Falling;     /*下降沿触发*/
  	EXTI_InitStructure.EXTI_LineCmd = ENABLE;
  	EXTI_Init(&EXTI_InitStructure);	

		GPIO_EXTILineConfig(GPIO_PortSourceGPIOE,GPIO_PinSource3); 
	  EXTI_InitStructure.EXTI_Line=EXTI_Line3;
	  EXTI_Init(&EXTI_InitStructure);	

	  GPIO_EXTILineConfig(GPIO_PortSourceGPIOE,GPIO_PinSource4); 
	  EXTI_InitStructure.EXTI_Line=EXTI_Line4;
	  EXTI_Init(&EXTI_InitStructure);	
	
    Ex_NVIC_Config();                                        /*外部中断优先级设置*/
	
}

/*外部中断优先级设置*/
void Ex_NVIC_Config(void)
{
	  NVIC_InitTypeDef NVIC_InitStructure;
	  NVIC_InitStructure.NVIC_IRQChannel = EXTI2_IRQn;			
  	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;	    /*抢占优先级2*/ 
  	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;					  /*子优先级2*/
  	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;								/*使能外部中断通道*/
  	NVIC_Init(&NVIC_InitStructure);	

		NVIC_InitStructure.NVIC_IRQChannel = EXTI3_IRQn;			
  	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;	    /*抢占优先级2*/ 
  	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;					  /*子优先级2*/
  	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;								/*使能外部中断通道*/
  	NVIC_Init(&NVIC_InitStructure);	
	
	  NVIC_InitStructure.NVIC_IRQChannel = EXTI4_IRQn;			
  	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;	    /*抢占优先级2*/ 
  	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;					  /*子优先级2*/
  	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;								/*使能外部中断通道*/
  	NVIC_Init(&NVIC_InitStructure);	
	
}




/*外部中断2中断服务程序
 *转到第一个需要拍照的面*/
void EXTI2_IRQHandler(void)
{
	delay_ms(10);
	
	if(KEY2==0)
	{
		LED0=!LED0;		
		PicArray_ToBufferArray(firpic_position,3);
		motor_speed=250;
		change();		
		TIM_Cmd(TIM3, ENABLE);     /*打开TIM3*/	
		movement_tag='Z';     		
	}
	
	EXTI_ClearITPendingBit(EXTI_Line2);  /*清除LINE2上的中断标志位,经过测试发现，把这行代码放在if后面代码执行比较稳定*/ 
	
}


/*外部中断3中断服务程序
 *回到初始转动魔方的位置*/
void EXTI4_IRQHandler(void)
{
	delay_ms(10);
	
	if(KEY0==0)
	{
		LED1=!LED1;		
		Init_MotorMovement();
		motor_speed=250;
		change();		
		TIM_Cmd(TIM3, ENABLE);
		movement_tag='Y';     				
	}	
	EXTI_ClearITPendingBit(EXTI_Line4);  /*清除LINE4上的中断标志位,经过测试发现，把这行代码放在if后面代码执行比较稳定*/ 
	
}


/*外部中断3中断服务程序
 *舵机闭合*/
void EXTI3_IRQHandler(void)
{
	delay_ms(10);
	
	if(KEY1==0)
	{
		PicArray_ToBufferArray(firpic_position,1);
		motor_speed=500;
		change();		
		TIM_Cmd(TIM3, ENABLE);     /*打开TIM3*/	
	
	}
	EXTI_ClearITPendingBit(EXTI_Line3);  /*清除LINE3上的中断标志位,经过测试发现，把这行代码放在if后面代码执行比较稳定*/ 
	
}






