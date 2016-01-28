package com.digdream.androidrubiksolver.scramble;

public class ScrambleGeneratorNxNxN {
  private ScrambleGeneratorNxNxN() {
    
  }
  
  public static ScrambleGeneratorNxNxN getInstance() {
    if(instance == null)
	  instance = new ScrambleGeneratorNxNxN();
	return instance;
  }

  public String nextScramble(int size, boolean mult, int seqlen) {
    scramble(size, mult, seqlen);
	return scramblestring(size, mult);
  }
  
  private void scramble(int size, boolean mult, int seqlen) {
	int tl = size;
	if(mult || (size&1)!=0 ) tl--;
	//set up bookkeeping
	int axsl[]=new int[tl];    // movement of each slice/movetype on this axis
	int axam[]=new int[] {0, 0, 0}; // number of slices moved each amount
	int la; // last axis moved
 
	// for each cube scramble
	// for( n=0; n<numcub; n++){
		// initialise this scramble
		la=-1;
		seq=new int[seqlen]; // moves generated so far
		seq_length = 0;
		// reset slice/direction counters
		for( int i=0; i<tl; i++) axsl[i]=0;
		axam[0]=axam[1]=axam[2]=0;
		int moved = 0;
 
		// while generated sequence not long enough
		while( seq_length + moved <seqlen ){
 
			int ax, sl, q;
			do{
				do{
					// choose a random axis
					ax=new Double (Math.floor(Math.random()*3)).intValue();
					// choose a random move type on that axis
					sl=new Double (Math.floor(Math.random()*tl)).intValue();
					// choose random amount
					q=new Double (Math.floor(Math.random()*3)).intValue();
				}while( ax==la && axsl[sl]!=0 );		// loop until have found an unused movetype
			}while( ax==la					// loop while move is reducible: reductions only if on same axis as previous moves
					&& !mult				// multislice moves have no reductions so always ok
					&& tl==size				// only even-sized cubes have reductions (odds have middle layer as reference)
					&& (
						2*axam[0]==tl ||	// reduction if already have half the slices move in same direction
						2*axam[1]==tl ||
						2*axam[2]==tl ||
						(
							2*(axam[q]+1)==tl	// reduction if move makes exactly half the slices moved in same direction and
							&&
							axam[0]+axam[1]+axam[2]-axam[q] > 0 // some other slice also moved
						)
				    )
			);
 
			// if now on different axis, dump cached moves from old axis
			if( ax!=la ) {
				seq_length = appendmoves( seq, seq_length, axsl, tl, la );
				// reset slice/direction counters
				for( int i=0; i<tl; i++) axsl[i]=0;
				axam[0]=axam[1]=axam[2]=0;
				moved = 0;
				// remember new axis
				la=ax;
			}
 
			// adjust counters for this move
			axam[q]++;// adjust direction count
			moved++;
			axsl[sl]=q+1;// mark the slice has moved amount
 
		}
		// dump the last few moves
		appendmoves( seq, seq_length, axsl, tl, la );
 
		// do a random cube orientation if necessary
		// seq[seq_length]= cubeorient ? Math.floor(Math.random()*24) : 0;
	// }
 
	// build lookup table
	int flat2posit[]=new int[12*size*size];
	for(int i=0; i<flat2posit.length; i++) flat2posit[i]=-1;
	for(int i=0; i<size; i++){
		for(int j=0; j<size; j++){
			flat2posit[4*size*(3*size-i-1)+  size+j  ]=        i *size+j;	//D
			flat2posit[4*size*(  size+i  )+  size-j-1]=(  size+i)*size+j;	//L
			flat2posit[4*size*(  size+i  )+4*size-j-1]=(2*size+i)*size+j;	//B
			flat2posit[4*size*(       i  )+  size+j  ]=(3*size+i)*size+j;	//U
			flat2posit[4*size*(  size+i  )+2*size+j  ]=(4*size+i)*size+j;	//R
			flat2posit[4*size*(  size+i  )+  size+j  ]=(5*size+i)*size+j;	//F
		}
	}
 
/*
       19                32
   16           48           35
       31   60      51   44
   28     80    63    67     47
              83  64
          92          79
              95  76
 
                 0
             12     3
                15
*/
  }
  
  private int appendmoves( int sq[], int sq_length, int axsl[], int tl, int la ) {
	for( int sl=0; sl<tl; sl++){	// for each move type
		if( axsl[sl] != 0 ){				// if it occurs
			int q=axsl[sl]-1;
 
			// get semi-axis of this move
			int sa = la;
			int m = sl;
			if(sl+sl+1>=tl){ // if on rear half of this axis
				sa+=3; // get semi-axis (i.e. face of the move)
				m=tl-1-m; // slice number counting from that face
				q=2-q; // opposite direction when looking at that face
			}
			// store move
			sq[sq_length++]=(m*6+sa)*4+q;
		}
	}
	return sq_length;
  }

  private String scramblestring(int size, boolean mult) {
	String s="";
	int j;
	for(int i=0; i<seq_length-1; i++){
		if( i!=0 ) s+=" ";
		int k=seq[i]>>2;
 
		j=k%6; k=(k-j)/6;
		if( k!=0 && size<=5 && !mult ) {
			s+="dlburf".charAt(j);	// use lower case only for inner slices on 4x4x4 or 5x5x5
		}else{
			if(size<=5 && mult ){
				s+="DLBURF".charAt(j);
				if(k!=0) s+="w";	// use w only for double layers on 4x4x4 and 5x5x5
			}
			else{
				if(k!=0)	s+=(k+1);
				s+="DLBURF".charAt(j);
			}
		}
 
		j=seq[i]&3;
		if(j!=0) s+=" 2'".charAt(j);
	}
 
	// add cube orientation
	// if( cubeorient ){
	//	var ori = seq[n][seq[n].length-1];
	//	s="Top:"+colorList[ 2+colors[colorPerm[ori][3]] ]
	//		+"&nbsp;&nbsp;&nbsp;Front:"+colorList[2+ colors[colorPerm[ori][5]] ]+"<br>"+s;
	// }
	return s;
  }

  private static ScrambleGeneratorNxNxN instance = null;
  
  private int seq[];
  private int seq_length;
}
