package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class PrimsMazeGenerator extends MazeGenerator {

    public static final int PART_OF_MAZE = 0;

    @Override
    public void generate(Grid grid, Random random) {
        grid.initialize(true, 1, cell -> cell.put(PART_OF_MAZE, false));
        final Cell firstCell = grid.getRandomCell(random);
        firstCell.put(PART_OF_MAZE, true);
        final List<Wall> walls = grid.getNeighborsOf(firstCell).stream().map(c -> new Wall(firstCell, c)).collect(Collectors.toList());
        while (!walls.isEmpty()) {
            final int wallIndex = random.nextInt(walls.size());
            final Wall wall = walls.get(wallIndex);
            final boolean b1 = wall.cell1.get(PART_OF_MAZE);
            final boolean b2 = wall.cell2.get(PART_OF_MAZE);
            if (b1 ^ b2) {
                grid.removeWalls(wall.cell1, wall.cell2);
                final Cell unvisited = b1 ? wall.cell2 : wall.cell1;
                unvisited.put(PART_OF_MAZE, true);
                walls.addAll(grid.getNeighborsOf(unvisited).stream().map(c -> new Wall(unvisited, c)).collect(Collectors.toList()));
            }
            walls.remove(wallIndex);
        }
    }

    private static class Wall {

        final Cell cell1;
        final Cell cell2;

        public Wall(Cell cell1, Cell cell2) {
            this.cell1 = cell1;
            this.cell2 = cell2;
        }
    }
}
