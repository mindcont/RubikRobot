package com.digdream.androidrubiksolver.scramble;

public class ScrambleGenerator {
	public static String nextScramble(String puzzle_type) {
		if ("2x2x2".equals(puzzle_type))
			return ScrambleGenerator2x2x2.getInstance().nextScramble();
		else if ("3x3x3".equals(puzzle_type))
			return ScrambleGenerator3x3x3.getInstance().nextScramble();
		else if ("4x4x4".equals(puzzle_type))
			return ScrambleGeneratorNxNxN.getInstance().nextScramble(4, true, 40);
		else if ("5x5x5".equals(puzzle_type))
			return ScrambleGeneratorNxNxN.getInstance().nextScramble(5, true, 60);
		else if ("6x6x6".equals(puzzle_type))
			return ScrambleGeneratorNxNxN.getInstance().nextScramble(6, true, 80);
		else if ("7x7x7".equals(puzzle_type))
			return ScrambleGeneratorNxNxN.getInstance().nextScramble(7, true, 100);
		else
			return "";
	}
}
