package de.evilcodez.mazes.grid;

import de.evilcodez.mazes.utils.DimensionUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Grid {

    private final int dimensions;
    private final int[] sizes;
    private final Cell[] cells;
    private boolean[] mask;

    public Grid(int... sizes) {
        if (sizes.length == 0) throw new IllegalArgumentException("Grid must have at least one dimension");
        this.sizes = sizes;
        this.dimensions = sizes.length;
        this.cells = new Cell[IntStream.of(sizes).reduce(1, (a, b) -> a * b)];
        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = new Cell(this, DimensionUtils.linearToMultiDimensional(this.sizes, i));
        }
    }

    public void initialize(boolean walls, int propertyCount, Consumer<Cell> initializer) {
        for (Cell cell : this.cells) {
            if (walls) cell.setWalls();
            else cell.clearWalls();
            if (propertyCount > 0) cell.properties = new Object[propertyCount];
            if (initializer != null) initializer.accept(cell);
        }
    }

    public void initialize(int propertyCount, Consumer<Cell> initializer) {
        this.initialize(true, propertyCount, initializer);
    }

    public void initialize(int propertyCount, boolean walls) {
        this.initialize(walls, propertyCount, null);
    }

    public void initialize(int propertyCount) {
        this.initialize(true, propertyCount, null);
    }

    public void initialize(boolean walls) {
        this.initialize(walls, 0, null);
    }

    public int totalSize() {
        return this.cells.length;
    }

    public int cellCount() {
        if (this.mask == null) return this.cells.length;
        int count = 0;
        for (boolean b : this.mask) {
            if (b) ++count;
        }
        return count;
    }

    public Cell getCell(int... position) {
        if(this.isCellAvailableAt(position)) {
            return cells[DimensionUtils.multiDimensionalToLinear(sizes, position)];
        }
        return null;
    }

    public Cell getCellIgnoreMask(int... position) {
        if (!this.isPositionOutOfBounds(position)) {
            return cells[DimensionUtils.multiDimensionalToLinear(sizes, position)];
        }
        return null;
    }

    public Cell getRandomCell(Random random) {
        Cell cell;
        do {
            final int[] position = new int[this.dimensions];
            for (int i = 0; i < position.length; i++) {
                position[i] = random.nextInt(this.sizes[i]);
            }
            cell = this.getCell(position);
        }while (cell == null);
        return cell;
    }

    public boolean isCellMasked(int... position) {
        if (this.mask == null) return true;
        return this.mask[DimensionUtils.multiDimensionalToLinear(sizes, position)];
    }

    public boolean isCellMasked(Cell cell) {
        if (this.mask == null) return true;
        return this.mask[DimensionUtils.multiDimensionalToLinear(sizes, cell.getPosition())];
    }

    public boolean isCellAvailableAt(int... position) {
        return !this.isPositionOutOfBounds(position) && this.isCellMasked(position);
    }

    public boolean isPositionOutOfBounds(int... position) {
        for (int i = 0; i < position.length; i++) {
            final int pos = position[i];
            if (pos < 0 || pos >= sizes[i]) return true;
        }
        return false;
    }

    public List<Cell> getNeighborsOf(int... position) {
        final List<Cell> neighbors = new ArrayList<>();
        for (int i = 0; i < position.length; i++) {
            final int _pos = position[i];
            final int size = this.sizes[i];
            if(_pos > 0) {
                final int[] nPos = new int[position.length];
                System.arraycopy(position, 0, nPos, 0, position.length);
                nPos[i] = _pos - 1;
                final Cell neighbor = this.getCell(nPos);
                if (neighbor != null) neighbors.add(neighbor);
            }
            if(_pos < size - 1) {
                final int[] nPos = new int[position.length];
                System.arraycopy(position, 0, nPos, 0, position.length);
                nPos[i] = _pos + 1;
                final Cell neighbor = this.getCell(nPos);
                if (neighbor != null) neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

    public List<Cell> getNeighborsOf(Cell cell) {
        return this.getNeighborsOf(cell.getPosition());
    }

    public void removeWalls(Cell cell1, Cell cell2) {
        if (!this.isCellMasked(cell1) || !this.isCellMasked(cell2)) return;
        final int[] pos1 = cell1.getPosition();
        final int[] pos2 = cell2.getPosition();
        if(pos1.length != pos2.length) throw new IllegalArgumentException("Cells must have the same dimensions!");

        boolean hasAlreadyFound = false;
        for (int i = 0; i < pos1.length; i++) {
            final int p1 = pos1[i];
            final int p2 = pos2[i];
            final int direction = p2 - p1;
            if(direction == 0) continue;
            if(direction < -1 || direction > 1) throw new IllegalArgumentException("Cells must be neighbors!");
            if(hasAlreadyFound) throw new IllegalArgumentException("Cells must be neighbors!");
            cell1.setWall(i, direction, false);
            cell2.setWall(i, -direction, false);
            hasAlreadyFound = true;
        }
        if(!hasAlreadyFound) throw new IllegalArgumentException("Cells must be neighbors!");
    }

    public boolean hasWallsBetween(Cell cell1, Cell cell2) {
        if (cell1 == null || cell2 == null) return true;
        if (!this.isCellMasked(cell1) || !this.isCellMasked(cell2)) return true;
        final int[] pos1 = cell1.getPosition();
        final int[] pos2 = cell2.getPosition();
        if(pos1.length != pos2.length) throw new IllegalArgumentException("Cells must have the same dimensions!");

        boolean hasAlreadyFound = false;
        for (int i = 0; i < pos1.length; i++) {
            final int p1 = pos1[i];
            final int p2 = pos2[i];
            final int direction = p2 - p1;
            if(direction == 0) continue;
            if(direction < -1 || direction > 1) throw new IllegalArgumentException("Cells must be neighbors!");
            if(hasAlreadyFound) throw new IllegalArgumentException("Cells must be neighbors!");
            if(cell1.hasWall(i, direction) && cell2.hasWall(i, -direction)) return true;
            hasAlreadyFound = true;
        }
        if(!hasAlreadyFound) throw new IllegalArgumentException("Cells must be neighbors!");
        return false;
    }

    public int getDimensions() {
        return dimensions;
    }

    public int[] getSizes() {
        return sizes;
    }

    public int getSize(int dimension) {
        return this.sizes[dimension];
    }

    public Cell[] getCells() {
        return cells;
    }

    public boolean[] getMask() {
        return mask;
    }

    public void setMask(boolean[] mask) {
        if (mask.length != this.cells.length) throw new IllegalArgumentException("Mask must have the same size as the grid!");
        this.mask = mask;
    }
}
