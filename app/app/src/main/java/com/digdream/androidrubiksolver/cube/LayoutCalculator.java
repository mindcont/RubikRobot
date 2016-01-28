package com.digdream.androidrubiksolver.cube;

import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;

public class LayoutCalculator {

	private int padding;
	private int xOffset;
	private int height;

	public LayoutCalculator(int width, int height) {
		// width is unused this time
		this.height = height;
	}

	public ArrayList<Rect> calculateRectanglesCoordinates() {
		// Creates ListArray of cv::rects to draw overlay

		// Rectangle positions in ListArray:
		// |0|1|2|
		// |3|4|5|
		// |6|7|8|

		ArrayList<Rect> tmpRect = new ArrayList<Rect>(9);

		// spacing between rects
		int rectSpacing = 7*height/100;

		// margin around face
		int faceMargin = 10*height/100;

		// Size of single rect
		Size rectSize = new Size(
				(height - 2*rectSpacing - 2*faceMargin)/3,
				(height - 2*rectSpacing - 2*faceMargin)/3);

		// vector from tl of rect to tl of rect on the right
		Point rectDistance = new Point(rectSize.width + rectSpacing,
				rectSize.height + rectSpacing);

		// Initial points: top left/bottom right corners of first rectangle
		Point topLeft = new Point(faceMargin, faceMargin);
		Point bottomRight = new Point(faceMargin + rectSize.width,
				faceMargin + rectSize.height);

		// Values needed by positionViews()
		padding = faceMargin;
		xOffset = (int) (2*faceMargin + 3*rectSize.width + 3*rectSpacing);

		// Create nine rectangles, three in each row
		for (int row=0; row < 3; row++) {
			for (int col=0; col < 3; col++) {
				tmpRect.add(getNextRectInRow(topLeft, bottomRight, rectDistance, col));
			}
			// move to next row
			topLeft = movePointVertically(topLeft, rectDistance);
			bottomRight = movePointVertically(bottomRight, rectDistance);
		}
		return tmpRect;
	}


	public Point movePointHorizontally(Point a, Point b, int factor) {
		return new Point(a.x + factor*b.x, a.y);
	}

	public Rect getNextRectInRow(Point tl, Point br, Point dist, int factor) {
		// calculate next rectangle in row
		return new Rect(
				movePointHorizontally(tl, dist, factor),
				movePointHorizontally(br, dist, factor));
	}

	public Point movePointVertically(Point a, Point b) {
		return new Point(a.x, a.y + b.y);
	}

	public int getXOffset() {
		return xOffset;
	}

	public int getPadding() {
		return padding;
	}


}
