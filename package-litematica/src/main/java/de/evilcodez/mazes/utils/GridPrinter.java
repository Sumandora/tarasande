package de.evilcodez.mazes.utils;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GridPrinter {

    public static BufferedImage toImage2D(Grid grid, int cellSize, int wallSize) {
        if(grid.getDimensions() != 2) throw new IllegalArgumentException("Grid must be 2 dimensional!");
        final BufferedImage image = new BufferedImage(grid.getSize(0) * cellSize, grid.getSize(1) * cellSize, BufferedImage.TYPE_INT_RGB);
        final Graphics2D graphics2D = image.createGraphics();
        for (int i = 0; i < grid.getSize(0); i++) {
            for (int j = 0; j < grid.getSize(1); j++) {
                final int x = i * cellSize;
                final int y = j * cellSize;
                final boolean notMasked = !grid.isCellMasked(i, j);
                graphics2D.setColor(notMasked ? Color.BLACK : Color.WHITE);
                graphics2D.fillRect(x, y, cellSize, cellSize);
                if (notMasked) continue;
                graphics2D.setColor(Color.BLACK);
                if(grid.getCell(i, j).hasWall(0, 1)) graphics2D.fillRect(x + cellSize - wallSize, y, wallSize, cellSize);
                if(grid.getCell(i, j).hasWall(1, 1)) graphics2D.fillRect(x, y + cellSize - wallSize, cellSize, wallSize);
                if(grid.getCell(i, j).hasWall(0, -1)) graphics2D.fillRect(x, y, wallSize, cellSize);
                if(grid.getCell(i, j).hasWall(1, -1)) graphics2D.fillRect(x, y, cellSize, wallSize);
            }
        }
        graphics2D.dispose();
        return image;
    }

    public static BufferedImage[] toImage3D(Grid grid, int cellSize, int wallSize) {
        if(grid.getDimensions() != 3) throw new IllegalArgumentException("Grid must be 3 dimensional!");
        final BufferedImage[] images = new BufferedImage[grid.getSize(2)];
        for (int k = 0; k < grid.getSize(2); k++) {
            final BufferedImage image = new BufferedImage(grid.getSize(0) * cellSize, grid.getSize(1) * cellSize, BufferedImage.TYPE_INT_RGB);
            images[k] = image;
            final Graphics2D graphics2D = image.createGraphics();
            for (int i = 0; i < grid.getSize(0); i++) {
                for (int j = 0; j < grid.getSize(0); j++) {
                    final int x = i * cellSize;
                    final int y = j * cellSize;
                    final boolean notMasked = !grid.isCellMasked(i, j);
                    graphics2D.setColor(notMasked ? Color.BLACK : Color.WHITE);
                    graphics2D.fillRect(x, y, cellSize, cellSize);
                    if (notMasked) continue;
                    graphics2D.setColor(Color.BLACK);
                    final Cell cell = grid.getCell(i, j, k);
                    if(cell.hasWall(0, 1)) graphics2D.fillRect(x + cellSize - wallSize, y, wallSize, cellSize);
                    if(cell.hasWall(1, 1)) graphics2D.fillRect(x, y + cellSize - wallSize, cellSize, wallSize);
                    if(cell.hasWall(0, -1)) graphics2D.fillRect(x, y, wallSize, cellSize);
                    if(cell.hasWall(1, -1)) graphics2D.fillRect(x, y, cellSize, wallSize);

                    if(!cell.hasWall(2, -1)) { // down
                        final float scale = 0.6f;
                        final int w = (int) (cellSize * scale);
                        final int off = (int) (cellSize / 2.0f - w / 2.0f);
                        graphics2D.fillRect(x + off, y + off, w, w);
                    }
                    if(!cell.hasWall(2, 1)) { // up
                        graphics2D.setColor(Color.RED);
                        final float scale = 0.4f;
                        final int w = (int) (cellSize * scale);
                        final int off = (int) (cellSize / 2.0f - w / 2.0f);
                        graphics2D.fillRect(x + off, y + off, w, w);
                    }
                }
            }
            graphics2D.dispose();
        }
        return images;
    }
}
