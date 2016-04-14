/*  文件名：movement.c
 *文件描述：
 *备    注: 在计算舵机动作数组时,记住用到的是绝对位置，而不是相对位置
 */
#include "stm32f10x.h"
#include "movement.h"
#include "motor.h"
#include "usart.h"
#include "instruction.h"
static const u16 original_position[4]={1475,1445,1525,1440};          /*初始位置1,2,3,4号舵机的角度*/
static const u16 clockwise90_position[4]={695,650,730,600};           /*由初始位置顺时针转动90度时1,2,3,4号舵机的角度*/
static const u16 anticlockwise90_position[4]={2350,2230,2310,2270};   /*由初始位置逆时针转动90度时1,2,3,4号舵机的角度*/
static const u16 wrasp_position[4]={1995,1890,1990,1890};             /*机械手抓紧魔方时5,6,7,8号舵机的角度*/
static const u16 loosen_position[4]={1480,1390,1450,1390};            /*机械手松开魔方时5,6,7,8号舵机的角度*/
 

u16 lines_num;
u16 initial_position[8];
u16 firpic_position[3][8];
u16 secpic_position[1][8];
u16 thirpic_position[3][8];
u16 fourpic_position[1][8];
u16 fifpic_position[4][8];
u16 sixpic_position[1][8];
u16 retuinit_position[5][8];

u16 u_clockwise90[16][8];/*上层顺时针90度*/
u16 d_clockwise90[16][8];/*下层顺时针90度*/
u16 r_clockwise90[4][8]; /*右层顺时针90度*/
u16 l_clockwise90[4][8]; /*左层顺时针90度*/
u16 f_clockwise90[4][8]; /*前层顺时针90度*/
u16 b_clockwise90[4][8]; /*后层顺时针90度*/

u16 u_anticlockwise90[16][8];/*上层逆时针90度*/
u16 d_anticlockwise90[16][8];/*下层逆时针90度*/
u16 r_anticlockwise90[4][8]; /*右层逆时针90度*/
u16 l_anticlockwise90[4][8]; /*左层逆时针90度*/
u16 f_anticlockwise90[4][8]; /*前层逆时针90度*/
u16 b_anticlockwise90[4][8]; /*后层逆时针90度*/


/*左右前后旋转180度时，只需执行两遍逆时针90度或者两遍顺时针90度数组*/
u16 u_clock180[20][8];    /*上层180度*/
u16 d_clock180[20][8];    /*下层180度*/


/*
 *  函数名：Calcul_IniticalPosition()
 *函数功能: 计算舵机的初始角度
 *输    入: 无
 *输    出: 无
 */
void Calcul_InitPosition(void)
{
	u8 i,j;
	for(i=0;i<4;i++)
	{
		initial_position[i]=original_position[i];
	}
	for(i=4,j=0;i<8;i++,j++)
	{
		initial_position[i]=wrasp_position[j];
	}
}

/*  函数名:Calcul_FirPicPosition()
 *函数功能:计算拍照第一个面的舵机运行数组
 *备    注:数组标号=舵机号-1 
 */
void Calcul_FirPicPosition(void)
{
	u8 i;
	Calcul_InitPosition();
	
	for(i=0;i<8;i++)                     /*第一行放初始位置*/
	{
	  firpic_position[0][i]=initial_position[i];
	}
	
	for(i=0;i<8;i++)                     /*第二行暂时复制第一行*/
	{
		firpic_position[1][i]=firpic_position[0][i];
	}
	firpic_position[1][5]=loosen_position[1];            /*6后退*/
	firpic_position[1][7]=loosen_position[3];            /*8后退*/

 	for(i=0;i<8;i++)                     /*第三行暂时复制第二行*/
	{
		firpic_position[2][i]=firpic_position[1][i];
	}
	
	           /*拍照左面*/                        
		firpic_position[2][0]=clockwise90_position[0];      /*1顺时针90度,*/
		firpic_position[2][2]=anticlockwise90_position[2];  /*3逆时针90度*/                        	
}


/*  函数名:Calcul_SecPicPosition()
 *函数功能:计算拍照第二个面的舵机运行数组
 *备    注:数组标号=舵机号-1,先运行Calcul_FirPicPosition()函数
 */
void Calcul_SecPicPosition(void)
{
	u8 i;
	
 	for(i=0;i<8;i++)                     
	{
		secpic_position[0][i]=firpic_position[2][i];
	}
                      /*拍照右面*/   
 secpic_position[0][0]=anticlockwise90_position[0];       /*1逆时针90度,*/ 
 secpic_position[0][2]=clockwise90_position[2];	          /*3顺时针90度*/  

}


/*  函数名:Calcul_ThirPicPosition()
 *函数功能:计算拍照第三个面的舵机运行数组
 *备    注:数组标号=舵机号-1 
 */
void Calcul_ThirPicPosition(void)
{
	u8 i;
	for(i=0;i<8;i++)
	{
		thirpic_position[0][i]=secpic_position[0][i];
	}
	
	thirpic_position[0][5]=wrasp_position[1];  /*6前进*/
	thirpic_position[0][7]=wrasp_position[3];  /*8前进*/
	
	for(i=0;i<8;i++)
	{
		thirpic_position[1][i]=thirpic_position[0][i];  /*复制第一行到第二行*/
	}
	
	thirpic_position[1][4]=loosen_position[0];   /*5后退*/
	thirpic_position[1][6]=loosen_position[2];   /*7后退*/
	
		/*拍照前面*/
	for(i=0;i<8;i++)
	{
	  thirpic_position[2][i]=thirpic_position[1][i];  /*复制第一行到第二行*/
	}
	
	thirpic_position[2][1]=clockwise90_position[1];                 /*2顺时针*/
	thirpic_position[2][3]=anticlockwise90_position[3];             /*4逆时针*/
	
}


/*  函数名:Calcul_FourPicPosition()
 *函数功能:计算拍照第四个面的舵机运行数组
 *备    注:数组标号=舵机号-1 
 */
void Calcul_FourPicPosition(void)
{
	 u8 i;
	for(i=0;i<8;i++)
		{
			fourpic_position[0][i]=thirpic_position[2][i];   
		}
		
		/*拍照后面*/
		fourpic_position[0][1]=anticlockwise90_position[1];  /*2逆时针*/
		fourpic_position[0][3]=clockwise90_position[3];      /*4顺时针*/
}

/*  函数名:Calcul_FifPicPosition()
 *函数功能:计算拍照第五个面的舵机运行数组
 *备    注:数组标号=舵机号-1,上面
 */
void Calcul_FifPicPosition(void)
{
	u8 i;
	for(i=0;i<8;i++)
	{
		fifpic_position[0][i]=fourpic_position[0][i];
	}
	
	fourpic_position[0][0]=original_position[0];	         /*1回到初始位置*/
	fourpic_position[0][2]=original_position[2];	         /*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		fifpic_position[1][i]=fifpic_position[0][i];
	}
	
  fifpic_position[1][4]=wrasp_position[0];		/*5前进*/
  fifpic_position[1][6]=wrasp_position[2];		/*7前进*/	

	for(i=0;i<8;i++)
	{
		fifpic_position[2][i]=fifpic_position[1][i];
	}
	
	fifpic_position[2][5]=loosen_position[1];   /*6后退*/
	fifpic_position[2][7]=loosen_position[3];   /*8后退*/
	
	
	/*拍照上面*/
	for(i=0;i<8;i++)
	{
		fifpic_position[3][i]=fifpic_position[2][i];
	}
	
	fifpic_position[3][0]=clockwise90_position[0];      /*1顺时针*/
	fifpic_position[3][2]=anticlockwise90_position[2];  /*3逆时针*/
	
}


/*  函数名:Calcul_SixPicPosition()
 *函数功能:计算拍照第六个面的舵机运行数组
 *备    注:数组标号=舵机号-1 
 */
void Calcul_SixPicPosition(void)
{
	u8 i;
	for(i=0;i<8;i++)
	{
		sixpic_position[0][i]=fifpic_position[3][i];
	}
		/*拍照下面*/
	sixpic_position[0][0]=anticlockwise90_position[0];/*1逆时针*/
	sixpic_position[0][2]=clockwise90_position[2];    /*3顺时针*/
}


/*  函数名: RetuIni_AftPic()
 *函数功能: 在拍照完6个面之后回到初始位置
 *备    注：按照原路的逆路径返回，并且和原来的摆放位置相同
 */
void RetuIni_AftPic(void)
{
		u8 i;
	
	for(i=0;i<8;i++)
	{
		retuinit_position[0][i]=sixpic_position[0][i];
	}	
		
	retuinit_position[0][1]=original_position[1];  /*2回到初始位置*/
	retuinit_position[0][3]=original_position[3];  /*4回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		retuinit_position[1][i]=retuinit_position[0][i];
	}
	retuinit_position[1][5]=wrasp_position[1];  /*6前进*/
	retuinit_position[1][7]=wrasp_position[3];  /*8前进*/
	
	for(i=0;i<8;i++)
	{
		retuinit_position[2][i]=retuinit_position[1][i];
	}
	retuinit_position[2][4]=loosen_position[0];  /*5后退*/
	retuinit_position[2][6]=loosen_position[2];  /*7后退*/
	
	for(i=0;i<8;i++)
	{
		retuinit_position[3][i]=retuinit_position[2][i];
	}
	
	retuinit_position[3][0]=original_position[0]; /*1回到初始位置*/ 
	retuinit_position[3][2]=original_position[2]; /*3回到初始位置*/ 
		
	for(i=0;i<8;i++)
	{
		retuinit_position[4][i]=retuinit_position[3][i];
	}
	
	retuinit_position[4][4]=wrasp_position[0];    /*5前进*/
	retuinit_position[4][6]=wrasp_position[2];    /*7前进*/	

}




/*  函数名: Calcul_Uclockwise90()
 *函数功能：计算魔方上层顺时针旋转90的舵机角度数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：初始化的顺序要注意
 */
void Calcul_Uclockwise90(void)
{
	u8 i,j,k;
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[0][i]=initial_position[i];
	}
	
	u_clockwise90[0][5]=loosen_position[1]; /*6后退*/
	u_clockwise90[0][7]=loosen_position[3]; /*8后退*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[1][i]=u_clockwise90[0][i];
	}
	
	u_clockwise90[1][0]=clockwise90_position[0];       /*1顺时针*/
	u_clockwise90[1][2]=anticlockwise90_position[2];   /*3逆时针*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[2][i]=u_clockwise90[1][i];
	}
	
	u_clockwise90[2][5]=wrasp_position[1]; /*6前进*/
	u_clockwise90[2][7]=wrasp_position[3]; /*8前进*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[3][i]=u_clockwise90[2][i];
	}
	u_clockwise90[3][4]=loosen_position[0]; /*5后退*/
	u_clockwise90[3][6]=loosen_position[2]; /*7后退*/


	for(i=0;i<8;i++)
	{
		u_clockwise90[4][i]=u_clockwise90[3][i];
	}
	
	 u_clockwise90[4][0]=original_position[0];/*1回到初始位置*/
	 u_clockwise90[4][2]=original_position[2];/*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[5][i]=u_clockwise90[4][i];
	}
	
	u_clockwise90[5][4]=wrasp_position[0]; /*5前进*/
	u_clockwise90[5][6]=wrasp_position[2]; /*7前进，此时到达初始位置*/
	
   
/*前面代码和逆时针旋转的代码相同，前面生成的数组相同，下面是魔方右面顺时针旋转90度*/	
	
//	for(i=0;i<8;i++)
//	{
//		u_clockwise90[6][i]=u_clockwise90[5][i];
//	}
//	
//	u_clockwise90[6][3]=anticlockwise90_position[3];/*4逆时针*/
//	
//	 
//	for(i=0;i<8;i++)
//	{
//		u_clockwise90[7][i]=u_clockwise90[6][i];
//	}
//	
//	u_clockwise90[7][7]=loosen_position[3]; /*8后退*/
//	
//	for(i=0;i<8;i++)
//	{
//		u_clockwise90[8][i]=u_clockwise90[7][i];
//	}
//	
//	u_clockwise90[8][3]=original_position[3];/*4回到初始位置*/
//	
//	for(i=0;i<8;i++)
//	{
//		u_clockwise90[9][i]=u_clockwise90[8][i];
//	}
//	
//	u_clockwise90[9][7]=wrasp_position[3];   /*8前进,此刻到达处又一次到达初始位置*/

	
	/*魔方右面顺时针旋转90度*/
	
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			u_clockwise90[j][i]=r_clockwise90[k][i];				
		}
	
	}
		
 /*下面是从放倒到直立过程*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[10][i]=u_clockwise90[9][i];
	}

	u_clockwise90[10][5]=loosen_position[1];	  /*6后退*/
	u_clockwise90[10][7]=loosen_position[3];    /*8后退*/
	
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[11][i]=u_clockwise90[10][i];
	}
	
	u_clockwise90[11][0]=anticlockwise90_position[0];	  /*1逆时针*/
	u_clockwise90[11][2]=clockwise90_position[2];       /*3顺时针*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[12][i]=u_clockwise90[11][i];
	}
	
	u_clockwise90[12][5]=wrasp_position[1];	 /*6前进*/
	u_clockwise90[12][7]=wrasp_position[3];  /*8前进*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[13][i]=u_clockwise90[12][i];
	}
	
	u_clockwise90[13][4]=loosen_position[0];	   /*5后退*/
	u_clockwise90[13][6]=loosen_position[2];     /*7后退*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[14][i]=u_clockwise90[13][i];
	}
	u_clockwise90[14][0]=original_position[0];	/*1回到初始位置*/
	u_clockwise90[14][2]=original_position[2];  /*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		u_clockwise90[15][i]=u_clockwise90[14][i];
	}
	u_clockwise90[15][4]=wrasp_position[0];   /*5前进*/
	u_clockwise90[15][6]=wrasp_position[2];	  /*7前进*/  
	
}

/*  函数名: Calcul_Uanticlockwise90()
 *函数功能：计算魔方上层逆时针旋转90的舵机角度数组
 *输    入：无
 *输    出：无
 *备    注：向右边翻转，上面朝右,重用顺时针生成的数组
 */
void Calcul_Uanticlockwise90(void)
{
		u8 i,j,k;
	
	/*从初始位置到右边倒的过程*/
	for(i=0;i<=5;i++)
	{
		for(j=0;j<8;j++)
		
			{
				u_anticlockwise90[i][j]=u_clockwise90[i][j];	
			}
	}
	
	
	/*魔方右面逆时针旋转90度*/
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			u_anticlockwise90[j][i]=r_anticlockwise90[k][i];				
		}
	
	}

	
	/*下面是从放倒到直立过程*/
	for(j=10;j<=15;j++)
	{
		
		for(i=0;i<8;i++)
		{
			u_anticlockwise90[j][i]=u_clockwise90[j][i];				
		}
	
	}
	
	
}


/*  函数名: Calcul_Dclockwise90()
 *函数功能：计算魔方下层顺时针旋转90的舵机角度数组
 *输    入：无
 *输    出：无
 *调用情况：被初始化函数调用
 *备    注：向左翻转，下面朝右，另外注意初始化函数调用的顺序，逆时针在顺时针之后调用
 */
void Calcul_Dclockwise90(void)
{
	u8 i,j,k;
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[0][i]=initial_position[i];
	}
	
	d_clockwise90[0][5]=loosen_position[1];/*6后退*/
	d_clockwise90[0][7]=loosen_position[3];/*8后退*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[1][i]=d_clockwise90[0][i];
	}
	
	d_clockwise90[1][0]=anticlockwise90_position[0];       /*1逆时针*/
	d_clockwise90[1][2]=clockwise90_position[2];           /*3顺时针，完成向左翻转的动作*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[2][i]=d_clockwise90[1][i];
	}
	
	d_clockwise90[2][5]=wrasp_position[1]; /*6前进*/
	d_clockwise90[2][7]=wrasp_position[3]; /*8前进*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[3][i]=d_clockwise90[2][i];
	}
	d_clockwise90[3][4]=loosen_position[0]; /*5后退*/
	d_clockwise90[3][6]=loosen_position[2]; /*7后退*/


	for(i=0;i<8;i++)
	{
		d_clockwise90[4][i]=d_clockwise90[3][i];
	}
	
	 d_clockwise90[4][0]=original_position[0];/*1回到初始位置*/
	 d_clockwise90[4][2]=original_position[2];/*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[5][i]=d_clockwise90[4][i];
	}
	
	d_clockwise90[5][4]=wrasp_position[0]; /*5前进*/
	d_clockwise90[5][6]=wrasp_position[2]; /*7前进，此时到达初始位置*/
   
	
	/*前面代码和逆时针旋转的代码相同，前面生成的数组相同*/
	
//	for(i=0;i<8;i++)
//	{
//		d_clockwise90[6][i]=d_clockwise90[5][i];
//	}
//	
//	d_clockwise90[6][3]=anticlockwise90_position[3];/*4逆时针*/
//	
//	 
//	for(i=0;i<8;i++)
//	{
//		d_clockwise90[7][i]=d_clockwise90[6][i];
//	}
//	
//	d_clockwise90[7][7]=loosen_position[3]; /*8后退*/
//	
//	for(i=0;i<8;i++)
//	{
//		d_clockwise90[8][i]=d_clockwise90[7][i];
//	}
//	
//	d_clockwise90[8][3]=original_position[3];/*4回到初始位置*/
//	
//	for(i=0;i<8;i++)
//	{
//		d_clockwise90[9][i]=d_clockwise90[8][i];
//	}
//
//	d_clockwise90[9][7]=wrasp_position[3];   /*8前进,此刻到达处又一次到达初始位置*/


	/*魔方右面顺时针旋转90度*/
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			d_clockwise90[j][i]=r_clockwise90[k][i];				
		}
	
	}

	
	/*下面是从放倒到直立过程*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[10][i]=d_clockwise90[9][i];
	}

	d_clockwise90[10][5]=loosen_position[1];	  /*6后退*/
	d_clockwise90[10][7]=loosen_position[3];    /*8后退*/
	
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[11][i]=d_clockwise90[10][i];
	}
	
	d_clockwise90[11][0]=clockwise90_position[0];	        /*1顺时针*/
	d_clockwise90[11][2]=anticlockwise90_position[2];     /*3逆时针*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[12][i]=d_clockwise90[11][i];
	}
	
	d_clockwise90[12][5]=wrasp_position[1];	 /*6前进*/
	d_clockwise90[12][7]=wrasp_position[3];  /*8前进*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[13][i]=d_clockwise90[12][i];
	}
	
	d_clockwise90[13][4]=loosen_position[0];	   /*5后退*/
	d_clockwise90[13][6]=loosen_position[2];     /*7后退*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[14][i]=d_clockwise90[13][i];
	}
	d_clockwise90[14][0]=original_position[0];	/*1回到初始位置*/
	d_clockwise90[14][2]=original_position[2];  /*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		d_clockwise90[15][i]=d_clockwise90[14][i];
	}
	d_clockwise90[15][4]=wrasp_position[0];      /*5前进*/
	d_clockwise90[15][6]=wrasp_position[2];	     /*7前进*/  

}


/*  函数名: Calcul_Danticlockwise90()
 *函数功能：计算魔方下层逆时针旋转90的舵机角度数组
 *输    入：无
 *输    出：无
 *备    注：向左翻转，下面朝右
 */
void Calcul_Danticlockwise90(void)
{
	
    u8 i,j,k;
	
	/*从初始位置向左翻转*/
	for(i=0;i<=5;i++)
	{
		for(j=0;j<8;j++)
		
			{
				d_anticlockwise90[i][j]=d_clockwise90[i][j];	
			}
	}
	
	
	/*魔方右面逆时针旋转90度,*/
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			d_anticlockwise90[j][i]=r_anticlockwise90[k][i];				
		}
	
	}

	
	/*下面是从放倒到直立过程*/
	for(j=10;j<=15;j++)
	{
		
		for(i=0;i<8;i++)
		{
			d_anticlockwise90[j][i]=d_clockwise90[j][i];				
		}
	
	}	
	
	
}


/*  函数名：Calcul_Lclockwise90()
 *函数功能：计算魔方左面顺时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：先调用initial_position()函数
 */
void Calcul_Lclockwise90(void)
{
	
	u8 i;
	
	for(i=0;i<8;i++)
	{
		l_clockwise90[0][i]=initial_position[i];
	}
	
	l_clockwise90[0][1]=anticlockwise90_position[1];/*2逆时针*/
	
	for(i=0;i<8;i++)
	{
		l_clockwise90[1][i]=l_clockwise90[0][i];
	}
	
	l_clockwise90[1][5]=loosen_position[1]; /*6后退*/
	
	for(i=0;i<8;i++)
	{
		l_clockwise90[2][i]=l_clockwise90[1][i];
	}
	
	l_clockwise90[2][1]=original_position[1];/*2回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		l_clockwise90[3][i]=l_clockwise90[2][i];
	}
	
	l_clockwise90[3][5]=wrasp_position[1]; /*6前进*/
	
	
}


/*  函数名：Calcul_Lanticlockwise90()
 *函数功能：计算魔方左面逆时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：先调用initial_position()函数
 */
void Calcul_Lanticlockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		l_anticlockwise90[0][i]=initial_position[i];
	}
	
	l_anticlockwise90[0][1]=clockwise90_position[1];/*2顺时针*/
	
	for(i=0;i<8;i++)
	{
		l_anticlockwise90[1][i]=l_anticlockwise90[0][i];
	}
	
	l_anticlockwise90[1][5]=loosen_position[1]; /*6后退*/
	
	for(i=0;i<8;i++)
	{
		l_anticlockwise90[2][i]=l_anticlockwise90[1][i];
	}
	
	l_anticlockwise90[2][1]=original_position[1];/*2回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		l_anticlockwise90[3][i]=l_anticlockwise90[2][i];
	}
	
	l_anticlockwise90[3][5]=wrasp_position[1]; /*6前进*/
	

}



/*  函数名：Calcul_Rclockwise90()
 *函数功能：计算魔方右面顺时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：先调用initial_position()函数
 */
void Calcul_Rclockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		r_clockwise90[0][i]=initial_position[i];
	}
	
	r_clockwise90[0][3]=anticlockwise90_position[3];/*4逆时针*/
	
	for(i=0;i<8;i++)
	{
		r_clockwise90[1][i]=r_clockwise90[0][i];
	}
	
	r_clockwise90[1][7]=loosen_position[3]; /*8后退*/
	
	for(i=0;i<8;i++)
	{
		r_clockwise90[2][i]=r_clockwise90[1][i];
	}
	
	r_clockwise90[2][3]=original_position[3];/*4回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		r_clockwise90[3][i]=r_clockwise90[2][i];
	}
	
	r_clockwise90[3][7]=wrasp_position[3]; /*8前进*/
	
}



/*  函数名：Calcul_Ranticlockwise90()
 *函数功能：计算魔方右面逆时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：先调用initial_position()函数
 */
void Calcul_Ranticlockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		r_anticlockwise90[0][i]=initial_position[i];
	}
	
    r_anticlockwise90[0][3]=clockwise90_position[3];/*4顺时针*/
	
	for(i=0;i<8;i++)
	{
		r_anticlockwise90[1][i]=r_anticlockwise90[0][i];
	}
	
	r_anticlockwise90[1][7]=loosen_position[3]; /*8后退*/
	
	for(i=0;i<8;i++)
	{
		r_anticlockwise90[2][i]=r_anticlockwise90[1][i];
	}
	
	r_anticlockwise90[2][3]=original_position[3];/*4回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		r_anticlockwise90[3][i]=r_anticlockwise90[2][i];
	}
	
	r_anticlockwise90[3][7]=wrasp_position[3]; /*8前进*/
	
}



/*  函数名：Calcul_Fclockwise90()
 *函数功能：计算魔方前面顺时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：
 */
void Calcul_Fclockwise90(void)
{
    u8 i;
	
	for(i=0;i<8;i++)
	{
		f_clockwise90[0][i]=initial_position[i];
	}
	
	f_clockwise90[0][2]=anticlockwise90_position[2];/*3逆时针*/
	
	for(i=0;i<8;i++)
	{
		f_clockwise90[1][i]=f_clockwise90[0][i];
	}
	
	f_clockwise90[1][6]=loosen_position[2]; /*7后退*/
	
	for(i=0;i<8;i++)
	{
		f_clockwise90[2][i]=f_clockwise90[1][i];
	}
	
	f_clockwise90[2][2]=original_position[2];/*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		f_clockwise90[3][i]=f_clockwise90[2][i];
	}
	
	f_clockwise90[3][6]=wrasp_position[2]; /*7前进*/
	
}


/*  函数名：Calcul_Fanticlockwise90()
 *函数功能：计算魔方前面逆时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：
 */
void Calcul_Fanticlockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		f_anticlockwise90[0][i]=initial_position[i];
	}
	
	f_anticlockwise90[0][2]=clockwise90_position[2];/*3顺时针*/
	
	for(i=0;i<8;i++)
	{
		f_anticlockwise90[1][i]=f_anticlockwise90[0][i];
	}
	
	f_anticlockwise90[1][6]=loosen_position[2]; /*7后退*/
	
	for(i=0;i<8;i++)
	{
		f_anticlockwise90[2][i]=f_anticlockwise90[1][i];
	}
	
	f_anticlockwise90[2][2]=original_position[2];/*3回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		f_anticlockwise90[3][i]=f_anticlockwise90[2][i];
	}
	
	f_anticlockwise90[3][6]=wrasp_position[2]; /*7前进*/
	
}

/*  函数名：Calcul_	Bclockwise90()
 *函数功能：计算魔方后面顺时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：
 */
void Calcul_Bclockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		b_clockwise90[0][i]=initial_position[i];
	}
	
	b_clockwise90[0][0]=anticlockwise90_position[0];/*1逆时针*/
	
	for(i=0;i<8;i++)
	{
		b_clockwise90[1][i]=b_clockwise90[0][i];
	}
	
	b_clockwise90[1][4]=loosen_position[0]; /*5后退*/
	
	for(i=0;i<8;i++)
	{
		b_clockwise90[2][i]=b_clockwise90[1][i];
	}
	
	b_clockwise90[2][0]=original_position[0];/*1回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		b_clockwise90[3][i]=b_clockwise90[2][i];
	}
	
	b_clockwise90[3][4]=wrasp_position[0]; /*5前进*/
	

}

/*  函数名：Calcul_	Bantianticlockwise90()
 *函数功能：计算魔方后面逆时针旋转90度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：
 */
void Calcul_Banticlockwise90(void)
{
	u8 i;
	
	for(i=0;i<8;i++)
	{
		b_anticlockwise90[0][i]=initial_position[i];
	}
	
	b_anticlockwise90[0][0]=clockwise90_position[0];    /*1顺时针*/
	
	for(i=0;i<8;i++)
	{
		b_anticlockwise90[1][i]=b_anticlockwise90[0][i];
	}
	
	b_anticlockwise90[1][4]=loosen_position[0];         /*5后退*/
	
	for(i=0;i<8;i++)
	{
		b_anticlockwise90[2][i]=b_anticlockwise90[1][i];
	}
	
	b_anticlockwise90[2][0]=original_position[0];      /*1回到初始位置*/
	
	for(i=0;i<8;i++)
	{
		b_anticlockwise90[3][i]=b_anticlockwise90[2][i];
	}
	
	b_anticlockwise90[3][4]=wrasp_position[0];         /*5前进*/
	
}




/*  函数名：Calcul_Uclock180()
 *函数功能：计算魔方上面旋转180度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：数组最大标号19
 */
void Calcul_Uclock180(void)
{
   u8 i,j,k;
	
   /*从初始位置到向右倒立的过程*/
	
	for(j=0;j<=5;j++)
	{
		for(i=0;i<8;i++)
		{
			u_clock180[j][i]=u_clockwise90[j][i];
		}
		
	}
	
	/*执行两遍右面顺时针右转*/
	
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			u_clock180[j][i]=r_clockwise90[k][i];				
		}
	
	}
	
	for(k=0,j=10;j<=13;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			u_clock180[j][i]=r_clockwise90[k][i];				
		}
	
	}
	
	/*从放倒到直立的过程*/   
	
	for(j=10;j<=15;j++)
	{
		for(i=0;i<8;i++)
		{
			u_clock180[j+4][i]=u_clockwise90[j][i];				
		}
	
	}	

}


/*  函数名：Calcul_Dclock180()
 *函数功能：计算魔方下面旋转180度的舵机执行数组
 *输    入：无
 *输    出：无
 *调用情况：初始化函数
 *备    注：数组最大标号19
 */
void Calcul_Dclock180(void)
{
	 u8 i,j,k;
	
	/*从初使位置到向左倒立的过程*/
		for(j=0;j<=5;j++)
	{
		for(i=0;i<8;i++)
		{
			d_clock180[j][i]=d_clockwise90[j][i];
		}
	}
	
	
  /*执行两遍右面顺时针右转*/
	for(j=6,k=0;j<=9;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			d_clock180[j][i]=r_clockwise90[k][i];				
		}
	
	}
	
	for(j=10,k=0;j<=13;j++,k++)
	{
		for(i=0;i<8;i++)
		{
			d_clock180[j][i]=r_clockwise90[k][i];				
		}
	
	}
	
	/*从放倒到直立的过程*/   

	for(j=10;j<=15;j++)
	{
		for(i=0;i<8;i++)
		{
			d_clock180[j+4][i]=d_clockwise90[j][i];				
		}
	
	}	

}

/*  函数名：Init_TotalArray()
 *函数功能：把所有需要在程序中一开始计算的函数放在此函数中，以初始化数组
 *输    入：无
 *输    出：无
 *备    注：注意调用函数的顺序,运行完需要530us
 */
void Init_TotalArray(void)
{
	Calcul_InitPosition(); /*此函数首先被初始化，因为生成的数组会被以后很多函数用到*/
	
	Calcul_FirPicPosition();
  Calcul_SecPicPosition();
  Calcul_ThirPicPosition();
  Calcul_FourPicPosition();
  Calcul_FifPicPosition();
  Calcul_SixPicPosition();
  RetuIni_AftPic();
	
	Calcul_Lclockwise90();
	Calcul_Rclockwise90();
	Calcul_Fclockwise90();
	Calcul_Bclockwise90();
	Calcul_Lanticlockwise90();
	Calcul_Ranticlockwise90();
	Calcul_Fanticlockwise90();
	Calcul_Banticlockwise90();

	
  Calcul_Uclockwise90();
  Calcul_Uanticlockwise90();
  Calcul_Dclockwise90();
  Calcul_Danticlockwise90();

  Calcul_Uclock180();
  Calcul_Dclock180(); /*后六个函数的顺序不要动*/
}

/*  函数名：Init_PWM()
 *函数功能：
 *备    注：
 */
void Init_PWM(void)
{
	u8 i,j;
	
	for(i=0;i<4;i++)
	{
		pwm[i]=original_position[i];
	}
	for(i=4,j=0;i<8;i++,j++)
	{
		pwm[i]=wrasp_position[j];
	}

}


/*  函数名： SolvecubeArray_ToBufferArray()
 *函数功能： 把解算魔方的巨大数组放进缓存数组u16 pos[500][8]中
 *输    入： 无
 *输    出： 数组行标
 *备    注： 在把相关数组放入缓存数组时，要关掉定时器，防止插补函数中改变全局变量lines_num
 */
void  SolvecubeArray_ToBufferArray(void)
{
	 u16 i,j; 
	
  lines_num=Analy_UsartString()+1;

	for(i=0;i<8;i++)
		{
		
			pos[0][i]=initial_position[i];
		}

	for(j=1;j<=lines_num;j++)
	{
		for(i=0;i<8;i++)
		{
		
			pos[j][i]=solvecube_data[j-1][i];
		}
	
	}	
		
}	

/*  函数名：PicArray_ToBufferArray(u(*array)[8],line_num)
 *函数功能：把拍照需要执行的数组放入缓存数组，并返回需要执行数组的行标
 *输    入：二维数组指针(*array)[8]，数组行标line_num
 *输    出：数组行标
 *备    注：在把相关数组放入缓存数组时，要关掉定时器，防止插补函数中改变全局变量lines_num
 */
void PicArray_ToBufferArray(u16 (*array)[8],u16 arrayline_num)
{
		u8 i,j;
	
	  lines_num=arrayline_num+1;
	
		for(i=0;i<8;i++)
		{
		
			pos[0][i]=initial_position[i];
		}

		for(j=1;j<=lines_num;j++)
		{
			
			for(i=0;i<8;i++)
			{
				pos[j][i]=*(*(array+j-1)+i);			
			}
		
		}
	
}

/*
 *
 */
void Init_Pwm(void)
{

	
}	
