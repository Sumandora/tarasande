package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.util.*;

public class BacktrackingMazeGenerator extends MazeGenerator {

    public static final int VISITED = 0;

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(true, 1, cell -> cell.put(VISITED, false));
        final Stack<Cell> stack = new Stack<>();

        Cell current = grid.getRandomCell(random);
        current.put(VISITED, true);
        stack.push(current);

        while (!stack.isEmpty()) {
            current = stack.peek();
            final List<Cell> neighbors = grid.getNeighborsOf(current.getPosition());
            neighbors.removeIf(cell -> cell.get(VISITED));
            if(neighbors.isEmpty()) {
                stack.pop();
                continue;
            }
            final Cell neighbor = neighbors.get(random.nextInt(neighbors.size()));
            grid.removeWalls(current, neighbor);
            neighbor.put(VISITED, true);
            stack.push(neighbor);
        }
    }
}
