package com.digdream.androidrubiksolver.showcube;

import java.util.ArrayList;
import java.util.List;

public abstract class RubiksCubeSolver {
    protected RubiksCube cube;
    protected List<Rotation> rotations;

    public abstract List<Rotation> getSolution();

    public RubiksCubeSolver(RubiksCube cube) {
        this.cube = cube;
        this.rotations = new ArrayList<Rotation>();
    }

    protected void addAndApplyRotation(Rotation rotation) {
        rotations.add(rotation);
        cube.applyRotation(rotation);
    }
}
