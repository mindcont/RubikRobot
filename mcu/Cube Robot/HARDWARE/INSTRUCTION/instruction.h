#ifndef ___INSTRUCTION___H___
#define ___INSTRUCTION___H___

#include "stm32f10x.h"

extern u16 solvecube_data[500][8];/*执行最终解算的数组*/
extern u16 lines_num;

void Initial_Data(u16 (*array)[8],u16 start_line,u16 end_line);
u16 Analy_UsartString(void);
u16 Ana_Double(u8 char1,u8 char2,u8 char3,u8 char4);
u16 Get_Movement(u8 char1,u8 char2,u8 char3,u8 char4);

u8 Ana_Double2(u8 char1,u8 char2);
u8  Instruction_movement(u8 movement_instruction,u16 startline_num);
u8 Get_Movement1_1(u8 char1,u8 char2,u8 char3);
u8 Get_Movement1_2(u8 char1,u8 char2);
u8 Get_Movement2_1(u8 char1,u8 char3);
u8 Get_Movement2_2(u8 char1,u8 char3,u8 char4);
u8 Get_Movement3_1(u8 char1,u8 char3);
u8 Get_Movement3_2(u8 char1,u8 char3,u8 char4);









#endif
