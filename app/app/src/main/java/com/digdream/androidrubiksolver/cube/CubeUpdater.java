package com.digdream.androidrubiksolver.cube;

import java.util.Arrays;



public class CubeUpdater {

	private Facelet[] facelets;

	public CubeUpdater() {
		// initialized to prevent null-pointer-exceptions.
		facelets = new Facelet[54];
	}

	public Facelet[] updateFacelets(Facelet[] facelet) {
		facelets = facelet;
		int position = 0;
		Facelet[] updatedFacelets = new Facelet[54];

		// Front face.
		for(int y = 1; y > -2; y--) {
			for(int x = -1; x < 2; x++) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(x, y, 1),
						new FaceletLocation(0, 0, 1));
				position++;
			}
		}

		// Right face.
		for(int y = 1; y > -2; y--) {
			for(int z = 1; z > -2; z--) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(1, y, z),
						new FaceletLocation(1, 0, 0));
				position++;
			}
		}

		// Back face.
		for(int y = 1; y > -2; y--) {
			for(int x = 1; x > -2; x--) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(x, y, -1),
						new FaceletLocation(0, 0, -1));
				position++;
			}
		}

		// Left face.
		for(int y = 1; y > -2; y--) {
			for(int z = -1; z < 2; z++) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(-1, y, z),
						new FaceletLocation(-1, 0, 0));
				position++;
			}
		}


		// Down face.
		for(int z = 1; z > -2; z--) {
			for(int x = -1; x < 2; x++) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(x, -1, z),
						new FaceletLocation(0, -1, 0));
				position++;
			}
		}

		// Up face.
		for(int z = -1; z < 2; z++) {
			for(int x = -1; x < 2; x++) {
				updatedFacelets[position] = getFaceletAt(new FaceletLocation(x, 1, z),
						new FaceletLocation(0, 1, 0));
				position++;
			}
		}

		facelets = updatedFacelets;
		return facelets;
	}


	private Facelet getFaceletAt(FaceletLocation loc, FaceletLocation dir) {
		for(Facelet facelet : facelets) {
			int[] getLoc = facelet.getLocation().getLocation();
			int[] getDir = facelet.getDirection().getLocation();

			if(Arrays.equals(loc.getLocation(), getLoc)) {
				if(Arrays.equals(dir.getLocation(), getDir)) {
					return facelet;
				}
			}
		}
		return null;
	}

	public Facelet[] getFacelets() {
		return facelets;
	}

}
