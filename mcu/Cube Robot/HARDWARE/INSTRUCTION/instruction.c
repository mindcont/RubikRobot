/*  文件名: instruction.c
 *文件功能：字符串解析
 *备    注：字符串命名规则:开始标志位:#,结束标志位:!
 *          单个大写字母90度旋转，大写字母后面带数字2代表180度旋转,后面带'的为逆时针，不带'的为顺时针
 *          U:上面,D:下面
 *          L:左面,R:右面,
 *          F:前面,B:后面
 *          示例：#B'R2U2B2DU2F2UB2R2U'L2U'L'U'FRB2RD'!
 */

#include "stm32f10x.h"
#include "instruction.h"
#include "usart.h"
#include "movement.h"

u16 solvecube_data[500][8];/*执行最终解算的数组*/



/*  函数名:Analy_UsartString()
 *函数功能：解析串口传来的字符串，并把魔方解算步骤放在solvecube_data数组中
 *输    入：无
 *输    出：solvecube_data数组的最大行标号
 *调用情况：被SolvecubeArray_ToBufferArray()调用
 *备    注：
 */
u16 Analy_UsartString(void)
{
	u8 i=1;
	u8 startline_num=0;        /*开始为solvecube_data数组赋值的数组标号*/
	while(rece_string[i]!='!')
	{
	
		
			if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&((0x41<=rece_string[i+1]&&rece_string[i+1]<=0x5a)||(rece_string[i+1]=='!')))   /*当前字符为大写字母，下一个字符为大写字母*/
			{
				  switch(rece_string[i])
					{
								case 'U':
								{
									Initial_Data(u_clockwise90,startline_num,startline_num+15);
									startline_num+=16;
								}break;
								
								case 'D':
								{
									Initial_Data(d_clockwise90,startline_num,startline_num+15);
									startline_num+=16;					
								}break;
								
								case 'L':
								{
									Initial_Data(l_clockwise90,startline_num,startline_num+3);
									startline_num+=4;						
								}break;
								
								case 'R':
								{
									Initial_Data(r_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								case 'F':
								{
									Initial_Data(f_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								case 'B':
								{
									Initial_Data(b_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								default:break;
					
					}
						
				    i++;
					
			}
			
			
			else if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(rece_string[i+1]==0x27))    /*当前字符为大写字母，下一个字符为字符’*/
			{	
				
				switch(rece_string[i])
					 {
								case 'U':
								{
									Initial_Data(u_anticlockwise90,startline_num,startline_num+15);
									startline_num+=16;
								}break;
								
								case 'D':
								{
									Initial_Data(d_anticlockwise90,startline_num,startline_num+15);
									startline_num+=16;					
								}break;
								
								case 'L':
								{
									Initial_Data(l_anticlockwise90,startline_num,startline_num+3);
									startline_num+=4;						
								}break;
								
								case 'R':
								{
									Initial_Data(r_anticlockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								case 'F':
								{
									Initial_Data(f_anticlockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								case 'B':
								{
									Initial_Data(b_anticlockwise90,startline_num,startline_num+3);
									startline_num+=4;
								}break;
								
								default:break;
					
					   }
					 
					i+=2;
						 
			}
			
			
		 else if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(rece_string[i+1]==0x32))    /**当前字符为大写字母，下一个字符为数字2*/
			{
				
				switch(rece_string[i])
					{
								case 'U':
								{
									Initial_Data(u_clock180,startline_num,startline_num+19);
									startline_num+=20;
								}break;
								
								case 'D':
								{
									Initial_Data(d_clock180,startline_num,startline_num+19);
									startline_num+=20;					
								}break;
								
								case 'L':
								{
									Initial_Data(l_clockwise90,startline_num,startline_num+3);
									startline_num+=4;	
									Initial_Data(l_clockwise90,startline_num,startline_num+3);
									startline_num+=4;	
									
								}break;
								
								case 'R':
								{
									Initial_Data(r_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									Initial_Data(r_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									
								}break;
								
								case 'F':
								{
									Initial_Data(f_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									Initial_Data(f_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									
								}break;
								
								case 'B':
								{
									Initial_Data(b_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									Initial_Data(b_clockwise90,startline_num,startline_num+3);
									startline_num+=4;
									
								}break;
								
								default:break;
							
							}
					
					i+=2;
							
		  }
			
		else
			{
				i++;
			}
					
			
	  }
	
	startline_num--;
		
	return (startline_num);

}


/*  函数名：Initial_Data(u16 *array,u8 start_num,u8 end_num)
 *函数功能：为solvecube_data赋值
 *输    入: array:二维数组指针,start_num:数组开始标号,end_num:数组结束标号
 *输    出: 无
 *调用情况: 被Analy_UsartString()函数调用
 *备    注：数组的列数为8，二维数组指针作为函数参数时列数要确定
 */
void Initial_Data(u16 (*array)[8],u8 start_line,u8 end_line)
{
	u8 i,j;
	
	for(j=start_line;j<=end_line;j++)
	{
		
		for(i=0;i<8;i++)
		{
			solvecube_data[j][i]=*(*(array+(j-start_line))+i);			
		}
	
	}
}



