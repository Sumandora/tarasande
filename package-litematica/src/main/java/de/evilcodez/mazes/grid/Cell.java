package de.evilcodez.mazes.grid;

import de.evilcodez.mazes.utils.DimensionUtils;

import java.util.Arrays;

public class Cell {

    private final Grid grid;
    private final int[] position;
    private final boolean[][] walls;
    Object[] properties;

    public Cell(Grid grid, int... position) {
        this.grid = grid;
        this.position = position;
        this.walls = new boolean[grid.getDimensions()][2];
    }

    public Grid getGrid() {
        return grid;
    }

    public int[] getPosition() {
        return position;
    }

    public boolean hasWall(int dimension, int direction) {
        if(direction == 0) throw new IllegalArgumentException("Direction must be 1 or -1");
        return this.walls[dimension][direction > 0 ? 1 : 0];
    }

    public void setWall(int dimension, int direction, boolean wall) {
        if(direction == 0) throw new IllegalArgumentException("Direction must be 1 or -1");
        this.walls[dimension][direction > 0 ? 1 : 0] = wall;
    }

    public void clearWalls() {
        for (boolean[] wall : this.walls) {
            Arrays.fill(wall, false);
        }
    }

    public void setWalls() {
        for (boolean[] wall : this.walls) {
            Arrays.fill(wall, true);
        }
    }

    public Object[] getProperties() {
        return properties;
    }

    public <T> T get(int index) {
        return (T) properties[index];
    }

    public void put(int index, Object value) {
        properties[index] = value;
    }

    public int getIndex() {
        return DimensionUtils.multiDimensionalToLinear(grid.getSizes(), position);
    }
}
