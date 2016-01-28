package com.digdream.androidrubiksolver.android;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.digdream.androidrubiksolver.showcube.Cubie;
import com.digdream.androidrubiksolver.showcube.LameRubiksCubeSolver;
import com.digdream.androidrubiksolver.showcube.Rotation;
import com.digdream.androidrubiksolver.showcube.Rotation.Axis;
import com.digdream.androidrubiksolver.showcube.Rotation.Direction;
import com.digdream.androidrubiksolver.showcube.RubiksCube;
import com.digdream.androidrubiksolver.showcube.RubiksCubeSolver;

import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RubiksCubeGLRenderer implements Renderer {
    private static final float ZERO_F = 0.0f;
    private static final float ONE_F = 1.0f;
    private static final float TWO_F = 2.0f;
    private static final float CUBIE_GAP_F = 0.1f; // gap between cubies
    private static final float CUBIE_TRANSLATION_FACTOR = TWO_F + CUBIE_GAP_F;

    private static final float DEFAULT_CAMERA_ANGLE_X = 45.0f;
    private static final float DEFAULT_CAMERA_ANGLE_Y = 45.0f;
    private static final float DEFAULT_ZOOM = -18.0f;

    private static final int SECTION_ROTATE_STEP_DEGREES = 90;

    private float cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
    private float cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
    private float cameraAngleZ = ZERO_F;
    private float zoom = DEFAULT_ZOOM;

    private float[] columnAnglesX;
    private float[] rowAnglesY;
    private float[] faceAnglesZ;

    private int rotatingSectionX = -1;
    private int rotatingSectionY = -1;
    private int rotatingSectionZ = -1;
    private float angularVelocity = 5.0f; // speed and direction of rotating sections

    private RubiksCube rubiksCube;
    private RotationAnimatorThread scrambleAnimatorThread;
    private RotationAnimatorThread solutionAnimatorThread;

    public RubiksCubeGLRenderer(int size) {
        this.rubiksCube = new RubiksCube(size);
        this.columnAnglesX = new float[size];
        this.rowAnglesY = new float[size];
        this.faceAnglesZ = new float[size];
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
        gl.glClearDepthf(ONE_F);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glShadeModel(GL10.GL_SMOOTH);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (height == 0) height = 1;
        float aspect = (float) width / height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, aspect, 0.1f, 100.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        updateRotationAngles();
        drawRubiksCube(gl);
    }

    private void drawRubiksCube(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        // camera transformations
        gl.glTranslatef(ZERO_F, ZERO_F, zoom);
        gl.glRotatef(cameraAngleX, ONE_F, ZERO_F, ZERO_F);
        gl.glRotatef(cameraAngleY, ZERO_F, ONE_F, ZERO_F);
        gl.glRotatef(cameraAngleZ, ZERO_F, ZERO_F, ONE_F);

        int lastIdx = rubiksCube.getSize() - 1;
        for (int x = 0; x < rubiksCube.getSize(); x++) {
            for (int y = 0; y < rubiksCube.getSize(); y++) {
                for (int z = 0; z < rubiksCube.getSize(); z++) {
                    gl.glPushMatrix();

                    gl.glRotatef(columnAnglesX[x], ONE_F, ZERO_F, ZERO_F);
                    gl.glRotatef(rowAnglesY[y], ZERO_F, ONE_F, ZERO_F);
                    gl.glRotatef(faceAnglesZ[z], ZERO_F, ZERO_F, ONE_F);

                    // bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
                    float t = (float) lastIdx / 2;
                    gl.glTranslatef((x - t) * CUBIE_TRANSLATION_FACTOR, (y - t) * CUBIE_TRANSLATION_FACTOR, -(z - t) * CUBIE_TRANSLATION_FACTOR);

                    drawCubie(gl, rubiksCube.getVisibleFaces(x, y, z), rubiksCube.getCubie(x, y, z));

                    gl.glPopMatrix();
                }
            }
        }
    }

    private void drawCubie(GL10 gl, int visibleFaces, Cubie cubie) {
        float[] topFaceVertices = {
                -ONE_F, ONE_F, ONE_F,
                ONE_F, ONE_F, ONE_F,
                -ONE_F, ONE_F, -ONE_F,
                ONE_F, ONE_F, -ONE_F
        };
        Square topFace = new Square(topFaceVertices, ((visibleFaces & Cubie.FACELET_TOP) > 0) ? cubie.getTopColor() : null);
        topFace.draw(gl);

        float[] bottomFaceVertices = {
                -ONE_F, -ONE_F, ONE_F,
                ONE_F, -ONE_F, ONE_F,
                -ONE_F, -ONE_F, -ONE_F,
                ONE_F, -ONE_F, -ONE_F
        };
        Square bottomFace = new Square(bottomFaceVertices, ((visibleFaces & Cubie.FACELET_BOTTOM) > 0) ? cubie.getBottomColor() : null);
        bottomFace.draw(gl);

        float[] frontFaceVertices = {
                -ONE_F, -ONE_F, ONE_F,
                ONE_F, -ONE_F, ONE_F,
                -ONE_F, ONE_F, ONE_F,
                ONE_F, ONE_F, ONE_F
        };
        Square frontFace = new Square(frontFaceVertices, ((visibleFaces & Cubie.FACELET_FRONT) > 0) ? cubie.getFrontColor() : null);
        frontFace.draw(gl);

        float[] rearFaceVertices = {
                -ONE_F, -ONE_F, -ONE_F,
                ONE_F, -ONE_F, -ONE_F,
                -ONE_F, ONE_F, -ONE_F,
                ONE_F, ONE_F, -ONE_F
        };
        Square rearFace = new Square(rearFaceVertices, ((visibleFaces & Cubie.FACELET_REAR) > 0) ? cubie.getRearColor() : null);
        rearFace.draw(gl);

        float[] leftFaceVertices = {
                -ONE_F, -ONE_F, -ONE_F,
                -ONE_F, ONE_F, -ONE_F,
                -ONE_F, -ONE_F, ONE_F,
                -ONE_F, ONE_F, ONE_F,
        };
        Square leftFace = new Square(leftFaceVertices, ((visibleFaces & Cubie.FACELET_LEFT) > 0) ? cubie.getLeftColor() : null);
        leftFace.draw(gl);

        float[] rightFaceVertices = {
                ONE_F, -ONE_F, -ONE_F,
                ONE_F, ONE_F, -ONE_F,
                ONE_F, -ONE_F, ONE_F,
                ONE_F, ONE_F, ONE_F,
        };
        Square rightFace = new Square(rightFaceVertices, ((visibleFaces & Cubie.FACELET_RIGHT) > 0) ? cubie.getRightColor() : null);
        rightFace.draw(gl);
    }

    private boolean isRotating() {
        return rotatingSectionX + rotatingSectionY + rotatingSectionZ > -3;
    }

    public void rotating(int id) {
        if (scrambleAnimatorThread == null || !scrambleAnimatorThread.isAlive()) {
            scrambleAnimatorThread = new RotationAnimatorThread() {
                @Override
                protected int getSection(int i) {
                    switch (i) {
                        case 1:
                            return 0;
                        case 2:
                            return 0;
                        default:
                            return 0;
                    }
                    //return new Random().nextInt(rubiksCube.getSize());
                }

                @Override
                protected Axis getAxis(int i) {
                    switch (i) {
                        case 0:
                            return Axis.values()[0];
                        case 1:
                            return Axis.values()[2];
                        case 2:
                            return Axis.values()[1];
                        default:
                            return Axis.values()[2];
                    }

                    //return Axis.values()[new Random().nextInt(Axis.values().length)];
                }

                @Override
                protected boolean isReverse(int i) {
                    return true;
                    //return new Random().nextBoolean();
                }

                @Override
                protected boolean isComplete(int i) {
                    return false;
                }
            };
            scrambleAnimatorThread.xuanzhuan(id);
        } else {
            scrambleAnimatorThread.terminate();
        }

    }

    private void updateRotationAngles() {
        Direction direction = (angularVelocity > 0) ? Direction.COUNTER_CLOCKWISE : Direction.CLOCKWISE;

        if (rotatingSectionX >= 0) {
            columnAnglesX[rotatingSectionX] += angularVelocity;
            if (columnAnglesX[rotatingSectionX] % SECTION_ROTATE_STEP_DEGREES == 0) {
                columnAnglesX[rotatingSectionX] = 0;
                rubiksCube.applyRotation(new Rotation(Axis.X, rotatingSectionX, direction));
                rotatingSectionX = -1;
            }
        } else if (rotatingSectionY >= 0) {
            rowAnglesY[rotatingSectionY] += angularVelocity;
            if (rowAnglesY[rotatingSectionY] % SECTION_ROTATE_STEP_DEGREES == 0) {
                rowAnglesY[rotatingSectionY] = 0;
                rubiksCube.applyRotation(new Rotation(Axis.Y, rotatingSectionY, direction));
                rotatingSectionY = -1;
            }
        } else if (rotatingSectionZ >= 0) {
            faceAnglesZ[rotatingSectionZ] += angularVelocity;
            if (faceAnglesZ[rotatingSectionZ] % SECTION_ROTATE_STEP_DEGREES == 0) {
                faceAnglesZ[rotatingSectionZ] = 0;
                rubiksCube.applyRotation(new Rotation(Axis.Z, rotatingSectionZ, direction));
                rotatingSectionZ = -1;
            }
        }
    }

    // section is the index of the column/row/face that is to be rotated.
    // if reverse is true then rotation will be clockwise
    private void rotateSection(int section, Axis axis, boolean reverse) {
        // make sure nothing is currently rotating
        if (!isRotating()) {
            if (axis == Axis.X) rotatingSectionX = section;
            if (axis == Axis.Y) rotatingSectionY = section;
            if (axis == Axis.Z) rotatingSectionZ = section;
            angularVelocity = reverse ? -Math.abs(angularVelocity) : Math.abs(angularVelocity);
        }
    }

    public void toggleScrambleCube() {
        if (scrambleAnimatorThread == null || !scrambleAnimatorThread.isAlive()) {
            scrambleAnimatorThread = new RotationAnimatorThread() {
                @Override
                protected int getSection(int i) {
                    return new Random().nextInt(rubiksCube.getSize());
                }

                @Override
                protected Axis getAxis(int i) {
                    return Axis.values()[new Random().nextInt(Axis.values().length)];
                }

                @Override
                protected boolean isReverse(int i) {
                    return new Random().nextBoolean();
                }

                @Override
                protected boolean isComplete(int i) {
                    return false;
                }
            };
            scrambleAnimatorThread.start();
        } else {
            scrambleAnimatorThread.terminate();
        }
    }

    public void toggleSolveCube() {
        if (solutionAnimatorThread == null || !solutionAnimatorThread.isAlive()) {
            RubiksCubeSolver solver = new LameRubiksCubeSolver(rubiksCube.getCopy());
            final List<Rotation> rotations = solver.getSolution();
            System.out.println("Found solution with " + rotations.size() + " moves");

            solutionAnimatorThread = new RotationAnimatorThread() {
                @Override
                protected int getSection(int i) {
                    return rotations.get(i).getSection();
                }

                @Override
                protected Axis getAxis(int i) {
                    return rotations.get(i).getAxis();
                }

                @Override
                protected boolean isReverse(int i) {
                    return rotations.get(i).isClockwise();
                }

                @Override
                protected boolean isComplete(int i) {
                    return (i == rotations.size());
                }
            };
            solutionAnimatorThread.start();
        } else {
            solutionAnimatorThread.terminate();
        }
    }

    private abstract class RotationAnimatorThread extends Thread {
        private boolean isTerminated = false;

        public void terminate() {
            isTerminated = true;
        }

        protected abstract int getSection(int i);

        protected abstract Axis getAxis(int i);

        protected abstract boolean isReverse(int i);

        protected abstract boolean isComplete(int i);

        @Override
        public void run() {
            int i = 0;
            while (!isTerminated && !isComplete(i)) {
                while (isRotating()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
                rotateSection(getSection(i), getAxis(i), isReverse(i));
                i++;
            }
        }

        public void xuanzhuan(int i) {
            rotateSection(getSection(i), getAxis(i), isReverse(i));
        }
    }

    public void rotateCameraX(float cameraAngleX) {
        this.cameraAngleX += cameraAngleX;
    }

    public void rotateCameraY(float cameraAngleY) {
        this.cameraAngleY += cameraAngleY;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void resetCamera() {
        cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
        cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
        cameraAngleZ = ZERO_F;
        zoom = DEFAULT_ZOOM;
    }

    public void resetCube() {
        columnAnglesX = new float[rubiksCube.getSize()];
        rowAnglesY = new float[rubiksCube.getSize()];
        faceAnglesZ = new float[rubiksCube.getSize()];
        rubiksCube.resetState();
    }
}
