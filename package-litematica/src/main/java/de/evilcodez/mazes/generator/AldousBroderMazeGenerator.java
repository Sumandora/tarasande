package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.util.List;
import java.util.Random;

public class AldousBroderMazeGenerator extends MazeGenerator {

    public static final int VISITED = 0;

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(true, 1, cell -> cell.put(VISITED, false));
        final int cellCount = grid.cellCount();
        Cell current = grid.getRandomCell(random);
        int visited = 1;
        current.put(VISITED, true);

        while (visited < cellCount) {
            final List<Cell> neighbors = grid.getNeighborsOf(current);
            final Cell neighbor = neighbors.get(random.nextInt(neighbors.size()));
            if(!neighbor.<Boolean>get(VISITED)) {
                grid.removeWalls(current, neighbor);
                neighbor.put(VISITED, true);
                ++visited;
            }
            current = neighbor;
        }
    }
}
