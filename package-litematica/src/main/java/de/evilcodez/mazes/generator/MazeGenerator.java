package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Grid;

import java.util.Random;

public abstract class MazeGenerator {

    public abstract void generate(Grid grid, Random random);

    public Grid generate(Random random, int... size) {
        final Grid grid = new Grid(size);
        this.generate(grid, random);
        return grid;
    }

    public Grid generate(int... size) {
        return this.generate(new Random(), size);
    }
}
