package com.digdream.androidrubiksolver.android;

import com.digdream.androidrubiksolver.showcube.Cubie.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Square {
    private static final float ZERO_F = 0.0f;
    private static final float ONE_F = 1.0f;

    private float[] vertices;
    private Color color;
    private FloatBuffer vertexBuffer;

    public Square(float[] vertices, Color color) {
        this.vertices = vertices;
        this.color = color;

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glColor4f(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
        if (color != null) glApplyColor(gl, color);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void glApplyColor(GL10 gl, Color color) {
        switch (color) {
            case WHITE:
                gl.glColor4f(ONE_F, ONE_F, ONE_F, ZERO_F);
                break;
            case YELLOW:
                gl.glColor4f(ONE_F, ONE_F, ZERO_F, ZERO_F);
                break;
            case GREEN:
                gl.glColor4f(ZERO_F, ONE_F, ZERO_F, ZERO_F);
                break;
            case ORANGE:
                gl.glColor4f(ONE_F, ONE_F / 2, ZERO_F, ZERO_F);
                break;
            case BLUE:
                gl.glColor4f(ZERO_F, ZERO_F, ONE_F, ZERO_F);
                break;
            case RED:
                gl.glColor4f(ONE_F, ZERO_F, ZERO_F, ZERO_F);
                break;
        }
    }
}
