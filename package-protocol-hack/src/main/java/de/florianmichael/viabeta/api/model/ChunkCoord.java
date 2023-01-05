package de.florianmichael.viabeta.api.model;

import java.util.Objects;

public class ChunkCoord {

    public int chunkX;
    public int chunkZ;

    public ChunkCoord(final int chunkX, final int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public ChunkCoord(final long pos) {
        this.chunkX = (int) pos;
        this.chunkZ = (int) (pos >> 32);
    }

    public long toLong() {
        return toLong(this.chunkX, this.chunkZ);
    }

    public static long toLong(final int chunkX, final int chunkZ) {
        return (long) chunkX & 4294967295L | ((long) chunkZ & 4294967295L) << 32;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkCoord that = (ChunkCoord) o;
        return chunkX == that.chunkX &&
                chunkZ == that.chunkZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkX, chunkZ);
    }

    @Override
    public String toString() {
        return "ChunkCoord{" +
                "chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                '}';
    }

}
