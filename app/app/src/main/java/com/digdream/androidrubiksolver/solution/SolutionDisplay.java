package com.digdream.androidrubiksolver.solution;

/* This class is taking the cube and calculates the next
 * step only when it's needed.
 */

import com.digdream.androidrubiksolver.cube.Facelet;
import com.digdream.androidrubiksolver.cube.Rotator;
import com.digdream.androidrubiksolver.cube.RubiksCube;

public class SolutionDisplay {

	private RubiksCube cube;

	// The cube is given from the activity.

	public SolutionDisplay(RubiksCube cube) {
		this.cube = cube;
	}

	public Facelet[] getNextStep(String step) {

		/* step is a string that looks like "r3".
		 * The first char is the rotation and the second
		 * char is the count, how often this rotation
		 * should be done.
		 */
		Facelet[] facelets = cube.getFace(Rotator.FRONT);
		char rotation = step.charAt(0);
		int count = Character.getNumericValue(step.charAt(1));

		switch(rotation) {
		case 'f':
			facelets = cube.getFace(Rotator.FRONT);
			for(int i = 0; i < count; i++) {
				cube.rotateFront();
			}
			break;
		case 'r':
			facelets = cube.getFace(Rotator.RIGHT);
			for(int i = 0; i < count; i++) {
				cube.rotateRight();
			}
			break;
		case 'b':
			facelets = cube.getFace(Rotator.BACK);
			for(int i = 0; i < count; i++) {
				cube.rotateBack();
			}
			break;
		case 'l':
			facelets = cube.getFace(Rotator.LEFT);
			for(int i = 0; i < count; i++) {
				cube.rotateLeft();
			}
			break;
		case 'd':
			facelets = cube.getFace(Rotator.DOWN);
			for(int i = 0; i < count; i++) {
				cube.rotateDown();
			}
			break;
		case 'u':
			facelets = cube.getFace(Rotator.UP);
			for(int i = 0; i < count; i++) {
				cube.rotateUp();
			}
			break;
		default:
			break;
		}

		// Returns an array of Facelets.
		return facelets;
	}

}
