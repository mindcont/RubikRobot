#ifndef ___INSTRUCTION___H___
#define ___INSTRUCTION___H___

#include "stm32f10x.h"

extern u16 solvecube_data[500][8];/*执行最终解算的数组*/
extern u16 lines_num;

void Initial_Data(u16 (*array)[8],u8 start_line,u8 end_line);
u16 Analy_UsartString(void);


#endif
