package com.digdream.androidrubiksolver.cube;

import java.util.ArrayList;

import android.util.Log;

import com.digdream.androidrubiksolver.solver.Tools;

public class RubiksCube {

	private static final String TAG = "ARCS::RubiksCube";

	private Rotator rotator;
	private CubeUpdater updater;
	private Facelet[] facelets;

	public RubiksCube() {
		rotator = new Rotator();
		updater = new CubeUpdater();
		facelets = rotator.getFacelets();
	}

	public void rotateFront() {
		rotator.rotateFront();
		update();
	}

	public void rotateBack() {
		rotator.rotateBack();
		update();
	}

	public void rotateUp() {
		rotator.rotateUp();
		update();
	}

	public void rotateDown() {
		rotator.rotateDown();
		update();
	}

	public void rotateLeft() {
		rotator.rotateLeft();
		update();
	}

	public void rotateRight() {
		rotator.rotateRight();
		update();
	}

	public void update() {
		facelets = rotator.getFacelets();
		facelets = updater.updateFacelets(facelets);
	}

	public Facelet[] getFacelets() {
		return facelets;
	}

	public void setFacelets(Facelet[] facelets) {
		this.facelets = facelets;
	}

	public Facelet[] getFace(int facename) {
		Facelet[] face = new Facelet[9];
		for (int i=0; i<9; i++) {
			face[i] = facelets[9*facename + i];
		}
		return face;
	}

	public ArrayList<Integer> getFaceColor(int facename) {
		ArrayList<Integer> face = new ArrayList<Integer>(9);
		try {
			for (int i=0; i<9; i++) {
				face.add(facelets[9*facename + i].getColor());
			}
		} catch (IndexOutOfBoundsException e) {
			face = null;
		}
		return face;
	}

	public int[] getFaceletColors() {
		int[] colors = new int[54];
		for (int i=0; i<facelets.length; i++) {
			colors[i] = facelets[i].getColor();
		}
		return colors;
	}

	public void setFaceletColors(int[] colors) {
		for (int i=0; i<colors.length; i++) {
			facelets[i].setColor(colors[i]);
		}
	}

	public void setFaceColor(int facename, ArrayList<Integer> face) {
		for(int i = 0; i < 9; i++) {
			facelets[9*facename + i].setColor(face.get(i));
		}
	}

	public boolean hasUnsetFacelets() {
		for (Facelet facelet : facelets) {
			if (facelet.getColor() == ColorConverter.UNSET_COLOR) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		for (int i=0; i<facelets.length; i++) {
			facelets[i].setColor(ColorConverter.UNSET_COLOR);
		}
	}

	public void randomize() {
		String randomCube = Tools.randomCube();

		// set facelets
		for (int inputFace = 0; inputFace < 6; inputFace++) {
			// convert face order
			int[] faceConversion = {5, 1, 0, 4, 3, 2};
			int outputFace = faceConversion[inputFace];

			// read the 54 facelets from randomCube and repostion them according our face order
			//URFDLB -> FRBLDU
			for (int facelet = 0; facelet < 9; facelet++) {
				if (randomCube.charAt(9*inputFace + facelet) == 'F') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.ORANGE);
				}
				if (randomCube.charAt(9*inputFace + facelet) == 'R') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.BLUE);
				}
				if (randomCube.charAt(9*inputFace + facelet) == 'B') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.RED);
				}
				if (randomCube.charAt(9*inputFace + facelet) == 'L') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.GREEN);
				}
				if (randomCube.charAt(9*inputFace + facelet) == 'D') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.WHITE);
				}
				if (randomCube.charAt(9*inputFace + facelet) == 'U') {
					facelets[9*outputFace + facelet].setColor(ColorConverter.YELLOW);
				}
			}
		}
	}

	// toString-methods for testing purposes.

	@Override
	public String toString() {
		String string = "";
		for(int i = 0; i < facelets.length; i += 3) {
			string += facelets[i].getColor() + " " + facelets[i + 1].getColor() + " " + facelets[i + 2].getColor() + "\n";
		}
		return string;
	}

	public String getPositions() {
		String string = "";
		for(int i = 0; i < 9; i++) {
			string += "Color: " + facelets[i].getColor() + ", " + facelets[i].getLocation().toString();
		}
		return string;
	}

	public String getSingmasterNotation() {
		StringBuilder singmaster = new StringBuilder();

		// URFDLB
		for (Facelet facelet: getFace(Rotator.UP)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		for (Facelet facelet: getFace(Rotator.RIGHT)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		for (Facelet facelet: getFace(Rotator.FRONT)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		for (Facelet facelet: getFace(Rotator.DOWN)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		for (Facelet facelet: getFace(Rotator.LEFT)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		for (Facelet facelet: getFace(Rotator.BACK)) {
			singmaster.append(ColorConverter.colorToSingmaster(facelet.getColor()));
		}
		Log.d(TAG, "CUBE: " + singmaster);
		return singmaster.toString();
	}
}
