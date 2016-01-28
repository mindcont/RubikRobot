/*
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 */

package com.digdream.androidrubiksolver.showcube;

/*
 * Represents a smaller cube (aka cubie/cubelet) in a Rubik's Cube.
 */
public class Cubie {
    // bits denoting faces of the cubie
    public static final int FACELET_FRONT = (1 << 0);
    public static final int FACELET_REAR = (1 << 1);
    public static final int FACELET_LEFT = (1 << 2);
    public static final int FACELET_RIGHT = (1 << 3);
    public static final int FACELET_TOP = (1 << 4);
    public static final int FACELET_BOTTOM = (1 << 5);

    public static final Color SOLVED_STATE_FRONT_COLOR = Color.WHITE;
    public static final Color SOLVED_STATE_REAR_COLOR = Color.YELLOW;
    public static final Color SOLVED_STATE_TOP_COLOR = Color.GREEN;
    public static final Color SOLVED_STATE_BOTTOM_COLOR = Color.BLUE;
    public static final Color SOLVED_STATE_LEFT_COLOR = Color.RED;
    public static final Color SOLVED_STATE_RIGHT_COLOR = Color.ORANGE;

    public enum Color {
        WHITE, YELLOW, GREEN, ORANGE, BLUE, RED
    }

    Color frontColor = SOLVED_STATE_FRONT_COLOR;
    Color rearColor = SOLVED_STATE_REAR_COLOR;
    Color topColor = SOLVED_STATE_TOP_COLOR;
    Color bottomColor = SOLVED_STATE_BOTTOM_COLOR;
    Color leftColor = SOLVED_STATE_LEFT_COLOR;
    Color rightColor = SOLVED_STATE_RIGHT_COLOR;

    public Cubie() {
    }

    public Cubie(Color front, Color rear, Color top, Color bottom, Color left, Color right) {
        this.frontColor = front;
        this.rearColor = rear;
        this.topColor = top;
        this.bottomColor = bottom;
        this.leftColor = left;
        this.rightColor = right;
    }

    public Color getBottomColor() {
        return bottomColor;
    }

    public Color getFrontColor() {
        return frontColor;
    }

    public Color getRearColor() {
        return rearColor;
    }

    public Color getTopColor() {
        return topColor;
    }

    public Color getLeftColor() {
        return leftColor;
    }

    public Color getRightColor() {
        return rightColor;
    }

    public Cubie getCopy() {
        return new Cubie(frontColor, rearColor, topColor, bottomColor, leftColor, rightColor);
    }
}
