package de.evilcodez.mazes.utils;

import de.evilcodez.mazes.grid.Cell;
import de.evilcodez.mazes.grid.Grid;

public class TileExporter {

    public static boolean[][] export2D(Grid grid, int scale) {
        if (grid.getDimensions() != 2) throw new IllegalArgumentException("Grid must be 2 dimensional!");
        final int offset = 1;
        final boolean[][] tiles = new boolean[offset + grid.getSize(0) * 2][offset + grid.getSize(1) * 2];
        for (int x = offset; x < 1 + grid.getSize(0) * 2; x += 2) {
            for (int y = offset; y < 1 + grid.getSize(1) * 2; y += 2) {
                final Cell cell = grid.getCell((x - offset) / 2, (y - offset) / 2);
                if(cell == null) continue;
                tiles[x + 1][y + 1] = true;
                if(cell.hasWall(0, -1)) {
                    tiles[x - 1][y] = true;
                    tiles[x - 1][y + 1] = true;
                }
                if(cell.hasWall(1, -1)) {
                    tiles[x][y - 1] = true;
                    tiles[x + 1][y - 1] = true;
                    tiles[x - 1][y - 1] = true;
                }
                if(cell.hasWall(0, 1)) tiles[x + 1][y] = true;
                if(cell.hasWall(1, 1)) tiles[x][y + 1] = true;
            }
        }
        return scale > 1 ? scale(tiles, scale) : tiles;
    }

    public static boolean[][] scale(boolean[][] input, int scale) {
        final boolean[][] output = new boolean[input.length * scale][input[0].length * scale];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < scale; k++) {
                    for (int l = 0; l < scale; l++) {
                        output[i * scale + k][j * scale + l] = input[i][j];
                    }
                }
            }
        }
        return output;
    }

    public static final int FLAG_3D_WALL = 1;
    public static final int FLAG_3D_UP = 2;
    public static final int FLAG_3D_DOWN = 4;

    public static byte[][][] export3D(Grid grid, int scale) {
        if (grid.getDimensions() != 3) throw new IllegalArgumentException("Grid must be 3 dimensional!");
        final int offset = 1;
        final byte[][][] tiles = new byte[grid.getSize(1)][offset + grid.getSize(0) * 2][offset + grid.getSize(2) * 2];
        for (int y = 0; y < grid.getSize(1); y++) {
            final byte[][] tilesXY = tiles[y];
            for (int x = offset; x < 1 + grid.getSize(0) * 2; x += 2) {
                for (int z = offset; z < 1 + grid.getSize(2) * 2; z += 2) {
                    final Cell cell = grid.getCell((x - offset) / 2, y, (z - offset) / 2);
                    if(cell == null) continue;
                    final boolean up = cell.hasWall(1, 1);
                    final boolean down = cell.hasWall(1, -1);
                    if (!up) tilesXY[x][z] |= FLAG_3D_UP;
                    if (!down) tilesXY[x][z] |= FLAG_3D_DOWN;
                    tilesXY[x + 1][z + 1] = FLAG_3D_WALL;
                    if(cell.hasWall(0, -1)) {
                        tilesXY[x - 1][z] = FLAG_3D_WALL;
                        tilesXY[x - 1][z + 1] = FLAG_3D_WALL;
                    }
                    if(cell.hasWall(2, -1)) {
                        tilesXY[x][z - 1] = FLAG_3D_WALL;
                        tilesXY[x + 1][z - 1] = FLAG_3D_WALL;
                        tilesXY[x - 1][z - 1] = FLAG_3D_WALL;
                    }
                    if(cell.hasWall(0, 1)) tilesXY[x + 1][z] = FLAG_3D_WALL;
                    if(cell.hasWall(2, 1)) tilesXY[x][z + 1] = FLAG_3D_WALL;
                }
            }
            if (scale > 1) tiles[y] = scale(tilesXY, scale);
        }
        return tiles;
    }

    public static byte[][] scale(byte[][] input, int scale) {
        final byte[][] output = new byte[input.length * scale][input[0].length * scale];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                for (int k = 0; k < scale; k++) {
                    for (int l = 0; l < scale; l++) {
                        output[i * scale + k][j * scale + l] = input[i][j];
                    }
                }
            }
        }
        return output;
    }

    public static byte[][] export3DOld(Grid grid, int layer, int scale) {
        if (grid.getDimensions() != 3) throw new IllegalArgumentException("Grid must be 3 dimensional!");
        if(scale < 1) throw new IllegalArgumentException("scale can't be lower than 1");
        byte[][] data = new byte[1 + grid.getSize(0) * 2][1 + grid.getSize(2) * 2];

        for(int x = 0; x < data.length; ++x) {
            data[x][0] = 1;
        }
        for(int y = 0; y < data[0].length; ++y) {
            data[0][y] = 1;
        }

        // Export Maze
        int x1 = 1;
        for(int x = 0; x < grid.getSize(0); x++) {
            int z1 = 1;
            for(int z = 0; z < grid.getSize(2); z++) {
                Cell c = grid.getCell(x, layer, z);
                final boolean east = c.hasWall(0, 1);
                final boolean west = c.hasWall(0, -1);
                final boolean south = c.hasWall(2, 1);
                final boolean north = c.hasWall(2, -1);
                final boolean up = c.hasWall(1, 1);
                final boolean down = c.hasWall(1, -1);

                data[x1 + 1][z1] = (byte) (east ? 1 : 0);
                data[x1][z1 + 1] = (byte) (south ? 1 : 0);

                if(x1 - 1 >= 0) {
                    data[x1 - 1][z1] = (byte) (west ? 1 : 0);
                }
                if(z1 - 1 >= 0) {
                    data[x1][z1 - 1] = (byte) (north ? 1 : 0);
                }

                data[x1 + 1][z1 + 1] = 1;
                if(!up) {
                    data[x1][z1] |= (1 << 1);
                }
                if(!down) {
                    data[x1][z1] |= (1 << 2);
                }

                z1 += 2;
            }
            x1 += 2;
        }

        // Scale
        if(scale != 1) {
            final byte[][] scaled = new byte[data.length * scale][data.length * scale];
            for(int x = 0; x < data.length; ++x) {
                for(int y = 0; y < data[x].length; ++y) {
                    final byte val = data[x][y];
                    final int _x = x * scale;
                    final int _y = y * scale;
                    for(int xx = 0; xx < scale; ++xx) {
                        for(int yy = 0; yy < scale; ++yy) {
                            scaled[_x + xx][_y + yy] = val;
                        }
                    }
                }
            }
            return scaled;
        }

        return data;
    }
}
