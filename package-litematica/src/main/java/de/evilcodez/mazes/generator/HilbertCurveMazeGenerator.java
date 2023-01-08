package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;
import de.evilcodez.mazes.utils.HilbertCurve;

import java.util.List;
import java.util.Random;

public class HilbertCurveMazeGenerator extends MazeGenerator {

    @Override
    public void generate(Grid grid, Random random) {
        if (grid.getDimensions() > 3) throw new IllegalArgumentException("Hilbert curve only supports up to 3 dimensions!");
        if (grid.getMask() != null) throw new IllegalArgumentException("Hilbert curve does not support masks!");
        if (grid.getDimensions() > 2) {
            for (int size : grid.getSizes()) {
                if (size % 2 == 1) throw new IllegalArgumentException("Hilbert curve only supports even sizes!");
            }
        }
        grid.initialize(true);
        final int dims = grid.getDimensions();
        final List<int[]> list = HilbertCurve.generate(grid.getSize(0), dims > 1 ? grid.getSize(1) : 1, dims > 2 ? grid.getSize(2) : 1);
        for (int i = 0; i < list.size() - 1; i++) {
            final int[] pos1 = list.get(i);
            final int[] pos2 = list.get(i + 1);
            final Cell c1 = dims > 2 ? grid.getCell(pos1) : grid.getCell(pos1[0], pos1[1]);
            final Cell c2 = dims > 2 ? grid.getCell(pos2) : grid.getCell(pos2[0], pos2[1]);
            grid.removeWalls(c1, c2);
        }
    }
}
