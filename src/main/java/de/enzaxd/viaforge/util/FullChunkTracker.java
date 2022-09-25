package de.enzaxd.viaforge.util;

import kotlin.Pair;

import java.util.HashMap;
import java.util.Map;

public class FullChunkTracker {

    private static final Map<Pair<Integer, Integer>, Boolean> fullChunks = new HashMap<>();

    public static void track(final int chunkX, final int chunkZ, final boolean fullChunk) {
        FullChunkTracker.fullChunks.put(new Pair<>(chunkX, chunkZ), fullChunk);
    }

    public static boolean isFullChunk(final int chunkX, final int chunkZ) {
        for (Map.Entry<Pair<Integer, Integer>, Boolean> entry : fullChunks.entrySet()) {
            final Pair<Integer, Integer> position = entry.getKey();

            if (position.component1() == chunkX && position.component2() == chunkZ)
                return entry.getValue();
        }
        return false;
    }
}
