/*
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 */

package com.digdream.androidrubiksolver.showcube;

/*
 * Represents a single rotation of a Rubik's Cube. Specifies axis, section, and direction.
 */
public class Rotation {
    public enum Axis {X, Y, Z}

    public enum Direction {
        CLOCKWISE {
            @Override
            public Direction reverse() {
                return COUNTER_CLOCKWISE;
            }
        },
        COUNTER_CLOCKWISE {
            @Override
            public Direction reverse() {
                return CLOCKWISE;
            }
        };

        public abstract Direction reverse();
    }

    private Axis axis;
    private int section; // this is the index of the row/column/face that is to be rotated
    private Direction direction;

    public Rotation(Axis axis, int section, Direction direction) {
        this.axis = axis;
        this.section = section;
        this.direction = direction;
    }

    public int getSection() {
        return section;
    }

    public Axis getAxis() {
        return axis;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isClockwise() {
        return (direction == Direction.CLOCKWISE);
    }
}
