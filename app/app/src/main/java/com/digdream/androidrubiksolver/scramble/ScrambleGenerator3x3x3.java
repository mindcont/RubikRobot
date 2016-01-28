package com.digdream.androidrubiksolver.scramble;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class ScrambleGenerator3x3x3 {
	private ScrambleGenerator3x3x3() {
		random = new Random();
	}
	
	public static ScrambleGenerator3x3x3 getInstance() {
		if(instance == null)
			instance = new ScrambleGenerator3x3x3();
		return instance;
	}

	public String nextScramble() {
		ArrayList<Movement> movs = new ArrayList<Movement>();
		for (int i = 0; i < 25; i++) {
			Movement mov;
			do {
				mov = new Movement(
						faces[random.nextInt(faces.length)], directions[random
								.nextInt(directions.length)]);
			} while (!mov.compatible(movs));
			movs.add(mov);
		}

		String scramble = "";
		String comma = "";
		for (Movement mov : movs) {
			scramble += comma + mov.toString();
			comma = " ";
		}

		Log.d(getClass().getSimpleName(), scramble);
		return scramble;
	}

	private static ScrambleGenerator3x3x3 instance = null;
	private static Random random;
	private static String[] faces = { "F", "R", "U", "B", "L", "D" };
	private static String[] directions = { "", "'", "2" };

	private class Movement {
		public Movement(String face, String direction) {
			this.face = face;
			this.direction = direction;
		}

		public boolean compatible(ArrayList<Movement> movs) {
			boolean compatible = true;

			Movement mov_1 = null;
			if (movs.size() > 0)
				mov_1 = movs.get(movs.size() - 1);
			Movement mov_2 = null;
			if (movs.size() > 1)
				mov_2 = movs.get(movs.size() - 2);

			if (mov_1 != null) {
				if (face.equals(mov_1.face)) {
					compatible = false; // don't allow F F', R R2, L L
				} else if ((face.equals("F") && mov_1.face.equals("B"))
						|| (face.equals("R") && mov_1.face.equals("L"))
						|| (face.equals("U") && mov_1.face.equals("D"))
						|| (face.equals("B") && mov_1.face.equals("F"))
						|| (face.equals("L") && mov_1.face.equals("R"))
						|| (face.equals("D") && mov_1.face.equals("U"))) {
					if (mov_2 != null) {
						if (mov_1.face.equals(mov_2.face)) {
							compatible = false; // don't allow F F R
						} else if ((mov_1.face.equals("F") && mov_2.face
								.equals("B"))
								|| (mov_1.face.equals("R") && mov_2.face
										.equals("L"))
								|| (mov_1.face.equals("U") && mov_2.face
										.equals("D"))
								|| (mov_1.face.equals("B") && mov_2.face
										.equals("F"))
								|| (mov_1.face.equals("L") && mov_2.face
										.equals("R"))
								|| (mov_1.face.equals("D") && mov_2.face
										.equals("U"))) {
							compatible = false; // don't allow F B F, R L2 R, U
												// D D
						}
					}
				}
			}

			return compatible;
		}

		public String toString() {
			return face + direction;
		}

		private String face;
		private String direction;
	}
}
