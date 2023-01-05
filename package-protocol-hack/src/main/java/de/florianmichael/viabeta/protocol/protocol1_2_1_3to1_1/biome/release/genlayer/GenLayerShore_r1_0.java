package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.IntCache;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;

public class GenLayerShore_r1_0 extends GenLayer {
    public GenLayerShore_r1_0(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    public int[] getInts(int i, int j, int k, int l) {
        int ai[] = parent.getInts(i - 1, j - 1, k + 2, l + 2);
        int ai1[] = IntCache.getIntCache(k * l);
        for (int i1 = 0; i1 < l; i1++) {
            for (int j1 = 0; j1 < k; j1++) {
                initChunkSeed(j1 + i, i1 + j);
                int k1 = ai[j1 + 1 + (i1 + 1) * (k + 2)];
                if (k1 == NewBiomeGenBase.mushroomIsland.biomeID) {
                    int l1 = ai[j1 + 1 + ((i1 + 1) - 1) * (k + 2)];
                    int i2 = ai[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                    int j2 = ai[((j1 + 1) - 1) + (i1 + 1) * (k + 2)];
                    int k2 = ai[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                    if (l1 == NewBiomeGenBase.ocean.biomeID || i2 == NewBiomeGenBase.ocean.biomeID || j2 == NewBiomeGenBase.ocean.biomeID || k2 == NewBiomeGenBase.ocean.biomeID) {
                        ai1[j1 + i1 * k] = NewBiomeGenBase.mushroomIslandShore.biomeID;
                    } else {
                        ai1[j1 + i1 * k] = k1;
                    }
                } else {
                    ai1[j1 + i1 * k] = k1;
                }
            }

        }

        return ai1;

    }
}
