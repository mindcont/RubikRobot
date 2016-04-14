#ifndef __TIMER_H
#define __TIMER_H
#include "sys.h"

extern u8 flag_vpwm; /*²å²¹±êÖ¾Î»*/

void TIM3_Int_Init(u16 arr,u16 psc);
void TIM4_Int_Init(u16 arr,u16 psc);
void TIM3_Set_Time(u16 arr);


 
#endif
