#ifndef __EXTI__H___
#define __EXTI__H___

#include "stm32f10x.h"

extern u8 exti_flag;
extern u8 movement_tag;

void Exti_Init(void);
void Ex_NVIC_Config(void);

#endif
