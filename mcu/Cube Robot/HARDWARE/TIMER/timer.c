#include "timer.h"
#include "led.h"
#include "motor.h"
#include "usart.h"
#include "exti.h"
#include "movement.h"

u8 flag_vpwm=0; /*插补标志位*/

/*
 *通用定时器3中断初始化
 *这里时钟选择为APB1的2倍，而APB1为36M
 *arr：自动重装值
 *psc：时钟预分频数
 *这里使用的是定时器3!
 */
void TIM3_Int_Init(u16 arr,u16 psc)
{
  TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
	NVIC_InitTypeDef NVIC_InitStructure;

	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM3, ENABLE); //时钟使能
	
     /*定时器TIM3初始化*/
	TIM_TimeBaseStructure.TIM_Period = arr; //设置在下一个更新事件装入活动的自动重装载寄存器周期的值	
	TIM_TimeBaseStructure.TIM_Prescaler =psc; //设置用来作为TIMx时钟频率除数的预分频值
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1; //设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;  //TIM向上计数模式
	TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure); //根据指定的参数初始化TIMx的时间基数单位
 
	TIM_ITConfig(TIM3,TIM_IT_Update,ENABLE ); //使能指定的TIM3中断,允许更新中断

	/*中断优先级NVIC设置*/
	NVIC_InitStructure.NVIC_IRQChannel = TIM3_IRQn;  //TIM3中断
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;  //先占优先级0级
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 0;  //从优先级3级
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE; //IRQ通道被使能
	NVIC_Init(&NVIC_InitStructure);  //初始化NVIC寄存器


	TIM_Cmd(TIM3, DISABLE);  /*不使能TIMx*/					 
}


/*
 *通用定时器4中断初始化
 *这里时钟选择为APB1的2倍，而APB1为36M
 *arr：自动重装值
 *psc：时钟预分频数
 *这里使用的是定时器4!
 */
void TIM4_Int_Init(u16 arr,u16 psc)
{
  TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
	NVIC_InitTypeDef NVIC_InitStructure;

	RCC_APB1PeriphClockCmd(RCC_APB1Periph_TIM4, ENABLE); //时钟使能
	
	/*定时器TIM4初始化*/
	TIM_TimeBaseStructure.TIM_Period = arr; //设置在下一个更新事件装入活动的自动重装载寄存器周期的值	
	TIM_TimeBaseStructure.TIM_Prescaler =psc; //设置用来作为TIMx时钟频率除数的预分频值
	TIM_TimeBaseStructure.TIM_ClockDivision = TIM_CKD_DIV1; //设置时钟分割:TDTS = Tck_tim
	TIM_TimeBaseStructure.TIM_CounterMode = TIM_CounterMode_Up;  //TIM向上计数模式
	TIM_TimeBaseInit(TIM4, &TIM_TimeBaseStructure); //根据指定的参数初始化TIMx的时间基数单位
 
	TIM_ITConfig(TIM4,TIM_IT_Update,ENABLE ); //使能指定的TIM4中断,允许更新中断

	/*中断优先级NVIC设置*/
	NVIC_InitStructure.NVIC_IRQChannel = TIM4_IRQn;  //TIM4中断
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 0;  //先占优先级0级
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 2;  //从优先级2级
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE; //IRQ通道被使能
	NVIC_Init(&NVIC_InitStructure);  //初始化NVIC寄存器

	TIM_Cmd(TIM4, DISABLE);  //不使能TIMx			
}


/*
 *功能描述：重新设定定时器3的定时时间
 *输    入：arr:自动重装载寄存器周期的值，单位为us
 *输    出：无
 *调    用:	在定时器中断中被调用
 *备    注:	预分频数值设定为71，即计数周期为1us
 */
void TIM3_Set_Time(u16 arr)
{
    TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
    arr--;
  	TIM_TimeBaseStructure.TIM_Period = arr;           /*设置在下一个更新事件装入活动的自动重装载寄存器周期的值*/	
	  TIM_TimeBaseStructure.TIM_Prescaler =71;          /*设置用来作为TIMx时钟频率除数的预分频值*/
    TIM_TimeBaseInit(TIM3, &TIM_TimeBaseStructure);   /*根据指定的参数初始化TIMx的时间基数单位*/
				
}


/*
 *功能描述：重新设定定时器4的定时时间
 *输    入：arr:自动重装载寄存器周期的值，单位为us
 *输    出：无
 *调    用:	在定时器中断中被调用
 *备    注:	预分频数值设定为71，即计数周期为0.1ms
 */
void TIM4_Set_Time(u16 arr)
{
    TIM_TimeBaseInitTypeDef  TIM_TimeBaseStructure;
    arr--;
  	TIM_TimeBaseStructure.TIM_Period = arr;           /*设置在下一个更新事件装入活动的自动重装载寄存器周期的值*/	
	  TIM_TimeBaseStructure.TIM_Prescaler =57199;          /*设置用来作为TIMx时钟频率除数的预分频值*/
    TIM_TimeBaseInit(TIM4, &TIM_TimeBaseStructure);   /*根据指定的参数初始化TIMx的时间基数单位*/			
}


   /*定时器3中断服务程序*/
void TIM3_IRQHandler(void)  
{	    
	static u8 i=1;
	
	if (TIM_GetITStatus(TIM3, TIM_IT_Update) != RESET)  /*检查TIM3更新中断发生与否*/
		{
			flag_vpwm=1; 
			switch(i)	   
			{
					case 1:	  
						{
								PWM1=1;	 /*pwm1变高*/ 					  
								TIM3_Set_Time(pwm[0]);
								flag_vpwm=1;   
						}  break;
			
					case 2:
						{
							PWM1=0; /*pwm1变低*/
							TIM3_Set_Time(2500-pwm[0]);
							flag_vpwm=1;
						}  break;
					case 3:
						{
							PWM2=1; /*pwm2变高*/
							TIM3_Set_Time(pwm[1]);
							flag_vpwm=1;
						}  break;
					case 4:
						{
							PWM2=0; /*pwm2变低*/
								TIM3_Set_Time(2500-pwm[1]);
							flag_vpwm=1;
						}	 break;
					case 5:
						{
							PWM3=1; /*pwm3变高*/
							TIM3_Set_Time(pwm[2]);
							flag_vpwm=1;
						}  break;
					case 6:
						{
							PWM3=0; /*pwm3变低*/
							TIM3_Set_Time(2500-pwm[2]);
							flag_vpwm=1;
						}	break;	  
					case 7:
						{
							PWM4=1; /*pwm4变高*/
							TIM3_Set_Time(pwm[3]);
							flag_vpwm=1;
						}	break;	  
					case 8:
						{
							PWM4=0; /*pwm4变低*/
							TIM3_Set_Time(2500-pwm[3]);
							flag_vpwm=1;
						}	break;	  
					case 9:
						{
							PWM5=1; /*pwm5变高*/
							TIM3_Set_Time(pwm[4]);
							flag_vpwm=1;
						}	break;	  
					case 10:
						{
							PWM5=0; /*pwm5变低*/
							TIM3_Set_Time(2500-pwm[4]);
							flag_vpwm=1;
						}	break;	  
					case 11:
						{
							PWM6=1; /*pwm6变高*/
							TIM3_Set_Time(pwm[5]);
							flag_vpwm=1;
						}	break;	  
					case 12:
						{
							PWM6=0; /*pwm6变低*/
							TIM3_Set_Time(2500-pwm[5]);
							flag_vpwm=1;
						}	break;	
			
					case 13:
						{
							PWM7=1; /*pwm6变高*/
							TIM3_Set_Time(pwm[6]);
							flag_vpwm=1;
						}	break;	  
					case 14:
						{
							PWM7=0; /*pwm7变低*/
							TIM3_Set_Time(2500-pwm[6]);
							flag_vpwm=1;
						}	break;	
			
					case 15:
						{
							PWM8=1; /*pwm8变高*/
							TIM3_Set_Time(pwm[7]);
							flag_vpwm=1;
						}	break;	  
					case 16:
						{
							PWM8=0; /*pwm8变低*/
							TIM3_Set_Time(2500-pwm[7]);
							flag_vpwm=1;
							i=0;
						}	break;	
					default:break;			
		    }	
			i++;
			TIM_ClearITPendingBit(TIM3,TIM_IT_Update);  /*清除TIMx更新中断标志，这行代码放置的位置很重要!!!!*/ 

		}	
		
}

   /*定时器4中断服务程序*/
void TIM4_IRQHandler(void)  
{  
					LED1=!LED1;	
					LED0=!LED0;					
		if (TIM_GetITStatus(TIM4, TIM_IT_Update) != RESET)  /*检查TIM3更新中断发生与否*/
		{
				 switch(movement_tag)
				 {
							 case 'Z':
							 {
								  TIM_Cmd(TIM4, DISABLE);/*关闭定时器4*/
								  movement_tag='A';
								  PicArray_ToBufferArray(secpic_position,0);
								  motor_speed=500;
							 }break;
							 
							 case 'A':
							 {
									TIM_Cmd(TIM4, DISABLE);
									movement_tag='B';
									PicArray_ToBufferArray(thirpic_position,3);
								  motor_speed=250;
							 }break;
						 
							 case 'B':
							 {
									TIM_Cmd(TIM4, DISABLE);
									movement_tag='C';								 
									PicArray_ToBufferArray(fourpic_position,0);		
									motor_speed=500;								 
							 }break;
						 
							 case 'C':
							 {
									TIM_Cmd(TIM4, DISABLE);
									movement_tag='D';								 
									PicArray_ToBufferArray(fifpic_position,3);
								  motor_speed=250;
							 }break;
						 
							 case 'D':
							 {
									TIM_Cmd(TIM4, DISABLE);
									movement_tag='E';								 
									PicArray_ToBufferArray(sixpic_position,0);
								  motor_speed=500;
							 }break;
						 
							 case 'E':
							 { 
									TIM_Cmd(TIM4, DISABLE);                       /*注意着这两个时间可能会冲突*/
               		movement_tag='Q';								    					/*回归初始值，否则会陷入循环*/			
                  USART_SendChar('7');	                        /*通知上位机图片识别*/
	                PicArray_ToBufferArray(retuinit_position,3);	/*返回初始位置*/	
									motor_speed=250;								 
							 }break;
							 
							 default:
							 {
								 
							 }break;
				 
				  }
				 
			change();		
			TIM_Cmd(TIM3, ENABLE);                      /*打开TIM3*/				
			TIM_ClearITPendingBit(TIM4,TIM_IT_Update);  /*清除TIMx更新中断标志，这行代码放置的位置很重要!!!!*/ 

		}

}










