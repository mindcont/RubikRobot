#ifndef ____MOVEMENT____H____
#define ____MOVEMENT____H____

#include "stm32f10x.h"

extern u16 initial_position[8];

extern u16 firpic_position[3][8];
extern u16 secpic_position[1][8];
extern u16 thirpic_position[3][8];
extern u16 fourpic_position[1][8];
extern u16 fifpic_position[4][8];
extern u16 sixpic_position[1][8];
extern u16 retuinit_position[5][8];


extern u16 u_clockwise90[16][8];/*上层顺时针90度*/
extern u16 d_clockwise90[16][8];/*下层顺时针90度*/
extern u16 r_clockwise90[4][8];/*右层顺时针90度*/
extern u16 l_clockwise90[4][8];/*左层顺时针90度*/
extern u16 f_clockwise90[4][8];/*前层顺时针90度*/
extern u16 b_clockwise90[4][8];/*后层顺时针90度*/

extern u16 u_anticlockwise90[16][8];/*上层逆时针90度*/
extern u16 d_anticlockwise90[16][8];/*下层逆时针90度*/
extern u16 r_anticlockwise90[4][8];/*右层逆时针90度*/
extern u16 l_anticlockwise90[4][8];/*左层逆时针90度*/
extern u16 f_anticlockwise90[4][8];/*前层逆时针90度*/
extern u16 b_anticlockwise90[4][8];/*后层逆时针90度*/

extern u16 u_clock180[20][8];    /*上层180度*/
extern u16 d_clock180[20][8];    /*下层180度*/


void Calcul_InitPosition(void);

void Calcul_FirPicPosition(void);
void Calcul_SecPicPosition(void);
void Calcul_ThirPicPosition(void);
void Calcul_FourPicPosition(void);
void Calcul_FifPicPosition(void);
void Calcul_SixPicPosition(void);
void RetuIni_AftPic(void);

void Calcul_Uclockwise90(void);
void Calcul_Dclockwise90(void);
void Calcul_Lclockwise90(void);
void Calcul_Rclockwise90(void);
void Calcul_Fclockwise90(void);
void Calcul_Bclockwise90(void);

void Calcul_Uanticlockwise90(void);
void Calcul_Danticlockwise90(void);
void Calcul_Lanticlockwise90(void);
void Calcul_Ranticlockwise90(void);
void Calcul_Fanticlockwise90(void);
void Calcul_Banticlockwise90(void);


void Calcul_Uclock180(void);
void Calcul_Dclock180(void);

void Init_TotalArray(void);
void Init_PWM(void);
void SolvecubeArray_ToBufferArray(void);
void PicArray_ToBufferArray(u16 (*array)[8],u16 line_num);


#endif
