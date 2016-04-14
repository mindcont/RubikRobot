#include "stm32f10x.h"
#include "exti.h"
#include "key.h"
#include "led.h"
#include "delay.h"
#include "movement.h"
#include "usart.h"

u8 exti_flag=0;

/*外部中断2初始化*/
void Exti2_Init(void)
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
	
    Exti2_NVIC_Config();                                        /*外部中断2优先级设置*/
}

/*外部中断2优先级设置*/
void Exti2_NVIC_Config(void)
{
	  NVIC_InitTypeDef NVIC_InitStructure;
	  NVIC_InitStructure.NVIC_IRQChannel = EXTI2_IRQn;			
  	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;	    /*抢占优先级2*/ 
  	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;					  /*子优先级2*/
  	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;								/*使能外部中断通道*/
  	NVIC_Init(&NVIC_InitStructure);	
}

/*外部中断2中断服务程序*/
void EXTI2_IRQHandler(void)
{
	delay_ms(10);
	
	if(KEY2==0)
	{
		LED0=!LED0;		
    rece_string[0]='Z';		
		rece_flag=1;  	
	}
	
	EXTI_ClearITPendingBit(EXTI_Line2);  /*清除LINE2上的中断标志位,经过测试发现，把这行代码放在if后面代码执行比较稳定*/ 
}


