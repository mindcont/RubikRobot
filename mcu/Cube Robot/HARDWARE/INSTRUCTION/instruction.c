/*  文件名: instruction.c
 *文件功能：字符串解析
 *备    注：字符串命名规则:开始标志位:#,结束标志位:!
 *          单个大写字母90度旋转，大写字母后面带数字2代表180度旋转,后面带'的为逆时针，不带'的为顺时针
 *          U:上面,D:下面
 *          L:左面,R:右面
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
	u16 startline_num=0; /*开始为solvecube_data数组赋值的数组标号*/
	u8 mix_sig=0;
	while(rece_string[i]!='!')
	{
	
			if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(0x41<=rece_string[i+1]&&rece_string[i+1]<=0x5a))   /*当前字符为大写字母，下一个字符为大写字母*/
			{
				
				if(Ana_Double2(rece_string[i],rece_string[i+1]))
				{			
					   if((rece_string[i+2]=='2')||(rece_string[i+2]==0x27)) //3位
						 {
							 mix_sig=Instruction_movement(Get_Movement1_1(rece_string[i],rece_string[i+1],rece_string[i+2]),startline_num);
							 i+=mix_sig/10;
					     startline_num+=(mix_sig%10+1);
						 }
						 else //2位
						 {
							mix_sig=Instruction_movement(Get_Movement1_2(rece_string[i],rece_string[i+1]),startline_num);
							i+=mix_sig/10;
					    startline_num+=(mix_sig%10+1);
 
						 }
				}
				else
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
					
			}
			
			
			else if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(rece_string[i+1]==0x27))    /*当前字符为大写字母，下一个字符为字符’*/
			{	
				
				if(Ana_Double2(rece_string[i],rece_string[i+2]))
				{
						if((rece_string[i+3]=='2')||(rece_string[i+3]==0x27)) //4位
						{
							mix_sig=Instruction_movement(Get_Movement2_2(rece_string[i],rece_string[i+2],rece_string[i+3]),startline_num);
							i+=mix_sig/10;
					    startline_num+=(mix_sig%10+1);						
					
						}
						else//3位						
						{
							mix_sig=Instruction_movement(Get_Movement2_1(rece_string[i],rece_string[i+2]),startline_num);	
	            i+=mix_sig/10;
					    startline_num+=(mix_sig%10+1);						
						}

				}
				else
				{
							switch(rece_string[i])
								 {
											case 'U':
											{	
													if(rece_string[i+2]=='!')
													{
													Initial_Data(u_anticlockwise90,startline_num,startline_num+9);
													startline_num+=10;
													}
													else 
													{
													Initial_Data(u_anticlockwise90,startline_num,startline_num+15);
													startline_num+=16;
													}
											}break;
											
											case 'D':
											{
													if(rece_string[i+2]=='!')
													{
													Initial_Data(d_anticlockwise90,startline_num,startline_num+9);
													startline_num+=10;
													}
													else 
													{
													Initial_Data(d_anticlockwise90,startline_num,startline_num+15);
													startline_num+=16;
													}
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
									 
			}
			
			
		 else if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(rece_string[i+1]==0x32))    /*当前字符为大写字母，下一个字符为数字2*/
			{
				
				if(Ana_Double2(rece_string[i],rece_string[i+2]))
				{
					
						if((rece_string[i+3]=='2')||(rece_string[i+3]==0x27)) //4位
						{
							mix_sig=Instruction_movement(Get_Movement3_2(rece_string[i],rece_string[i+2],rece_string[i+3]),startline_num);
							i+=mix_sig/10;
					    startline_num+=(mix_sig%10+1);						

						}
						else  //3位
						{
							mix_sig=Instruction_movement(Get_Movement3_1(rece_string[i],rece_string[i+2]),startline_num);
							i+=mix_sig/10;
					    startline_num+=(mix_sig%10+1);		
							
						}

						
				}
				
				else{
				
							switch(rece_string[i])
								{
											case 'U':
											{
													if(rece_string[i+2]=='!')
													{
													Initial_Data(u_clock180,startline_num,startline_num+13);
													startline_num+=14;
													}
													else
													{
													Initial_Data(u_clock180,startline_num,startline_num+19);
													startline_num+=20;
													}
											}break;
											
											case 'D':
											{
													if(rece_string[i+2]=='!')
													{
													Initial_Data(d_clock180,startline_num,startline_num+13);
													startline_num+=14;
													}
													else
													{
													Initial_Data(d_clock180,startline_num,startline_num+19);
													startline_num+=20;
													}
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
							
		  }
			
			else if((0x41<=rece_string[i]&&rece_string[i]<=0x5a)&&(rece_string[i+1]=='!'))
			{
							
							
									switch(rece_string[i])
									{
												case 'U':
												{
													Initial_Data(u_clockwise90,startline_num,startline_num+9);
													startline_num+=10;
												}break;
												
												case 'D':
												{
													Initial_Data(d_clockwise90,startline_num,startline_num+9);
													startline_num+=10;					
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
void Initial_Data(u16 (*array)[8],u16 start_line,u16 end_line)
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



u8 Ana_Double2(u8 char1,u8 char2)
{
	u8 right_flag;
		
				if((char1=='L'&&char2=='R')||(char1=='R'&&char2=='L')||(char1=='F'&&char2=='B')||(char1=='B'&&char2=='F'))
				{
					right_flag=1;
				}	
				else 
				{
					right_flag=0;
				}
	
	return right_flag;
	
}





/*  函数名：Ana_Double(，,,)
 *函数功能：判断是不是满足条件的指令
 *输    入: 
 *输    出: 
 *调用情况:被Get_Movement()调用
 *备    注：
 */
/*u16 Ana_Double(u8 char1,u8 char2,u8 char3,u8 char4)
{
		u8 flag_bit=0;
	  u8 flag_second=0;
		u8 flag_third=0;
		u8 flag_four=0;

		if(char1=='L'||char1=='R'||char1=='F'||char1=='B')
		{
					
				if(char2=='2')
			    {

									if(char3=='L'||char3=='R'||char3=='F'||char3=='B')
									{
										
										if(Ana_Double2(char1,char3))
										{
													if(char4=='2')
													{
														flag_bit=4;
														flag_second=3;
														flag_third=1;	
														flag_four=3;	
													}
													else if(char4==0x27)
													{
														flag_bit=4;
														flag_second=3;
														flag_third=1;	
														flag_four=2;	

													}
													else
													{
														flag_bit=3;
														flag_second=3;
														flag_third=1;													 
													}
									  }											
																		
										else
										{
											flag_bit=0;
										}
											
											
							   }
									
								 else
								 {
										flag_bit=0;
								 }
							
			   }
			
			
			else if(char2==0x27)
			{	
		
					  if(char3=='L'||char3=='R'||char3=='F'||char3=='B')//L'R
					  {
								
										if(Ana_Double2(char1,char3))
										{
													if(char4=='2')//L'R2
													{
															flag_bit=4;
															flag_second=2;
															flag_third=1;	
															flag_four=3;	
													}
													else if(char4==0x27) //L'R'
													{
															flag_bit=4;
															flag_second=2;
															flag_third=1;	
															flag_four=2;	

													}
													else           //L'R
													{
															flag_bit=3;
															flag_second=2;
															flag_third=1;													 
													}
										}											
																		
										else
										{
											flag_bit=0;
										}
												
					   }
							
						else
						{
							flag_bit=0;
						}

			}
		 
			
			else if(char2=='L'||char2=='R'||char2=='F'||char2=='B')
			{
						
							if(Ana_Double2(char1,char2))
							{
								  if(char3=='2')
									{
											flag_bit=3;  //LR2
											flag_second=1;
											flag_third=3;
									
									}
									else if(char3==0x27)
									{
											flag_bit=3;//LR'
											flag_second=1;
											flag_third=2;

									}
									else
									{
											flag_bit=2;//LR
											flag_second=1;
									}
								
							}
							else
							{
								flag_bit=0;							
							}
							
			}
		 
			else 
			{
				flag_bit=0;
			}
		
		}
		
		
		else
		{
			flag_bit=0;
		}		
		
	return(flag_bit*1000+flag_second*100+flag_third*10+flag_four);
		
}
*/



/*  函数名：Get_Movement(,,,)
 *函数功能：对应不同四个指令的获取不同的动作组
 *输    入: 
 *输    出: 
 *调用情况:
 *备    注：
 */
/*u16 Get_Movement(u8 char1,u8 char2,u8 char3,u8 char4)
{
	
		u8 flag_bit=0;
	  u8 flag_second=0;
		u8 flag_third=0;
		u8 flag_four=0;
	  u8 movement_flag=0;
	
	  u16 secert=Ana_Double(char1,char2,char3,char4);
	
	  flag_bit=secert/1000;
	  flag_second=secert/100%10;
	  flag_third=secert/10%10;
	  flag_four=secert%10;
	
		if(flag_bit==2)
		{
				if((char1=='L'&&char2=='R')||(char1=='R'&&char2=='L'))
				{
						movement_flag=1;
				}
				if((char1=='B'&&char2=='F')||(char1=='F'&&char2=='B'))
				{
						movement_flag=10;
				}

		
		}
		
		else if(flag_bit==3)
		{
				if(flag_second==2)//L'
				{
						if(flag_third==1)//L'R
						{
							
									if((char1=='L'&&char3=='R'))
									{
											movement_flag=4;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=13;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=2;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=11;
									}	
									
						}
						else
						{
							movement_flag=0;
						}
				}
				
				else if(flag_second==1)//LL
				{
					
						if(flag_third==2)//LR'
						{
									if((char1=='L'&&char2=='R'))
									{
											movement_flag=2;
									}
									if((char1=='F'&&char2=='B'))
									{
											movement_flag=11;
									}
									
									if((char1=='R'&&char2=='L'))
									{
											movement_flag=4;
									}
									if((char1=='B'&&char2=='F'))
									{
											movement_flag=13;
									}
						}
						
						else if(flag_third==3)//LR2
						{
									if((char1=='L'&&char2=='R'))
									{
											movement_flag=3;
									}
									if((char1=='F'&&char2=='B'))
									{
											movement_flag=12;
									}
									
									if((char1=='R'&&char2=='L'))
									{
											movement_flag=7;
									}
									if((char1=='B'&&char2=='F'))
									{
											movement_flag=16;
									}	
						}
						
						else 
						{
							movement_flag=0;
						}
				}
				
				else if(flag_second==3)//L2
				{
					
					        if((char1=='L'&&char3=='R'))
									{
											movement_flag=7;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=16;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=3;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=12;
									}	
				}
				
				else
				{
					movement_flag=0;
				}
		
		}
		
		else if(flag_bit==4)
		{
					
					if(flag_second==2&&flag_four==2)//L'R'
					{
							if((char1=='L'&&char3=='R')||(char1=='R'&&char3=='L'))
							{
									movement_flag=5;
							}
							if((char1=='B'&&char3=='F')||(char1=='F'&&char3=='B'))//F'B'
							{
									movement_flag=14;
							}			
					}
					else if(flag_second==2&&flag_four==3)//L'R2
					{
						
							  if((char1=='L'&&char3=='R'))
									{
											movement_flag=6;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=15;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=8;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=17;
									}	
					
					}
					
					else if(flag_second==3&&flag_four==2)//L2R'
					{
						
								  if((char1=='L'&&char3=='R'))
									{
											movement_flag=8;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=17;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=6;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=15;
									}	
					
					}
					
					else if(flag_second==3&&flag_four==3)//L2R2
					{
									
							if((char1=='L'&&char3=='R')||(char1=='R'&&char3=='L'))
							{
									movement_flag=9;
							}
							if((char1=='B'&&char3=='F')||(char1=='F'&&char3=='B'))
							{
									movement_flag=18;
							}
	
					}
					
					else 
					{
							movement_flag=0;
					}
		}
		

	 return movement_flag;
}*/



/*  函数名：Instruction_movement(movement_instruction,startline_num)
 *函数功能：根据不同的类似L'R'指令来调用不同的舵机执行数组
 *输    入: movement_instruction为指令编号，startline_num为solvecube_data数组赋值的开始行号
 *输    出: move_bit和line_add的组合，line_add是solvecube_data数组需要增加的行数
 *调用情况:	被Analy_UsartString()调用
 *备    注：
 */
u8  Instruction_movement(u8 movement_instruction,u16 startline_num)
{
	
							u8 move_bit=0;
							u8 line_add=0;
							switch(movement_instruction)
							{
								case 1:
								{
								Initial_Data(double_movement1,startline_num,startline_num+3);
                move_bit=2;
								line_add=3;
								}break;
								
								case 2:
								{
								Initial_Data(double_movement2,startline_num,startline_num+3);
							  move_bit=3;
								line_add=3;	
								}break;
								
								case 3:
								{
								Initial_Data(double_movement3,startline_num,startline_num+7);
							  move_bit=3;						
								line_add=7;
								}break;
								
								case 4:
								{
								Initial_Data(double_movement4,startline_num,startline_num+3);
							  move_bit=3;
					      line_add=3;
								}break;
								
								case 5:
								{
								Initial_Data(double_movement5,startline_num,startline_num+3);
							  move_bit=4;
								line_add=3;
								}break;
								
								case 6:
								{
								Initial_Data(double_movement6,startline_num,startline_num+7);
							  move_bit=4;
								line_add=7;
								}break;
								case 7:
								{
								Initial_Data(double_movement7,startline_num,startline_num+7);
							  move_bit=3;		
								line_add=7;									
								}break;
								
								case 8:
								{
								Initial_Data(double_movement8,startline_num,startline_num+7);
							  move_bit=4;	
								line_add=7;									
								}break;
								
								case 9:
								{
								Initial_Data(double_movement9,startline_num,startline_num+7);
							  move_bit=4;		
								line_add=7;									
								}break;
								
								case 10:
								{
								Initial_Data(double_movement10,startline_num,startline_num+3);
							  move_bit=2;
								line_add=3;
									
								}break;
								
								case 11:
								{
								Initial_Data(double_movement11,startline_num,startline_num+3);
							  move_bit=3;	
								line_add=3;
									
								}break;
								
								case 12:
								{
								Initial_Data(double_movement12,startline_num,startline_num+7);
						    move_bit=3;	
								line_add=7;
									
								}break;
								
								case 13:
								{
								Initial_Data(double_movement13,startline_num,startline_num+3);
							  move_bit=3;
								line_add=3;
									
								}break;
								
								case 14:
								{
								Initial_Data(double_movement14,startline_num,startline_num+3);
							  move_bit=4;	
								line_add=3;
									
								}break;
								
								case 15:
								{
									Initial_Data(double_movement15,startline_num,startline_num+7);
							  move_bit=4;		
								line_add=7;
									
								}break;
								case 16:
								{
								Initial_Data(double_movement16,startline_num,startline_num+7);
							  move_bit=3;
								line_add=7;
									
								}break;
								
								case 17:
								{
								Initial_Data(double_movement17,startline_num,startline_num+7);
							  move_bit=4;	
								line_add=7;
									
								}break;
								
								case 18:
								{
								Initial_Data(double_movement18,startline_num,startline_num+7);
							  move_bit=4;	
								line_add=7;									
								}break;
								
								default:
								{
									
								}break;

				   }	
     return move_bit*10+line_add;							
			
}






/*  函数名：Get_Movement1_1(char1,char2,char3)
 *函数功能：根据char1,char2和char3的不同组合来确定指令编号
 *输    入: char1为第一个字符，char2为第二个字符，char3为第三个字符
 *输    出: 指令编号
 *调用情况:	被Instruction_movement()调用
 *备    注：LR2 或者LR'
 */
u8 Get_Movement1_1(u8 char1,u8 char2,u8 char3)
{  
	  u8 movement_flag;	

						if(char3==0x27)//LR'
						{
									if((char1=='L'&&char2=='R'))
									{
											movement_flag=2;
									}
									if((char1=='F'&&char2=='B'))
									{
											movement_flag=11;
									}
									
									if((char1=='R'&&char2=='L'))
									{
											movement_flag=4;
									}
									if((char1=='B'&&char2=='F'))
									{
											movement_flag=13;
									}
						}
						
						else if(char3=='2')//LR2
						{
									if((char1=='L'&&char2=='R'))
									{
											movement_flag=3;
									}
									if((char1=='F'&&char2=='B'))
									{
											movement_flag=12;
									}
									
									if((char1=='R'&&char2=='L'))
									{
											movement_flag=7;
									}
									if((char1=='B'&&char2=='F'))
									{
											movement_flag=16;
									}	
									
						}
		return movement_flag;					

}


/*  函数名: Get_Movement1_2(char1,char2)
 *函数功能: 根据char1,char2的不同组合来确定指令编号
 *输    入: char1为第一个字符，char2为第二个字符
 *输    出: 指令编号
 *调用情况: 被Instruction_movement()调用
 *备    注：LR
 */
u8 Get_Movement1_2(u8 char1,u8 char2)
{  
	  u8 movement_flag;	
	
			if((char1=='L'&&char2=='R')||(char1=='R'&&char2=='L'))
			{
			movement_flag=1;
			}
			if((char1=='B'&&char2=='F')||(char1=='F'&&char2=='B'))
			{
			movement_flag=10;
			}	
			
		return movement_flag;					
}

			

/*  函数名: Get_Movement1_2(char1,char3)
 *函数功能: 根据char1,char3的不同组合来确定指令编号
 *输    入: char1为第一个字符，char3为第三个字符
 *输    出: 指令编号
 *调用情况: 被Instruction_movement()调用
 *备    注：R'L
 */
u8 Get_Movement2_1(u8 char1,u8 char3)
{  
	  u8 movement_flag;	
	
	 								if((char1=='L'&&char3=='R'))
									{
											movement_flag=4;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=13;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=2;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=11;
									}	
									
		return movement_flag;					

}


/*  函数名: Get_Movement2_2(char1,char3，char4)
 *函数功能: 根据char1,char3，char4的不同组合来确定指令编号
 *输    入: char1为第一个字符，char3为第三个字符,char4位第四个字符
 *输    出: 指令编号
 *调用情况: 被Instruction_movement()调用
 *备    注：4位
 */
u8 Get_Movement2_2(u8 char1,u8 char3,u8 char4)
{  
					u8 movement_flag;	
	
					if(char4==0x27) //L'R'
					{
							if((char1=='L'&&char3=='R')||(char1=='R'&&char3=='L'))
							{
									movement_flag=5;
							}
							if((char1=='B'&&char3=='F')||(char1=='F'&&char3=='B'))//F'B'
							{
									movement_flag=14;
							}			
					}
					else if(char4=='2') //L'R2
					{
						
							  if((char1=='L'&&char3=='R'))
									{
											movement_flag=6;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=15;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=8;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=17;
									}	
					
					}
	
		return movement_flag;					

}


/*  函数名: Get_Movement3_1(char1,char3)
 *函数功能: 根据char1,char3的不同组合来确定指令编号
 *输    入: char1为第一个字符，char3为第三个字符
 *输    出: 指令编号
 *调用情况: 被Instruction_movement()调用
 *备    注：3位,L2R
 */
u8 Get_Movement3_1(u8 char1,u8 char3)
{  
	  u8 movement_flag;	
	
						      if((char1=='L'&&char3=='R'))
									{
											movement_flag=7;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=16;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=3;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=12;
									}	
		
		return movement_flag;					

}




/*  函数名: Get_Movement3_2(char1,char3,char4)
 *函数功能: 根据char1,char3,char4的不同组合来确定指令编号
 *输    入: char1为第一个字符，char3为第三个字符,char4为第四个字符
 *输    出: 指令编号
 *调用情况: 被Instruction_movement()调用
 *备    注：4位,R2L
 */
u8 Get_Movement3_2(u8 char1,u8 char3,u8 char4 )
{  
	        u8 movement_flag;	
					if(char4==0x27)//L2R'
					{
						
								  if((char1=='L'&&char3=='R'))
									{
											movement_flag=8;
									}
									if((char1=='F'&&char3=='B'))
									{
											movement_flag=17;
									}
									
									if((char1=='R'&&char3=='L'))
									{
											movement_flag=6;
									}
									if((char1=='B'&&char3=='F'))
									{
											movement_flag=15;
									}	
					
					}
					
					else if(char4=='2')//L2R2
					{
									
							if((char1=='L'&&char3=='R')||(char1=='R'&&char3=='L'))
							{
									movement_flag=9;
							}
							if((char1=='B'&&char3=='F')||(char1=='F'&&char3=='B'))
							{
									movement_flag=18;
							}
	
					}
		return movement_flag;				
					
}







 

