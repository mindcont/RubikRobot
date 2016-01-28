/*
 * Ahsan Rabbani <ahsan@xargsgrep.com>
 */

package com.digdream.androidrubiksolver.showcube;

import com.digdream.androidrubiksolver.showcube.Cubie.Color;
import com.digdream.androidrubiksolver.showcube.Rotation.Axis;
import com.digdream.androidrubiksolver.showcube.Rotation.Direction;

import java.util.ArrayList;
import java.util.List;

/* 
 * A rather poorly implemented solver for a 3x3x3 Rubik's Cube. This implementation is "dumb" in
 * the sense that it doesn't do any sort of optimizations to reduce the number of moves and doesn't
 * use any search algorithm (eg IDA*) that might normally be used in this context. It is simply
 * an implementation of the way I learned to solve a Rubik's Cube (by layers).
 */
public class LameRubiksCubeSolver extends RubiksCubeSolver {
    public static final CubiePosition CENTER_FRONT = new CubiePosition(1, 1, 0);
    public static final CubiePosition CENTER_REAR = new CubiePosition(1, 1, 2);
    public static final CubiePosition CENTER_TOP = new CubiePosition(1, 2, 1);
    public static final CubiePosition CENTER_BOTTOM = new CubiePosition(1, 0, 1);
    public static final CubiePosition CENTER_LEFT = new CubiePosition(0, 1, 1);
    public static final CubiePosition CENTER_RIGHT = new CubiePosition(2, 1, 1);
    public static final CubiePosition EDGE_FRONT_LEFT = new CubiePosition(0, 1, 0);
    public static final CubiePosition EDGE_FRONT_RIGHT = new CubiePosition(2, 1, 0);
    public static final CubiePosition EDGE_FRONT_TOP = new CubiePosition(1, 2, 0);
    public static final CubiePosition EDGE_FRONT_BOTTOM = new CubiePosition(1, 0, 0);
    public static final CubiePosition EDGE_MIDDLE_TOP_LEFT = new CubiePosition(0, 2, 1);
    public static final CubiePosition EDGE_MIDDLE_BOTTOM_LEFT = new CubiePosition(0, 0, 1);
    public static final CubiePosition EDGE_MIDDLE_TOP_RIGHT = new CubiePosition(2, 2, 1);
    public static final CubiePosition EDGE_MIDDLE_BOTTOM_RIGHT = new CubiePosition(2, 0, 1);
    public static final CubiePosition EDGE_REAR_LEFT = new CubiePosition(0, 1, 2);
    public static final CubiePosition EDGE_REAR_RIGHT = new CubiePosition(2, 1, 2);
    public static final CubiePosition EDGE_REAR_TOP = new CubiePosition(1, 2, 2);
    public static final CubiePosition EDGE_REAR_BOTTOM = new CubiePosition(1, 0, 2);
    public static final CubiePosition CORNER_FRONT_TOP_LEFT = new CubiePosition(0, 2, 0);
    public static final CubiePosition CORNER_FRONT_BOTTOM_LEFT = new CubiePosition(0, 0, 0);
    public static final CubiePosition CORNER_FRONT_TOP_RIGHT = new CubiePosition(2, 2, 0);
    public static final CubiePosition CORNER_FRONT_BOTTOM_RIGHT = new CubiePosition(2, 0, 0);
    public static final CubiePosition CORNER_REAR_TOP_LEFT = new CubiePosition(0, 2, 2);
    public static final CubiePosition CORNER_REAR_BOTTOM_LEFT = new CubiePosition(0, 0, 2);
    public static final CubiePosition CORNER_REAR_TOP_RIGHT = new CubiePosition(2, 2, 2);
    public static final CubiePosition CORNER_REAR_BOTTOM_RIGHT = new CubiePosition(2, 0, 2);

    public LameRubiksCubeSolver(RubiksCube cube) {
        super(cube);

        if (cube.getSize() != 3)
            throw new RuntimeException(this.getClass().getName() + " only supports solving 3x3x3 cubes");
    }

    @Override
    public List<Rotation> getSolution() {
        positionCenters(); // this is just to get the cube into a known orientation (white on the front and green on the top)
        solveStep1(); // front face cross
        solveStep2(); // front face corners
        solveStep3(); // middle face edges
        solveStep4(); // rear face cross
        solveStep5(); // rear face edges
        solveStep6(); // rear face corners' positions
        solveStep7(); // rear face corners' orientations
        return rotations;
    }

    private void positionCenters() {
        // move white center cubie to front face
        if (cube.getCubie(CENTER_REAR).rearColor == Color.WHITE) {
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_MIDDLE, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_MIDDLE, Direction.COUNTER_CLOCKWISE));
        } else if (cube.getCubie(CENTER_TOP).topColor == Color.WHITE) {
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_MIDDLE, Direction.COUNTER_CLOCKWISE));
        } else if (cube.getCubie(CENTER_BOTTOM).bottomColor == Color.WHITE) {
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_MIDDLE, Direction.CLOCKWISE));
        } else if (cube.getCubie(CENTER_LEFT).leftColor == Color.WHITE) {
            addAndApplyRotation(new Rotation(Axis.Y, RubiksCube.ROW_MIDDLE, Direction.COUNTER_CLOCKWISE));
        } else if (cube.getCubie(CENTER_RIGHT).rightColor == Color.WHITE) {
            addAndApplyRotation(new Rotation(Axis.Y, RubiksCube.ROW_MIDDLE, Direction.CLOCKWISE));
        }

        // move green center cubie to top face
        if (cube.getCubie(CENTER_BOTTOM).bottomColor == Color.GREEN) {
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_MIDDLE, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_MIDDLE, Direction.COUNTER_CLOCKWISE));
        } else if (cube.getCubie(CENTER_LEFT).leftColor == Color.GREEN) {
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_MIDDLE, Direction.CLOCKWISE));
        } else if (cube.getCubie(CENTER_RIGHT).rightColor == Color.GREEN) {
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_MIDDLE, Direction.COUNTER_CLOCKWISE));
        }
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep1() {
        // iterate through each front edge and solve it
        List<CubiePosition> positions = getFrontEdgePositions();
        for (CubiePosition position : positions) {
            step1SolveEdge(position);
        }
    }

    private void step1SolveEdge(CubiePosition destination) {
        List<Color> colors = cube.getVisibleColorsInSolvedState(destination);

        while (!cube.isPositionSolved(destination)) {
            CubiePosition source = findCubiePositionWithColors(colors.toArray(new Color[0]));

            if (source.equals(destination)) {
                // edge is in correct position but wrong orientation, solve it
                Axis axis1 = source.isInRowMiddle() ? Axis.X : Axis.Y;
                int section1 = source.isInRowMiddle() ? source.x : source.y;
                Direction direction1 = (source.isInColumnLeft() || source.isInRowBottom()) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

                Axis axis2 = source.isInRowMiddle() ? Axis.Y : Axis.X;
                int section2 = 0;
                if (source.isInColumnLeft()) section2 = RubiksCube.ROW_TOP;
                else if (source.isInColumnRight()) section2 = RubiksCube.ROW_BOTTOM;
                else if (source.isInRowBottom()) section2 = RubiksCube.COLUMN_LEFT;
                else if (source.isInRowTop()) section2 = RubiksCube.COLUMN_RIGHT;
                Direction direction2 = (source.isInColumnLeft() || source.isInRowTop()) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;

                addAndApplyRotation(new Rotation(axis1, section1, direction1));
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_FRONT, Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(axis2, section2, direction2));
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_FRONT, Direction.COUNTER_CLOCKWISE));
            } else if (source.isInFaceFront()) {
                // edge is in the front face, get it into the rear face
                if (source.isInColumnMiddle()) {
                    addAndApplyRotation(new Rotation(Axis.Y, source.y, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(Axis.Y, source.y, Direction.CLOCKWISE));
                } else if (source.isInRowMiddle()) {
                    addAndApplyRotation(new Rotation(Axis.X, source.x, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(Axis.X, source.x, Direction.CLOCKWISE));
                }
            } else if (source.isInFaceMiddle()) {
                // edge is in the middle face, get it into the rear face
                addAndApplyRotation(new Rotation(Axis.X, source.x, (source.y == 0) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(Axis.X, source.x, (source.y == 0) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE));
            } else if (source.isInFaceRear()) {
                // edge is in the rear face, get it into the correct position in the front face
                if (source.x != destination.x && source.y != destination.y) {
                    Direction direction = ((destination.isInColumnLeft() && source.isInRowTop()) || (destination.isInColumnRight() && source.isInRowBottom()))
                            ? Direction.COUNTER_CLOCKWISE
                            : Direction.CLOCKWISE;
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, direction));
                } else if ((source.x == destination.x && source.y != destination.y) || (source.x != destination.x && source.y == destination.y)) {
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                }

                Axis axis = destination.isInRowMiddle() ? Axis.X : Axis.Y;
                int section = destination.isInRowMiddle() ? destination.x : destination.y;
                addAndApplyRotation(new Rotation(axis, section, Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(axis, section, Direction.CLOCKWISE));
            }
        }
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep2() {
        // iterate through each front corner and solve it
        List<CubiePosition> positions = getFrontCornerPositions();
        for (CubiePosition position : positions) {
            step2SolveCorner(position);
        }
    }

    private void step2SolveCorner(CubiePosition destination) {
        List<Color> colors = cube.getVisibleColorsInSolvedState(destination);

        while (!cube.isPositionSolved(destination)) {
            CubiePosition source = findCubiePositionWithColors(colors.toArray(new Color[0]));

            if (source.isInFaceFront()) {
                // corner is in the front face, get it into the rear face
                addAndApplyRotation(new Rotation(Axis.X, source.x, source.isInRowTop() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE));
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(Axis.X, source.x, source.isInRowTop() ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE));
            } else if (source.isInFaceRear() && (source.x == destination.x && source.y == destination.y)) {
                // corner is in the rear face and is aligned with the appropriate front corner, solve it
                Cubie sourceCubie = cube.getCubie(source);
                if ((source.isInColumnLeft() && source.isInRowBottom() && sourceCubie.bottomColor == Color.WHITE)
                        || (source.isInColumnRight() && source.isInRowBottom() && sourceCubie.rightColor == Color.WHITE)
                        || (source.isInColumnRight() && source.isInRowTop() && sourceCubie.topColor == Color.WHITE)
                        || (source.isInColumnLeft() && source.isInRowTop() && sourceCubie.leftColor == Color.WHITE)) {
                    Axis axis = (source.x == source.y) ? Axis.X : Axis.Y;
                    int section = (source.x == source.y) ? source.x : source.y;
                    Direction direction = (source.isInRowTop()) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction.reverse()));
                } else if ((source.isInColumnLeft() && source.isInRowBottom() && sourceCubie.leftColor == Color.WHITE)
                        || (source.isInColumnRight() && source.isInRowBottom() && sourceCubie.bottomColor == Color.WHITE)
                        || (source.isInColumnRight() && source.isInRowTop() && sourceCubie.rightColor == Color.WHITE)
                        || (source.isInColumnLeft() && source.isInRowTop() && sourceCubie.topColor == Color.WHITE)) {
                    Axis axis = (source.x == source.y) ? Axis.Y : Axis.X;
                    int section = (source.x == source.y) ? source.y : source.x;
                    Direction direction = (source.isInColumnLeft()) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction.reverse()));
                } else if (sourceCubie.rearColor == Color.WHITE) {
                    Axis axis = (source.x == source.y) ? Axis.X : Axis.Y;
                    int section = (source.x == source.y) ? source.x : source.y;
                    Direction direction = (source.isInRowBottom()) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction.reverse()));
                    addAndApplyRotation(new Rotation(axis, section, direction.reverse()));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction));
                    addAndApplyRotation(new Rotation(axis, section, direction));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(axis, section, direction.reverse()));
                }
            } else if (source.isInFaceRear() && (source.x != destination.x || source.y != destination.y)) {
                // corner is in the rear face but is not aligned with the appropriate front corner, align the cornerd in both x and y
                if (source.x != destination.x && source.y != destination.y) {
                    // corners are not aligned in either x or y
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                } else if (source.x != destination.x || source.y != destination.y) {
                    // corners are aligned in either x or y
                    Direction direction = ((destination.isInColumnLeft() && source.isInRowTop()) || (destination.isInColumnRight() && source.isInRowBottom()))
                            ? Direction.COUNTER_CLOCKWISE
                            : Direction.CLOCKWISE;
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, direction));
                }
            }
        }
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep3() {
        // iterate through each middle edge and solve it
        List<CubiePosition> positions = getMiddleEdgePositions();
        for (CubiePosition position : positions) {
            step3SolveEdge(position);
        }
    }

    private void step3SolveEdge(CubiePosition destination) {
        List<Color> colors = cube.getVisibleColorsInSolvedState(destination);

        while (!cube.isPositionSolved(destination)) {
            CubiePosition source = findCubiePositionWithColors(colors.toArray(new Color[0]));

            if (isMiddleEdgeSolvable(source)) {
                // middle edge non-rear color matches the center color of the row/column, solve it
                step3SolveEdgeFromRearFace(source);
            } else if (source.isInFaceRear()) {
                // middle edge is in the rear face but the non-rear color does not match the center color of the row/column, get it into the correct row/column
                Color color = getEdgeNonRearColor(source);

                CubiePosition tmpDestination = null;
                if (color == Color.GREEN) tmpDestination = EDGE_REAR_TOP;
                else if (color == Color.RED) tmpDestination = EDGE_REAR_LEFT;
                else if (color == Color.ORANGE) tmpDestination = EDGE_REAR_RIGHT;
                else if (color == Color.BLUE) tmpDestination = EDGE_REAR_BOTTOM;

                if ((source.x == tmpDestination.x) || (source.y == tmpDestination.y)) {
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                } else {
                    Direction direction = ((source.isInRowTop() && tmpDestination.isInColumnRight())
                            || (source.isInColumnRight() && tmpDestination.isInRowBottom())
                            || (source.isInRowBottom() && tmpDestination.isInColumnLeft())
                            || (source.isInColumnLeft() && tmpDestination.isInRowTop()))
                            ? Direction.CLOCKWISE
                            : Direction.COUNTER_CLOCKWISE;
                    addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, direction));
                }
            } else if (source.isInFaceMiddle()) {
                // middle edge is in the middle face, get it into the rear face
                Direction zDirection = ((source.isInRowBottom() && source.isInColumnRight()) || (source.isInRowTop() && source.isInColumnLeft()))
                        ? Direction.CLOCKWISE
                        : Direction.COUNTER_CLOCKWISE;
                Axis axis1 = Axis.X;
                Axis axis2 = Axis.Y;
                int section1 = source.x;
                int section2 = source.y;
                Direction direction1 = source.isInRowTop() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
                Direction direction2 = source.isInColumnLeft() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
                moveRearEdgeToMiddle(zDirection, axis1, section1, direction1, axis2, section2, direction2);
            }
        }
    }

    // solves a middle edge that is in a solvable position in the rear face
    private void step3SolveEdgeFromRearFace(CubiePosition position) {
        Cubie cubie = cube.getCubie(position);
        Color color = getEdgeNonRearColor(position);

        Axis axis1 = null, axis2 = null;
        int section1 = 0, section2 = 0;
        Direction zDirection = null, direction1 = null, direction2 = null;

        if (color == Color.GREEN) {
            zDirection = (cubie.rearColor == Color.RED) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
            axis1 = Axis.X;
            axis2 = Axis.Y;
            section1 = (cubie.rearColor == Color.RED) ? RubiksCube.COLUMN_LEFT : RubiksCube.COLUMN_RIGHT;
            section2 = RubiksCube.ROW_TOP;
            direction1 = Direction.CLOCKWISE;
            direction2 = (cubie.rearColor == Color.RED) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
        } else if (color == Color.BLUE) {
            zDirection = (cubie.rearColor == Color.ORANGE) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
            axis1 = Axis.X;
            axis2 = Axis.Y;
            section1 = (cubie.rearColor == Color.ORANGE) ? RubiksCube.COLUMN_RIGHT : RubiksCube.COLUMN_LEFT;
            section2 = RubiksCube.ROW_BOTTOM;
            direction1 = Direction.COUNTER_CLOCKWISE;
            direction2 = (cubie.rearColor == Color.ORANGE) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;
        } else if (color == Color.RED) {
            zDirection = (cubie.rearColor == Color.BLUE) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
            axis1 = Axis.Y;
            axis2 = Axis.X;
            section1 = (cubie.rearColor == Color.BLUE) ? RubiksCube.ROW_BOTTOM : RubiksCube.ROW_TOP;
            section2 = RubiksCube.COLUMN_LEFT;
            direction1 = Direction.CLOCKWISE;
            direction2 = (cubie.rearColor == Color.BLUE) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;
        } else if (color == Color.ORANGE) {
            zDirection = (cubie.rearColor == Color.GREEN) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
            axis1 = Axis.Y;
            axis2 = Axis.X;
            section1 = (cubie.rearColor == Color.GREEN) ? RubiksCube.ROW_TOP : RubiksCube.ROW_BOTTOM;
            section2 = RubiksCube.COLUMN_RIGHT;
            direction1 = Direction.COUNTER_CLOCKWISE;
            direction2 = (cubie.rearColor == Color.GREEN) ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
        }

        moveRearEdgeToMiddle(zDirection, axis1, section1, direction1, axis2, section2, direction2);
    }

    private void moveRearEdgeToMiddle(Direction zDirection, Axis axis1, int section1, Direction direction1, Axis axis2, int section2, Direction direction2) {
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, zDirection));
        addAndApplyRotation(new Rotation(axis1, section1, direction1));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, zDirection.reverse()));
        addAndApplyRotation(new Rotation(axis1, section1, direction1.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, zDirection.reverse()));
        addAndApplyRotation(new Rotation(axis2, section2, direction2));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, zDirection));
        addAndApplyRotation(new Rotation(axis2, section2, direction2.reverse()));
    }

    // returns true if the middle edge can be solved from the given position
    private boolean isMiddleEdgeSolvable(CubiePosition position) {
        if (!position.isInFaceRear()) return false;

        Color color = getEdgeNonRearColor(position);

        Color centerColor = null;
        if (position.isInRowTop()) centerColor = Cubie.SOLVED_STATE_TOP_COLOR;
        else if (position.isInRowBottom()) centerColor = Cubie.SOLVED_STATE_BOTTOM_COLOR;
        else if (position.isInColumnLeft()) centerColor = Cubie.SOLVED_STATE_LEFT_COLOR;
        else if (position.isInColumnRight()) centerColor = Cubie.SOLVED_STATE_RIGHT_COLOR;

        return color == centerColor;
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep4() {
        List<CubiePosition> positions = getRearEdgePositions();

        int i = 0;
        while (!isStep4Solved()) {
            step4RotateRearEdge(positions.get(i % 4));
            i++;
        }
    }

    private void step4RotateRearEdge(CubiePosition position) {
        Color color = getEdgeNonRearColor(position);

        if (color != Cubie.SOLVED_STATE_REAR_COLOR) return;

        Axis axis1 = null, axis2 = null;
        int section1 = 0, section2 = 0;
        Direction direction1 = null, direction2 = null;

        if (position.isInRowTop() || position.isInRowBottom()) {
            axis1 = Axis.Y;
            axis2 = Axis.X;
            section1 = position.y;
            section2 = position.isInRowTop() ? RubiksCube.COLUMN_RIGHT : RubiksCube.COLUMN_LEFT;
            direction1 = position.isInRowTop() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
            direction2 = position.isInRowTop() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
        } else if (position.isInColumnLeft() || position.isInColumnRight()) {
            axis1 = Axis.X;
            axis2 = Axis.Y;
            section1 = position.x;
            section2 = position.isInColumnLeft() ? RubiksCube.ROW_TOP : RubiksCube.ROW_BOTTOM;
            direction1 = position.isInColumnLeft() ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;
            direction2 = position.isInColumnLeft() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;
        }

        addAndApplyRotation(new Rotation(axis1, section1, direction1));
        addAndApplyRotation(new Rotation(axis2, section2, direction2));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
        addAndApplyRotation(new Rotation(axis2, section2, direction2.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(axis1, section1, direction1.reverse()));
    }

    private boolean isStep4Solved() {
        List<CubiePosition> positions = getRearEdgePositions();

        for (CubiePosition position : positions) {
            Cubie cubie = cube.getCubie(position);
            if (cubie.rearColor != Cubie.SOLVED_STATE_REAR_COLOR) return false;
        }

        return true;
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep5() {
        step5SolveTopRearEdge();

        while (!isStep5Solved()) {
            if (cube.isPositionSolved(EDGE_REAR_LEFT)) {
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
            } else if (cube.isPositionSolved(EDGE_REAR_RIGHT)) {
                addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
            }

            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_RIGHT, Direction.CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_RIGHT, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_RIGHT, Direction.CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
            addAndApplyRotation(new Rotation(Axis.X, RubiksCube.COLUMN_RIGHT, Direction.COUNTER_CLOCKWISE));

            step5SolveTopRearEdge();
        }
    }

    private void step5SolveTopRearEdge() {
        while (cube.getCubie(EDGE_REAR_TOP).topColor != Cubie.SOLVED_STATE_TOP_COLOR) {
            addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        }
    }

    private boolean isStep5Solved() {
        List<CubiePosition> positions = getRearEdgePositions();

        for (CubiePosition position : positions) {
            if (!cube.isPositionSolved(position)) return false;
        }

        return true;
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep6() {
        CubiePosition position = findRearCornerInCorrectPosition();
        while (position == null) {
            step6RotateRearCorners(Axis.X, RubiksCube.COLUMN_RIGHT, RubiksCube.COLUMN_LEFT, Direction.COUNTER_CLOCKWISE);
            position = findRearCornerInCorrectPosition();
        }

        while (!isStep6Solved()) {
            Axis axis = null;
            int section1 = 0, section2 = 0;
            Direction direction = position.isInRowTop() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

            if (position.equals(CORNER_REAR_BOTTOM_LEFT)) {
                axis = Axis.X;
                section1 = RubiksCube.COLUMN_RIGHT;
                section2 = RubiksCube.COLUMN_LEFT;
            } else if (position.equals(CORNER_REAR_BOTTOM_RIGHT)) {
                axis = Axis.Y;
                section1 = RubiksCube.ROW_TOP;
                section2 = RubiksCube.ROW_BOTTOM;
            } else if (position.equals(CORNER_REAR_TOP_LEFT)) {
                axis = Axis.Y;
                section1 = RubiksCube.ROW_BOTTOM;
                section2 = RubiksCube.ROW_TOP;
            } else if (position.equals(CORNER_REAR_TOP_RIGHT)) {
                axis = Axis.X;
                section1 = RubiksCube.COLUMN_LEFT;
                section2 = RubiksCube.COLUMN_RIGHT;
            }

            step6RotateRearCorners(axis, section1, section2, direction);
        }
    }

    private void step6RotateRearCorners(Axis axis, int section1, int section2, Direction direction) {
        addAndApplyRotation(new Rotation(axis, section1, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section2, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section1, direction.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section2, direction.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
    }

    private CubiePosition findRearCornerInCorrectPosition() {
        List<CubiePosition> positions = getRearCornerPositions();
        for (CubiePosition position : positions) {
            if (cube.isPositionCorrect(position)) return position;
        }
        return null;
    }

    private boolean isStep6Solved() {
        List<CubiePosition> positions = getRearCornerPositions();

        for (CubiePosition position : positions) {
            if (!cube.isPositionCorrect(position)) return false;
        }

        return true;
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private void solveStep7() {
        // order of positions matters here
        List<CubiePosition> positions = new ArrayList<CubiePosition>();
        positions.add(CORNER_REAR_TOP_RIGHT);
        positions.add(CORNER_REAR_BOTTOM_RIGHT);
        positions.add(CORNER_REAR_BOTTOM_LEFT);
        positions.add(CORNER_REAR_TOP_LEFT);

        for (CubiePosition position : positions) {
            while (!cube.isPositionSolved(position)) {
                step7RotateRearCorner(position);
            }
        }
    }

    private void step7RotateRearCorner(CubiePosition position) {
        Axis axis = null;
        int section1 = 0, section2 = 0;
        Direction direction = position.isInRowBottom() ? Direction.CLOCKWISE : Direction.COUNTER_CLOCKWISE;

        if (position.equals(CORNER_REAR_TOP_RIGHT)) {
            axis = Axis.X;
            section1 = RubiksCube.COLUMN_LEFT;
            section2 = RubiksCube.COLUMN_RIGHT;
        } else if (position.equals(CORNER_REAR_BOTTOM_RIGHT)) {
            axis = Axis.Y;
            section1 = RubiksCube.ROW_TOP;
            section2 = RubiksCube.ROW_BOTTOM;
        } else if (position.equals(CORNER_REAR_BOTTOM_LEFT)) {
            axis = Axis.X;
            section1 = RubiksCube.COLUMN_RIGHT;
            section2 = RubiksCube.COLUMN_LEFT;
        } else if (position.equals(CORNER_REAR_TOP_LEFT)) {
            axis = Axis.Y;
            section1 = RubiksCube.ROW_BOTTOM;
            section2 = RubiksCube.ROW_TOP;
        }

        addAndApplyRotation(new Rotation(axis, section1, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section1, direction.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.COUNTER_CLOCKWISE));

        addAndApplyRotation(new Rotation(axis, section1, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section1, direction.reverse()));

        addAndApplyRotation(new Rotation(axis, section2, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section2, direction.reverse()));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));

        addAndApplyRotation(new Rotation(axis, section2, direction));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(Axis.Z, RubiksCube.FACE_REAR, Direction.CLOCKWISE));
        addAndApplyRotation(new Rotation(axis, section2, direction.reverse()));
    }

    /**
     * *****************************************************************************************************************************************************
     */

    private CubiePosition findCubiePositionWithColors(Color... colors) {
        CubiePosition position = null;
        outerLoop:
        for (int x = 0; x < cube.getSize(); x++) {
            for (int y = 0; y < cube.getSize(); y++) {
                innerLoop:
                for (int z = 0; z < cube.getSize(); z++) {
                    position = new CubiePosition(x, y, z);
                    List<Color> positionColors = cube.getVisibleColors(position);

                    if (colors.length != positionColors.size()) continue;

                    for (Color color : colors) {
                        if (!positionColors.contains(color)) continue innerLoop;
                    }

                    break outerLoop;
                }
            }
        }
        return position;
    }

    // returns the color that is not on the rear face. position must be a rear edge
    private Color getEdgeNonRearColor(CubiePosition position) {
        Cubie cubie = cube.getCubie(position);
        if (position.isInRowTop()) return cubie.topColor;
        else if (position.isInRowBottom()) return cubie.bottomColor;
        else if (position.isInColumnLeft()) return cubie.leftColor;
        else return cubie.rightColor;
    }


    private List<CubiePosition> getFrontEdgePositions() {
        List<CubiePosition> edges = new ArrayList<CubiePosition>();
        edges.add(EDGE_FRONT_TOP);
        edges.add(EDGE_FRONT_BOTTOM);
        edges.add(EDGE_FRONT_LEFT);
        edges.add(EDGE_FRONT_RIGHT);
        return edges;
    }

    private List<CubiePosition> getFrontCornerPositions() {
        List<CubiePosition> corners = new ArrayList<CubiePosition>();
        corners.add(CORNER_FRONT_BOTTOM_LEFT);
        corners.add(CORNER_FRONT_BOTTOM_RIGHT);
        corners.add(CORNER_FRONT_TOP_LEFT);
        corners.add(CORNER_FRONT_TOP_RIGHT);
        return corners;
    }

    private List<CubiePosition> getMiddleEdgePositions() {
        List<CubiePosition> edges = new ArrayList<CubiePosition>();
        edges.add(EDGE_MIDDLE_TOP_RIGHT);
        edges.add(EDGE_MIDDLE_BOTTOM_RIGHT);
        edges.add(EDGE_MIDDLE_TOP_LEFT);
        edges.add(EDGE_MIDDLE_BOTTOM_LEFT);
        return edges;
    }

    private List<CubiePosition> getRearEdgePositions() {
        List<CubiePosition> edges = new ArrayList<CubiePosition>();
        edges.add(EDGE_REAR_TOP);
        edges.add(EDGE_REAR_BOTTOM);
        edges.add(EDGE_REAR_LEFT);
        edges.add(EDGE_REAR_RIGHT);
        return edges;
    }

    private List<CubiePosition> getRearCornerPositions() {
        List<CubiePosition> corners = new ArrayList<CubiePosition>();
        corners.add(CORNER_REAR_BOTTOM_LEFT);
        corners.add(CORNER_REAR_BOTTOM_RIGHT);
        corners.add(CORNER_REAR_TOP_LEFT);
        corners.add(CORNER_REAR_TOP_RIGHT);
        return corners;
    }
}