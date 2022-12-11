package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.util.*;

public class BinaryTreeMazeGenerator extends MazeGenerator {

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(true);
        final List<Integer> list = new ArrayList<>(grid.getDimensions());
        for (int i = 0; i < grid.getDimensions(); i++) list.add(i);
        final int[] position = new int[grid.getDimensions()];
        for (Cell cell : grid.getCells()) {
            if (!grid.isCellMasked(cell)) continue;
            Collections.shuffle(list, random);
            for (int dimension : list) {
                System.arraycopy(cell.getPosition(), 0, position, 0, position.length);
                ++position[dimension];
                final Cell neighbor = grid.getCell(position);
                if (neighbor != null) {
                    grid.removeWalls(cell, neighbor);
                    break;
                }
            }
        }
    }
}
