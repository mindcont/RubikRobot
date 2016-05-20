#ifndef __KEY_H
#define __KEY_H	 
#include "sys.h"
 
#define KEY0  GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_4)/*读取按键0*/
#define KEY1  GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_3)/*读取按键1*/
#define KEY2  GPIO_ReadInputDataBit(GPIOE,GPIO_Pin_2)/*读取按键2*/

void KEY_Init(void);
#endif
