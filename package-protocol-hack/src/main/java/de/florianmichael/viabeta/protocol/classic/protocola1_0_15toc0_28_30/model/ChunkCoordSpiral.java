package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.model;

import de.florianmichael.viabeta.api.model.ChunkCoord;

import java.util.*;

public class ChunkCoordSpiral implements Iterable<ChunkCoord> {

    private final ChunkCoord center;
    private final ChunkCoord lowerBound;
    private final ChunkCoord upperBound;
    private final int step;

    private boolean returnCenter = true;

    public ChunkCoordSpiral(ChunkCoord center, ChunkCoord lowerBound, ChunkCoord upperBound, int step) {
        this.center = center;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.step = step;
    }

    public ChunkCoordSpiral(ChunkCoord center, ChunkCoord radius, int step) {
        this(center, new ChunkCoord(center.chunkX - radius.chunkX, center.chunkZ - radius.chunkZ), new ChunkCoord(center.chunkX + radius.chunkX, center.chunkZ + radius.chunkZ), step);
    }

    public ChunkCoordSpiral(ChunkCoord center, ChunkCoord radius) {
        this(center, radius, 1);
    }

    @Override
    public Iterator<ChunkCoord> iterator() {
        return new Iterator<>() {
            int x = center.chunkX;
            int z = center.chunkZ;

            float n = 1;
            int floorN = 1;
            int i = 0;
            int j = 0;

            @Override
            public boolean hasNext() {
                return returnCenter || x >= lowerBound.chunkX && x <= upperBound.chunkX && z >= lowerBound.chunkZ && z <= upperBound.chunkZ;
            }

            @Override
            public ChunkCoord next() {
                if (returnCenter) {
                    returnCenter = false;
                    return new ChunkCoord(x, z);
                }

                floorN = (int) Math.floor(n);
                if (j < floorN) {
                    switch (i % 4) {
                        case 0 -> z += step;
                        case 1 -> x += step;
                        case 2 -> z -= step;
                        case 3 -> x -= step;
                    }
                    j++;
                    return new ChunkCoord(x, z);
                }
                j = 0;
                n += 0.5;
                i++;
                return next();
            }
        };
    }

    @Override
    public Spliterator<ChunkCoord> spliterator() {
        return Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED);
    }
}
