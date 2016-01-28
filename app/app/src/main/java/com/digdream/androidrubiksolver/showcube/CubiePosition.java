/*
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 */

package com.digdream.androidrubiksolver.showcube;

/* 
 * Wrapper class to store position coordinates of a Cubie
 */
public class CubiePosition {
    int x, y, z;

    public CubiePosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /* convenience methods for a 3x3x3 cube */
    public boolean isInColumnLeft() {
        return this.x == RubiksCube.COLUMN_LEFT;
    }

    public boolean isInColumnMiddle() {
        return this.x == RubiksCube.COLUMN_MIDDLE;
    }

    public boolean isInColumnRight() {
        return this.x == RubiksCube.COLUMN_RIGHT;
    }

    public boolean isInRowBottom() {
        return this.y == RubiksCube.ROW_BOTTOM;
    }

    public boolean isInRowMiddle() {
        return this.y == RubiksCube.ROW_MIDDLE;
    }

    public boolean isInRowTop() {
        return this.y == RubiksCube.ROW_TOP;
    }

    public boolean isInFaceFront() {
        return this.z == RubiksCube.FACE_FRONT;
    }

    public boolean isInFaceMiddle() {
        return this.z == RubiksCube.FACE_MIDDLE;
    }

    public boolean isInFaceRear() {
        return this.z == RubiksCube.FACE_REAR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        CubiePosition other = (CubiePosition) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        if (z != other.z) return false;
        return true;
    }
}