package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release;

import java.util.*;

public class BiomeCache {

    private final WorldChunkManager_r1_1 chunkmanager;
    private long lastCleanupTime;
    private final Map<Long, BiomeCacheBlock> cacheMap;
    private final List<BiomeCacheBlock> cache;

    public BiomeCache(WorldChunkManager_r1_1 worldchunkmanager) {
        lastCleanupTime = 0L;
        cacheMap = new HashMap<>();
        cache = new ArrayList<>();
        chunkmanager = worldchunkmanager;
    }

    public BiomeCacheBlock getBiomeCacheBlock(int i, int j) {
        i >>= 4;
        j >>= 4;
        long l = (long) i & 0xffffffffL | ((long) j & 0xffffffffL) << 32;
        BiomeCacheBlock biomecacheblock = cacheMap.get(l);
        if (biomecacheblock == null) {
            biomecacheblock = new BiomeCacheBlock(this, i, j);
            cacheMap.put(l, biomecacheblock);
            cache.add(biomecacheblock);
        }
        biomecacheblock.lastAccessTime = System.currentTimeMillis();
        return biomecacheblock;
    }

    public NewBiomeGenBase getBiomeGenAt(int i, int j) {
        return getBiomeCacheBlock(i, j).getBiomeGenAt(i, j);
    }

    public void cleanupCache() {
        long l = System.currentTimeMillis();
        long l1 = l - lastCleanupTime;
        if (l1 > 7500L || l1 < 0L) {
            lastCleanupTime = l;
            for (int i = 0; i < cache.size(); i++) {
                BiomeCacheBlock biomecacheblock = cache.get(i);
                long l2 = l - biomecacheblock.lastAccessTime;
                if (l2 > 30000L || l2 < 0L) {
                    cache.remove(i--);
                    long l3 = (long) biomecacheblock.xPosition & 0xffffffffL | ((long) biomecacheblock.zPosition & 0xffffffffL) << 32;
                    cacheMap.remove(l3);
                }
            }
        }
    }

    public NewBiomeGenBase[] getCachedBiomes(int i, int j) {
        return getBiomeCacheBlock(i, j).biomes;
    }

    static WorldChunkManager_r1_1 getWorldChunkManager(BiomeCache biomecache) {
        return biomecache.chunkmanager;
    }

}
