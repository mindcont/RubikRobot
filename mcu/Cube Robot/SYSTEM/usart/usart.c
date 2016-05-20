#include "usart.h"

u8 uart_rdata=0;               /*通过串口发来的单字符*/
u8 rece_flag=0;                /*串口接收标志位*/
u8 rece_string[50]="#U!";      /*存放接收到的字符串*/

  /*USART1初始化*/
void USART1_Config(void)
{
	GPIO_InitTypeDef GPIO_InitStructure;
	USART_InitTypeDef USART_InitStructure;

	RCC_APB2PeriphClockCmd( RCC_APB2Periph_GPIOA|RCC_APB2Periph_USART1, ENABLE); 


  	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_9;	
  	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_AF_PP;   
  	GPIO_InitStructure.GPIO_Speed = GPIO_Speed_50MHz; 
  	GPIO_Init(GPIOA, &GPIO_InitStructure);		  

  	GPIO_InitStructure.GPIO_Pin = GPIO_Pin_10;	
  	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IN_FLOATING;  
  	GPIO_Init(GPIOA, &GPIO_InitStructure);


	  USART_InitStructure.USART_BaudRate=9600;
    USART_InitStructure.USART_WordLength = USART_WordLength_8b;
    USART_InitStructure.USART_StopBits = USART_StopBits_1;
    USART_InitStructure.USART_Parity = USART_Parity_No;
    USART_InitStructure.USART_Mode = USART_Mode_Rx | USART_Mode_Tx;
    USART_InitStructure.USART_HardwareFlowControl = USART_HardwareFlowControl_None;
	  USART_Init (USART1,&USART_InitStructure);
	  USART_ITConfig(USART1, USART_IT_RXNE, ENABLE);	 /*Enables USART1 interrupts,USART_IT_RXNE: Receive Data register not empty interrupt */
	  USART_Cmd (USART1,ENABLE);

	  NVIC_USART1_Configuration();

	
}

/*数字转ASC码函数*/
u8 asc_to_num(u8 asc)				
{	
	u8 num;
	switch(asc)
	{
		case 0:num=0x30;break;
		case 1:num=0x31;break;
		case 2:num=0x32;break;
		case 3:num=0x33;break;
		case 4:num=0x34;break;
		case 5:num=0x35;break;
		case 6:num=0x36;break;
		case 7:num=0x37;break;
		case 8:num=0x38;break;
		case 9:num=0x39;break;	 
	}										  
	return num;
}

/*通过串口将char型数据转换为ASC码发送出去*/
void USART_SendChar8(u8 num)
{
     u8 bai,shi,ge;
	 u8 asc_bai,asc_shi,asc_ge;

	 bai=num/100;
   shi=num/10%10;
	 ge=num%10;

	 asc_bai=asc_to_num(bai);				
	 asc_shi=asc_to_num(shi);				
	 asc_ge=asc_to_num(ge);

	 if(asc_bai!=0x30)
	 {
		 USART_SendData (USART1,asc_bai);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 }
	 if(asc_bai!=0x30|asc_shi!=0x30)
	 {
		 USART_SendData (USART1,asc_shi);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);

	 }

	 USART_SendData (USART1,asc_ge);
     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);		 	

	 USART_SendData (USART1,'\r');
     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 	 
 	 USART_SendData (USART1,'\n');
     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);

	 USART_ClearFlag (USART1,USART_FLAG_TC);

}

/*通过串口将int型数据转换为ASC码发送出去*/
void USART_SendInt16(u16 num)
{
     u8 wan,qian,bai,shi,ge;
	 u8 asc_wan,asc_qian,asc_bai,asc_shi,asc_ge;
	 u8 wan_flag,qian_flag,bai_flag;

	 wan=num/10000;
	 qian=num/1000%10;
	 bai=num/100%10;
   shi=num/10%10;
	 ge=num%10;

	 asc_wan=asc_to_num(wan);
	 asc_qian=asc_to_num(qian);				
	 asc_bai=asc_to_num(bai);				
	 asc_shi=asc_to_num(shi);				
	 asc_ge=asc_to_num(ge);

	 if(asc_wan!=0x30)
	 {
	     wan_flag=1;
		 USART_SendData (USART1,asc_wan);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 }

	 if(wan_flag==1|asc_qian!=0x30)
	 {
	     qian_flag=1;
		 USART_SendData (USART1,asc_qian);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 }

	 if(qian_flag==1|asc_bai!=0x30)
	 {
	     bai_flag=1;
		 USART_SendData (USART1,asc_bai);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 }

	 if(bai_flag==1|asc_shi!=0x30)
	 {
		 USART_SendData (USART1,asc_shi);
	     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 }


     USART_SendData (USART1,asc_ge);
	 while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 		 	
	 USART_SendData (USART1,'\r');
     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	 	 
 	 USART_SendData (USART1,'\n');
     while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);

	 USART_ClearFlag (USART1,USART_FLAG_TC);

}




  /* 发送字符串*/
void USART_SendString (unsigned char *str)
{
	
	while(*str != '!')
	{
		
		 USART_SendData (USART1,*str++);
		 while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);
	
	}
       
}


 /* 通过串口发送单个字符*/
void USART_SendChar(u8 siglechar)
{
   USART_SendData (USART1,siglechar);
   while(USART_GetFlagStatus (USART1,USART_FLAG_TC)!=SET);	 //等待发送完成
   USART_ClearFlag (USART1,USART_FLAG_TC);
}

/*
 *函数功能：USART1中断优先级设置
 *备    注：经过测试，不设置USART1中断优先级，无法使用中断
 */
void NVIC_USART1_Configuration(void)
{
	NVIC_InitTypeDef NVIC_InitStructure; 

	/* Enable the USARTy Interrupt */
	NVIC_InitStructure.NVIC_IRQChannel = USART1_IRQn;	 
	NVIC_InitStructure.NVIC_IRQChannelPreemptionPriority = 1;
	NVIC_InitStructure.NVIC_IRQChannelSubPriority = 1;
	NVIC_InitStructure.NVIC_IRQChannelCmd = ENABLE;
	NVIC_Init(&NVIC_InitStructure);
}


/*串口1中断服务程序*/
//void USART1_IRQHandler(void)
//{
//	if(USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)  /*Receive Data register not empty interrupt*/
//	{ 	
//		uart_rdata=USART1->DR;
//		rece_flag=1;
//    USART_ClearFlag (USART1,USART_IT_RXNE);	           /*clear Receive data register not empty flag*/ 
//		
//	} 	 
//}

void USART1_IRQHandler(void)
{
   static u8 i=0;
	
	if(USART_GetITStatus(USART1, USART_IT_RXNE) != RESET)  /*Receive Data register not empty interrupt*/
	{ 	
		 rece_string[i++]=USART1->DR;
		
		if((rece_string[i-1]=='!')&&(rece_string[0]=='#'))  /*'!'为字符串结束标志位*/
		{	
			i=0;          /*勿忘清零*/
			rece_flag=1;
		  USART_ClearFlag (USART1,USART_IT_RXNE);	           /*clear Receive data register not empty flag*/
		}
		
     
	} 	
	
}




