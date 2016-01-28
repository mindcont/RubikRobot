package com.digdream.androidrubiksolver.scramble;

public class ScrambleGenerator2x2x2 {
	private ScrambleGenerator2x2x2() {
		initbrd();
		calcperm();
		initialize();
	}

	public static ScrambleGenerator2x2x2 getInstance() {
		if (instance == null)
			instance = new ScrambleGenerator2x2x2();
		return instance;
	}

	public String nextScramble() {
		mix2();
		return solve();
	}

	private int[] posit = new int[24];

	private void initbrd() {
		posit = new int[] { 1, 1, 1, 1, 2, 2, 2, 2, 5, 5, 5, 5, 4, 4, 4, 4, 3,
				3, 3, 3, 0, 0, 0, 0 };
	}

	private int sol[] = new int[16];
	private int sol_length = 0;
	private int seqlen = 0;
	private int piece[] = new int[] { 15, 16, 16, 21, 21, 15, 13, 9, 9, 17, 17,
			13, 14, 20, 20, 4, 4, 14, 12, 5, 5, 8, 8, 12, 3, 23, 23, 18, 18, 3,
			1, 19, 19, 11, 11, 1, 2, 6, 6, 22, 22, 2, 0, 10, 10, 7, 7, 0 };
	int adj[][] = new int[6][6];

	private String solve() {
		calcadj();
		int opp[] = new int[6];
		for (int a = 0; a < 6; a++) {
			for (int b = 0; b < 6; b++) {
				if (a != b && adj[a][b] + adj[b][a] == 0) {
					opp[a] = b;
					opp[b] = a;
				}
			}
		}
		// Each piece is determined by which of each pair of opposite colours it
		// uses.
		int ps[] = new int[8];
		int tws[] = new int[8];
		{
			int a = 0;
			for (int d = 0; d < 7; d++) {
				int p = 0;
				for (int b = a; b < a + 6; b += 2) {
					if (posit[piece[b]] == posit[piece[42]])
						p += 4;
					if (posit[piece[b]] == posit[piece[44]])
						p += 1;
					if (posit[piece[b]] == posit[piece[46]])
						p += 2;
				}
				ps[d] = p;
				if (posit[piece[a]] == posit[piece[42]]
						|| posit[piece[a]] == opp[posit[piece[42]]])
					tws[d] = 0;
				else if (posit[piece[a + 2]] == posit[piece[42]]
						|| posit[piece[a + 2]] == opp[posit[piece[42]]])
					tws[d] = 1;
				else
					tws[d] = 2;
				a += 6;
			}
		}
		// convert position to numbers
		int q = 0;
		{
			for (int a = 0; a < 7; a++) {
				int b = 0;
				for (int c = 0; c < 7; c++) {
					if (ps[c] == a)
						break;
					if (ps[c] > a)
						b++;
				}
				q = q * (7 - a) + b;
			}
		}
		int t = 0;
		{
			for (int a = 5; a >= 0; a--) {
				t = t
						* 3
						+ tws[a]
						- 3
						* new Double(Math.floor(new Double(tws[a]) / 3.0d))
								.intValue();
			}
		}

		if (q != 0 || t != 0) {
			sol_length = 0;
			for (int l = seqlen; l < 100; l++) {
				if (search(0, q, t, l, -1))
					break;
			}
			String s = "";
			for (q = 0; q < sol_length; q++) {
				s = "URF".substring(sol[q] / 10, sol[q] / 10 + 1)
						+ "\'2 ".substring(sol[q] % 10, sol[q] % 10 + 1) + " "
						+ s;
			}
			return s;
		}

		return "???";
	}

	private void calcadj() {
		// count all adjacent pairs (clockwise around corners)
		int a, b;
		for (a = 0; a < 6; a++)
			for (b = 0; b < 6; b++)
				adj[a][b] = 0;
		for (a = 0; a < 48; a += 2) {
			if (posit[piece[a]] <= 5 && posit[piece[a + 1]] <= 5)
				adj[posit[piece[a]]][posit[piece[a + 1]]]++;
		}
	}

	private boolean search(int d, int q, int t, int l, int lm) {
		// searches for solution, from position q|t, in l moves exactly. last
		// move was lm, current depth=d
		if (l == 0) {
			if (q == 0 && t == 0) {
				return (true);
			}
		} else {
			if (perm[q] > l || twst[t] > l)
				return (false);
			int p, s, a, m;
			for (m = 0; m < 3; m++) {
				if (m != lm) {
					p = q;
					s = t;
					for (a = 0; a < 3; a++) {
						p = permmv[p][m];
						s = twstmv[s][m];
						sol[d] = 10 * m + a;
						sol_length = d + 1;
						if (search(d + 1, p, s, l - 1, m))
							return (true);
					}
				}
			}
		}
		return (false);
	}

	private void mix2() {
		// Fixed cubie
		int fixed = 6;
		// Generate random permutation
		int perm_src[] = new int[] { 0, 1, 2, 3, 4, 5, 6, 7 };
		int perm_sel[] = new int[8];
		for (int i = 0; i < 7; i++) {
			int ch = new Double(Math.floor(Math.random() * new Double(7 - i)))
					.intValue();
			ch = perm_src[ch] == fixed ? (ch + 1) % (8 - i) : ch;
			perm_sel[i >= fixed ? i + 1 : i] = perm_src[ch];
			perm_src[ch] = perm_src[7 - i];
		}
		perm_sel[fixed] = fixed;
		// Generate random orientation
		int total = 0;
		int ori_sel[] = new int[8];
		int i = fixed == 0 ? 1 : 0;
		for (; i < 7; i = i == fixed - 1 ? i + 2 : i + 1) {
			ori_sel[i] = new Double(Math.floor(Math.random() * 3.0d))
					.intValue();
			total += ori_sel[i];
		}
		if (i <= 7)
			ori_sel[i] = (3 - (total % 3)) % 3;
		ori_sel[fixed] = 0;

		// Convert to face format
		// Mapping from permutation/orientation to facelet
		int D = 1, L = 2, B = 5, U = 4, R = 3, F = 0;
		// D 0 1 2 3 L 4 5 6 7 B 8 9 10 11 U 12 13 14 15 R 16 17 18 19 F 20 21
		// 22 23
		// Map from permutation/orientation to face
		int fmap[][] = new int[][] { new int[] { U, R, F },
				new int[] { U, B, R }, new int[] { U, L, B },
				new int[] { U, F, L }, new int[] { D, F, R },
				new int[] { D, R, B }, new int[] { D, B, L },
				new int[] { D, L, F } };
		// Map from permutation/orientation to facelet identifier
		int pos[][] = new int[][] { new int[] { 15, 16, 21 },
				new int[] { 13, 9, 17 }, new int[] { 12, 5, 8 },
				new int[] { 14, 20, 4 }, new int[] { 3, 23, 18 },
				new int[] { 1, 19, 11 }, new int[] { 0, 10, 7 },
				new int[] { 2, 6, 22 } };
		// Convert cubie representation into facelet representaion
		for (int ii = 0; ii < 8; ii++) {
			for (int j = 0; j < 3; j++)
				posit[pos[ii][(ori_sel[ii] + j) % 3]] = fmap[perm_sel[ii]][j];
		}
	}

	private int perm[];
	private int permmv[][];
	private int twst[];
	private int twstmv[][];

	private void calcperm() {
		// calculate solving arrays
		// first permutation

		perm = new int[5040];
		permmv = new int[5040][3];

		for (int p = 0; p < 5040; p++) {
			perm[p] = -1;
			for (int m = 0; m < 3; m++) {
				permmv[p][m] = getprmmv(p, m);
			}
		}

		perm[0] = 0;
		for (int l = 0; l <= 6; l++) {
			int n = 0;
			for (int p = 0; p < 5040; p++) {
				if (perm[p] == l) {
					for (int m = 0; m < 3; m++) {
						int q = p;
						for (int c = 0; c < 3; c++) {
							q = permmv[q][m];
							if (perm[q] == -1) {
								perm[q] = l + 1;
								n++;
							}
						}
					}
				}
			}
		}

		// then twist

		twst = new int[729];
		twstmv = new int[729][3];

		for (int p = 0; p < 729; p++) {
			twst[p] = -1;
			// twstmv[p]=new Array();
			for (int m = 0; m < 3; m++) {
				twstmv[p][m] = gettwsmv(p, m);
			}
		}

		twst[0] = 0;
		for (int l = 0; l <= 5; l++) {
			int n = 0;
			for (int p = 0; p < 729; p++) {
				if (twst[p] == l) {
					for (int m = 0; m < 3; m++) {
						int q = p;
						for (int c = 0; c < 3; c++) {
							q = twstmv[q][m];
							if (twst[q] == -1) {
								twst[q] = l + 1;
								n++;
							}
						}
					}
				}
			}
		}
		// remove wait sign
	}

	private int getprmmv(int p, int m) {
		// given position p<5040 and move m<3, return new position number
		int a, b, c, q;
		// convert number into array;
		int ps[] = new int[8];
		q = p;
		for (a = 1; a <= 7; a++) {
			b = q % a;
			q = (q - b) / a;
			for (c = a - 1; c >= b; c--)
				ps[c + 1] = ps[c];
			ps[b] = 7 - a;
		}
		// perform move on array
		if (m == 0) {
			// U
			c = ps[0];
			ps[0] = ps[1];
			ps[1] = ps[3];
			ps[3] = ps[2];
			ps[2] = c;
		} else if (m == 1) {
			// R
			c = ps[0];
			ps[0] = ps[4];
			ps[4] = ps[5];
			ps[5] = ps[1];
			ps[1] = c;
		} else if (m == 2) {
			// F
			c = ps[0];
			ps[0] = ps[2];
			ps[2] = ps[6];
			ps[6] = ps[4];
			ps[4] = c;
		}
		// convert array back to number
		q = 0;
		for (a = 0; a < 7; a++) {
			b = 0;
			for (c = 0; c < 7; c++) {
				if (ps[c] == a)
					break;
				if (ps[c] > a)
					b++;
			}
			q = q * (7 - a) + b;
		}
		return (q);
	}

	private int gettwsmv(int p, int m) {
		// given orientation p<729 and move m<3, return new orientation number
		int a, b, c, d, q;
		// convert number into array;
		int ps[] = new int[8];
		q = p;
		d = 0;
		for (a = 0; a <= 5; a++) {
			c = new Double(Math.floor(new Double(new Double(q) / 3.0d)))
					.intValue();
			b = q - 3 * c;
			q = c;
			ps[a] = b;
			d -= b;
			if (d < 0)
				d += 3;
		}
		ps[6] = d;
		// perform move on array
		if (m == 0) {
			// U
			c = ps[0];
			ps[0] = ps[1];
			ps[1] = ps[3];
			ps[3] = ps[2];
			ps[2] = c;
		} else if (m == 1) {
			// R
			c = ps[0];
			ps[0] = ps[4];
			ps[4] = ps[5];
			ps[5] = ps[1];
			ps[1] = c;
			ps[0] += 2;
			ps[1]++;
			ps[5] += 2;
			ps[4]++;
		} else if (m == 2) {
			// F
			c = ps[0];
			ps[0] = ps[2];
			ps[2] = ps[6];
			ps[6] = ps[4];
			ps[4] = c;
			ps[2] += 2;
			ps[0]++;
			ps[4] += 2;
			ps[6]++;
		}
		// convert array back to number
		q = 0;
		for (a = 5; a >= 0; a--) {
			q = q * 3 + (ps[a] % 3);
		}
		return (q);
	}

	private int size = 2;
	private int flat2posit[];

	// generate sequence of scambles
	private void initialize() {

		// build lookup table
		flat2posit = new int[12 * size * size];
		for (int i = 0; i < flat2posit.length; i++)
			flat2posit[i] = -1;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				flat2posit[4 * size * (3 * size - i - 1) + size + j] = i * size
						+ j; // D
				flat2posit[4 * size * (size + i) + size - j - 1] = (size + i)
						* size + j; // L
				flat2posit[4 * size * (size + i) + 4 * size - j - 1] = (2 * size + i)
						* size + j; // B
				flat2posit[4 * size * (i) + size + j] = (3 * size + i) * size
						+ j; // U
				flat2posit[4 * size * (size + i) + 2 * size + j] = (4 * size + i)
						* size + j; // R
				flat2posit[4 * size * (size + i) + size + j] = (5 * size + i)
						* size + j; // F
			}
		}

		/*
		 * 19 32 16 48 35 31 60 51 44 28 80 63 67 47 83 64 92 79 95 76
		 * 
		 * 0 12 3 15
		 */
	}

	private static ScrambleGenerator2x2x2 instance = null;

	public static void main(String args[]) {
		ScrambleGenerator2x2x2 sg2x2x2 = new ScrambleGenerator2x2x2();
		System.out.println(sg2x2x2.nextScramble());
	}
}
