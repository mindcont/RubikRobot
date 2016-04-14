#ifndef __EXTI__H___
#define __EXTI__H___

#include "stm32f10x.h"

extern u8 exti_flag;
void Exti2_Init(void);
void Exti2_NVIC_Config(void);

#endif
