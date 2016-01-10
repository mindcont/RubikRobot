package com.digdream.androidrubiksolver.cube;


public class Facelet {
	
	// The facelet color
	private int color;
	
	// Position of the facelet is saved in an object that functions as a vector
	// containing information about the x, y and y-coordinates.
	private FaceletLocation location;
	
	// the non-zero value of x,y or z of direction points to the face this facelet is located on
	private FaceletLocation direction;
	
	public Facelet(FaceletLocation location, FaceletLocation direction, int color) {
		this.location = location;
		this.direction = direction;
		this.color = color;
	}
	
	// getter and setter-methods
	public int getLocationX() {
		return location.getLocationX();
	}
	
	public int getLocationY() {
		return location.getLocationY();
	}
	
	public int getLocationZ() {
		return location.getLocationZ();
	}
	
	public FaceletLocation getLocation() {
		return location;
	}
	
	public int[] getLocationsAsArray() {
		return location.getLocation();
	}
	
	public int getDirectionX() {
		return direction.getLocationX();
	}
	
	public int getDirectionY() {
		return direction.getLocationY();
	}
	
	public int getDirectionZ() {
		return direction.getLocationZ();
	}
	
	public int[] getDirectionsAsArray() {
		return direction.getLocation();
	}
	
	public FaceletLocation getDirection() {
		return direction;
	}
	
	public int getColor() {
		return color;
	}
	
	public void setLocation(FaceletLocation newLocation) {
		this.location = newLocation;
	}
	
	public void setDirection(FaceletLocation newDirection) {
		this.direction = newDirection;
	}
	
	public void setColor(int color) {
		this.color = color;
	}

}
