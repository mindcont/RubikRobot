#ifndef ___MOTOR___H____
#define ___MOTOR___H____


#include "stm32f10x.h"

#define PWM1 PAout(0)// PB5
#define PWM2 PAout(1)// PE5	
#define PWM3 PAout(2)// PB5
#define PWM4 PAout(3)// PE5	
#define PWM5 PAout(4)// PB5
#define PWM6 PAout(5)// PE5	
#define PWM7 PAout(6)// PB5
#define PWM8 PAout(7)// PE5	

extern u16 pwm[8];
extern u16 pos[500][8];
extern u16 motor_speed;

void change(void);
void vpwm(void);
void MotorPin_Init(void);


		 
#endif
