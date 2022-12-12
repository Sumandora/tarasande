package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.util.*;

public class HuntAndKillMazeGenerator extends MazeGenerator {

    public static final int INITIALIZED = 0;

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(true, 1, cell -> cell.put(INITIALIZED, false));

        Cell firstCell;
        int j = 0;
        do {
            firstCell = grid.getCells()[j++];
        }while (firstCell == null);
        firstCell.put(INITIALIZED, true);

        for (int i = 0; i < grid.getCells().length; i++) {
            final Cell baseCell = grid.getCells()[i];
            if (baseCell == null || baseCell.<Boolean>get(INITIALIZED)) continue;
            Cell current = baseCell;
            final Cell n = grid.getNeighborsOf(current).stream().filter(c -> c.get(INITIALIZED)).findAny().orElse(null);
            if (n == null) continue;
            grid.removeWalls(baseCell, n);
            while (true) {
                final List<Cell> neighbors = grid.getNeighborsOf(current);
                neighbors.removeIf(c -> c.<Boolean>get(INITIALIZED));
                if (neighbors.isEmpty()) break;
                final Cell neighbor = neighbors.get(random.nextInt(neighbors.size()));
                current.put(INITIALIZED, true);
                neighbor.put(INITIALIZED, true);
                grid.removeWalls(current, neighbor);
                current = neighbor;
            }
        }
    }
}
