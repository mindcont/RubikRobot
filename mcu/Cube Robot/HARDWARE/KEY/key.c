#include "key.h"
#include "sys.h" 
#include "delay.h"

								    
/*按键初始化函数*/
void KEY_Init(void) 
{ 
 	GPIO_InitTypeDef GPIO_InitStructure;
 	RCC_APB2PeriphClockCmd(RCC_APB2Periph_GPIOE,ENABLE);

	GPIO_InitStructure.GPIO_Pin  = GPIO_Pin_2;
	GPIO_InitStructure.GPIO_Mode = GPIO_Mode_IPU; /*设置成上拉输入*/
 	GPIO_Init(GPIOE, &GPIO_InitStructure);
}

