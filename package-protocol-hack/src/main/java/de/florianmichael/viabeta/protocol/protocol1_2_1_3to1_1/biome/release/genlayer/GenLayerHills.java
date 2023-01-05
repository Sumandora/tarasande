package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.IntCache;

public class GenLayerHills extends GenLayer {
    public GenLayerHills(long l, GenLayer genlayer) {
        super(l);
        parent = genlayer;
    }

    public int[] getInts(int i, int j, int k, int l) {
        int[] ai = parent.getInts(i - 1, j - 1, k + 2, l + 2);
        int[] ai1 = IntCache.getIntCache(k * l);
        for (int i1 = 0; i1 < l; i1++) {
            for (int j1 = 0; j1 < k; j1++) {
                initChunkSeed(j1 + i, i1 + j);
                int k1 = ai[j1 + 1 + (i1 + 1) * (k + 2)];
                if (nextInt(3) == 0) {
                    int l1 = k1;
                    if (k1 == NewBiomeGenBase.desert.biomeID) {
                        l1 = NewBiomeGenBase.desertHills.biomeID;
                    } else if (k1 == NewBiomeGenBase.forest.biomeID) {
                        l1 = NewBiomeGenBase.forestHills.biomeID;
                    } else if (k1 == NewBiomeGenBase.taiga.biomeID) {
                        l1 = NewBiomeGenBase.taigaHills.biomeID;
                    } else if (k1 == NewBiomeGenBase.plains.biomeID) {
                        l1 = NewBiomeGenBase.forest.biomeID;
                    } else if (k1 == NewBiomeGenBase.icePlains.biomeID) {
                        l1 = NewBiomeGenBase.iceMountains.biomeID;
                    }
                    if (l1 != k1) {
                        int i2 = ai[j1 + 1 + ((i1 + 1) - 1) * (k + 2)];
                        int j2 = ai[j1 + 1 + 1 + (i1 + 1) * (k + 2)];
                        int k2 = ai[((j1 + 1) - 1) + (i1 + 1) * (k + 2)];
                        int l2 = ai[j1 + 1 + (i1 + 1 + 1) * (k + 2)];
                        if (i2 == k1 && j2 == k1 && k2 == k1 && l2 == k1) {
                            ai1[j1 + i1 * k] = l1;
                        } else {
                            ai1[j1 + i1 * k] = k1;
                        }
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
