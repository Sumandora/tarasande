package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Grid;
import de.evilcodez.mazes.grid.Cell;

import java.util.*;

public class WilsonMazeGenerator extends MazeGenerator {

    public static final int INITIALIZED = 0;

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(false, 1, cell -> cell.put(INITIALIZED, false));
        final Cell firstCell = grid.getRandomCell(random);
        firstCell.put(INITIALIZED, true);
        firstCell.setWalls();

        for (int i = 0; i < grid.getCells().length; i++) {
            final Cell baseCell = grid.getCells()[i];
            if (baseCell == null || baseCell.<Boolean>get(INITIALIZED)) continue;
            Cell current = baseCell;
            final Stack<Cell> path = new Stack<>();
            path.add(current);
            boolean first = true;
            while (true) {
                final List<Cell> neighbors = grid.getNeighborsOf(current);
                if (first && neighbors.isEmpty()) {
                    current.setWalls();
                    break;
                }
                first = false;
                final Cell neighbor = neighbors.get(random.nextInt(neighbors.size()));
                if (path.contains(neighbor)) {
                    Cell c = current;
                    while (!path.isEmpty() && !c.equals(neighbor)) {
                        path.pop();
                        c = path.peek();
                    }
                    if(path.isEmpty()) throw new IllegalStateException("Path is empty!");
                    current = neighbor;
                    continue;
                }
                path.add(neighbor);
                current = neighbor;
                if (neighbor.get(INITIALIZED)) {
                    final List<Cell> list = new ArrayList<>(path);
                    if(list.size() < 2) throw new IllegalStateException("Path is too short!");
                    Cell prevCell = list.get(0);
                    for (int j = 1; j < list.size(); j++) {
                        final Cell cell = list.get(j);
                        if (!prevCell.<Boolean>get(INITIALIZED)) {
                            prevCell.put(INITIALIZED, true);
                            prevCell.setWalls();
                        }
                        if(!cell.<Boolean>get(INITIALIZED)) {
                            cell.put(INITIALIZED, true);
                            cell.setWalls();
                        }
                        grid.removeWalls(prevCell, cell);
                        prevCell = cell;
                    }
                    break;
                }
            }
        }
    }
}
