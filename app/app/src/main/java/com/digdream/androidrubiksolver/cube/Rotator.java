package com.digdream.androidrubiksolver.cube;

import java.util.ArrayList;


public class Rotator {

	/* This is an implementation of a cube using vectors
	 * to represent the positions of the 54 facelets.
	 * This code is heavily inspired by:
	 * http://www.algosome.com/articles/rubiks-cube-computer-simulation.html
	 * Big thanks to www.algosome.com!
	 */

	@SuppressWarnings("unused")
	private static final String TAG = "ARCS::Rotator";

	// define static face names
	public final static int FRONT = 0; // facelets[0-8]
	public final static int RIGHT = 1; // facelets[9-17]
	public final static int BACK = 2;  // facelets[18-26]
	public final static int LEFT = 3;  // facelets[27-35]
	public final static int DOWN = 4;  // facelets[36-44]
	public final static int UP = 5;    // facelets[45-53]

	private Facelet[] facelets;
	private ArrayList<Facelet> selectedFacelets;

	public Rotator() {
		facelets = new Facelet[54];
		initCube();
	}

	/* These three methods are used to rotate a facelet to a certain position.
	 * They don't really alter the facelet they simply return a new one with
	 * the altered location.
	 */
	private FaceletLocation rotateOnXAxis(FaceletLocation loc, double degree) {
		int x = loc.getLocationX();
		int y = (int) Math.round(loc.getLocationY() * Math.cos(degree) - loc.getLocationZ() * Math.sin(degree));
		int z = (int) Math.round(loc.getLocationY() * Math.sin(degree) + loc.getLocationZ() * Math.cos(degree));
		return new FaceletLocation(x, y, z);
	}

	private FaceletLocation rotateOnYAxis(FaceletLocation loc, double degree) {
		int x = (int) Math.round(loc.getLocationZ() * Math.sin(degree) + loc.getLocationX() * Math.cos(degree));
		int y = loc.getLocationY();
		int z = (int) Math.round(loc.getLocationZ() * Math.cos(degree) - loc.getLocationX() * Math.sin(degree));
		return new FaceletLocation(x, y, z);
	}

	private FaceletLocation rotateOnZAxis(FaceletLocation loc, double degree) {
		int x = (int) Math.round(loc.getLocationX() * Math.cos(degree) - loc.getLocationY() * Math.sin(degree));
		int y = (int) Math.round(loc.getLocationX() * Math.sin(degree) + loc.getLocationY() * Math.cos(degree));
		int z = loc.getLocationZ();
		return new FaceletLocation(x, y, z);
	}


	/* rotateFacelet takes a facelet, an axis (string) and a double value,
	 * that represents the rotator on the given axis.
	 */
	private void rotateFacelet(Facelet facelet, String axis, double degree) {
		FaceletLocation newFaceletLocation = null;
		FaceletLocation newFaceletDirection = null;

		if(axis.equalsIgnoreCase("x")) {
			newFaceletLocation = rotateOnXAxis(facelet.getLocation(), degree);
			newFaceletDirection = rotateOnXAxis(facelet.getDirection(), degree);
		} else if(axis.equalsIgnoreCase("y")) {
			newFaceletLocation = rotateOnYAxis(facelet.getLocation(), degree);
			newFaceletDirection = rotateOnYAxis(facelet.getDirection(), degree);
		} else if(axis.equalsIgnoreCase("z")) {
			newFaceletLocation = rotateOnZAxis(facelet.getLocation(), degree);
			newFaceletDirection = rotateOnZAxis(facelet.getDirection(), degree);
		}

		facelet.setLocation(newFaceletLocation);
		facelet.setDirection(newFaceletDirection);
	}

	/* We use this coordinate system. There is no reason for it. Period.
	 *
	 * Coordinates:
	 * 			y
	 * 			^
	 * 			|
	 * 			|
	 * 			|
	 * 			|
	 * 			|_____________> x
	 * 			/
	 * 		   /
	 * 		  /
	 * 		 /
	 * 		z
	 */


	/* initCube works as followed: it creates 9 facelets at the
	 * positions of the front face. After that, the 9 facelets are moved to
	 * the positions of one of the other faces. Repeat until all faces are
	 * set.
	 */
	public void initCube() {
		ArrayList<Facelet> faceletsForEachFace;

		for(int face = FRONT; face <= UP; face++) {
			faceletsForEachFace = new ArrayList<Facelet>();

			// This will be the front face.
			for (int yAxis = 1; yAxis > -2; yAxis--) {
				for (int xAxis = -1; xAxis < 2; xAxis++) {
					faceletsForEachFace.add(new Facelet(new FaceletLocation(xAxis, yAxis, 1),
							new FaceletLocation(0, 0, 1), ColorConverter.UNSET_COLOR));
				}
			}

			// Here the facelets get rotated to their positions.

			// Front
			if (face == FRONT) {
				// we do not need to rotate for front face
				// Right
			} else if (face == RIGHT) {
				for(Facelet facelet : faceletsForEachFace) {
					rotateFacelet(facelet, "y", Math.PI / 2);
				}
				// Back
			} else if (face == BACK) {
				for(Facelet facelet : faceletsForEachFace) {
					rotateFacelet(facelet, "y", Math.PI);
				}
				// Left
			} else if (face == LEFT) {
				for(Facelet facelet : faceletsForEachFace) {
					rotateFacelet(facelet, "y", -Math.PI / 2);
				}
				// Down
			} else if (face == DOWN) {
				for(Facelet facelet : faceletsForEachFace) {
					rotateFacelet(facelet, "x", Math.PI / 2);
				}
				// Up
			} else if (face == UP) {
				for(Facelet facelet : faceletsForEachFace) {
					rotateFacelet(facelet, "x", -Math.PI / 2);
				}
			}

			// Here the facelets that just got initialized are assigned onto the facelets.
			for(int faceletOnFaceIndex = 0; faceletOnFaceIndex < 9; faceletOnFaceIndex++) {
				facelets[face * 9 + faceletOnFaceIndex] = faceletsForEachFace.get(faceletOnFaceIndex);
			}

		}
	}

	/* With this method, all the facelets that are needed for a certain
	 * rotator are selected.
	 * Example: for a rotatoion of the left side you will need to select
	 * all facelets where the x-value of their locations is -1.
	 * These facelets are added to the facelets ArrayList and can be used
	 * in the following actual rotator.
	 */
	private void setUpFaceletsForRotation(int facename) {
		selectedFacelets = new ArrayList<Facelet>();
		for(Facelet facelet : getFacelets()) {

			if(facename == FRONT) {
				if(facelet.getLocationZ() == 1) {
					selectedFacelets.add(facelet);
				}
			} else if(facename == RIGHT) {
				if(facelet.getLocationX() == 1) {
					selectedFacelets.add(facelet);
				}
			} else if(facename == BACK) {
				if(facelet.getLocationZ() == -1) {
					selectedFacelets.add(facelet);
				}
			} else if(facename == LEFT) {
				if(facelet.getLocationX() == -1) {
					selectedFacelets.add(facelet);
				}
			} else if(facename == DOWN) {
				if(facelet.getLocationY() == -1) {
					selectedFacelets.add(facelet);
				}
			} else if(facename == UP) {
				if(facelet.getLocationY() == 1) {
					selectedFacelets.add(facelet);
				}
			}
		}
	}


	// The rotate methods rotates each facelet to their new location.
	public void rotateFront() {
		setUpFaceletsForRotation(FRONT);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "z", -Math.PI / 2);
			}
		}
	}

	public void rotateRight() {
		setUpFaceletsForRotation(RIGHT);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "x", -Math.PI / 2);
			}
		}
	}

	public void rotateBack() {
		setUpFaceletsForRotation(BACK);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "z", Math.PI / 2);
			}
		}
	}

	public void rotateLeft() {
		setUpFaceletsForRotation(LEFT);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "x", Math.PI / 2);
			}
		}
	}

	public void rotateDown() {
		setUpFaceletsForRotation(DOWN);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "y", Math.PI / 2);
			}
		}
	}

	public void rotateUp() {
		setUpFaceletsForRotation(UP);
		for (Facelet facelet : facelets) {
			if(selectedFacelets.contains(facelet)) {
				rotateFacelet(facelet, "y", -Math.PI / 2);
			}
		}
	}

	// Returns all facelets.
	public Facelet[] getFacelets() {
		return facelets;
	}

}