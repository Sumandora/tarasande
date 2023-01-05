package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release;

public class IntCache {

    private static final ThreadLocal<IntCacheInstance> INT_CACHE = ThreadLocal.withInitial(IntCacheInstance::new);

    public static int[] getIntCache(int i) {
        return INT_CACHE.get().getIntCache(i);
    }

    public static void resetIntCache() {
        INT_CACHE.get().resetIntCache();
    }

    public static void resetEverything() {
        INT_CACHE.get().resetEverything();
    }

}
