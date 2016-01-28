package com.digdream.androidrubiksolver.cube;

public class FaceletLocation {
	
	private int locationX;
	private int locationY;
	private int locationZ;
	
	public FaceletLocation(int x, int y, int z) {
		this.locationX = x;
		this.locationY = y;
		this.locationZ = z;
	}
	
	public void setLocation(int x, int y, int z) {
		this.locationX = x;
		this.locationY = y;
		this.locationZ = z;
	}
	
	public int getLocationX() {
		return locationX;
	}

	public int getLocationY() {
		return locationY;
	}

	public int getLocationZ() {
		return locationZ;
	}
	
	// For test purposes.
	public int[] getLocation() {
		return new int[]{locationX, locationY, locationZ};
	}
	
	@Override
	public String toString() {
		String string = "X: " + locationX + ", Y: " + locationY + ", Z: " + locationZ + "\n";
		return string;
	}

}
