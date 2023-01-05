package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release;

import java.util.ArrayList;
import java.util.List;

public class IntCacheInstance {

    private int intCacheSize = 256;
    private List<int[]> freeSmallArrays = new ArrayList<>();
    private List<int[]> inUseSmallArrays = new ArrayList<>();
    private List<int[]> freeLargeArrays = new ArrayList<>();
    private List<int[]> inUseLargeArrays = new ArrayList<>();

    public int[] getIntCache(int i) {
        if (i <= 256) {
            if (freeSmallArrays.size() == 0) {
                int[] ai = new int[256];
                inUseSmallArrays.add(ai);
                return ai;
            } else {
                int[] ai1 = freeSmallArrays.remove(freeSmallArrays.size() - 1);
                inUseSmallArrays.add(ai1);
                return ai1;
            }
        }
        if (i > intCacheSize) {
            intCacheSize = i;
            freeLargeArrays.clear();
            inUseLargeArrays.clear();
            int[] ai2 = new int[intCacheSize];
            inUseLargeArrays.add(ai2);
            return ai2;
        }
        if (freeLargeArrays.size() == 0) {
            int[] ai3 = new int[intCacheSize];
            inUseLargeArrays.add(ai3);
            return ai3;
        } else {
            int[] ai4 = freeLargeArrays.remove(freeLargeArrays.size() - 1);
            inUseLargeArrays.add(ai4);
            return ai4;
        }
    }

    public void resetIntCache() {
        if (freeLargeArrays.size() > 0) {
            freeLargeArrays.remove(freeLargeArrays.size() - 1);
        }
        if (freeSmallArrays.size() > 0) {
            freeSmallArrays.remove(freeSmallArrays.size() - 1);
        }
        freeLargeArrays.addAll(inUseLargeArrays);
        freeSmallArrays.addAll(inUseSmallArrays);
        inUseLargeArrays.clear();
        inUseSmallArrays.clear();
    }

    public void resetEverything() {
        intCacheSize = 256;
        freeSmallArrays = new ArrayList<>();
        inUseSmallArrays = new ArrayList<>();
        freeLargeArrays = new ArrayList<>();
        inUseLargeArrays = new ArrayList<>();
    }

}
