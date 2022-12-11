package de.evilcodez.mazes.generator;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;
import de.evilcodez.mazes.utils.DimensionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class KruskalsMazeGenerator extends MazeGenerator {

    public static final int SET_ID = 0;

    @Override
    public void generate(Grid grid, Random random) {
        // TODO: speed improve this
        grid.initialize(true, 1, cell -> cell.put(SET_ID, new HashSet<>(Collections.singleton(cell))));
        final Set<Wall> wallsSet = new HashSet<>();
        for (Cell cell : grid.getCells()) {
            wallsSet.addAll(grid.getNeighborsOf(cell).stream().map(c -> new Wall(cell, c)).collect(Collectors.toList()));
        }
        final List<Wall> walls = new ArrayList<>(wallsSet);
        wallsSet.clear();
        Collections.shuffle(walls, random);
        for (Wall wall : walls) {
            final Set<Cell> set1 = wall.cell1.get(SET_ID);
            final Set<Cell> set2 = wall.cell2.get(SET_ID);
            if(set1 != set2) {
                grid.removeWalls(wall.cell1, wall.cell2);
                for (Cell cell : set2) {
                    cell.put(SET_ID, set1);
                }
                set1.addAll(set2);
                set2.clear();
            }
        }
    }

    private static class Wall {

        final Cell cell1;
        final Cell cell2;

        public Wall(Cell cell1, Cell cell2) {
            this.cell1 = cell1;
            this.cell2 = cell2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Wall wall = (Wall) o;
            return Objects.equals(cell1, wall.cell1) && Objects.equals(cell2, wall.cell2)
                    || Objects.equals(cell1, wall.cell2) && Objects.equals(cell2, wall.cell1);
        }

        @Override
        public int hashCode() {
            final List<Cell> list = new ArrayList<>(2);
            list.add(cell1);
            list.add(cell2);
            list.sort(Comparator.comparingInt(c -> DimensionUtils.multiDimensionalToLinear(c.getPosition(), c.getGrid().getSizes())));
            return Objects.hash(cell1, cell2);
        }
    }
}
