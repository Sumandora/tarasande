package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.IntCache;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;

public class GenLayerVillageLandscape_b1_8 extends GenLayer {
    private final NewBiomeGenBase[] allowedBiomes;

    public GenLayerVillageLandscape_b1_8(long l, GenLayer genlayer) {
        super(l);
        allowedBiomes = (new NewBiomeGenBase[]
                {
                        NewBiomeGenBase.desert, NewBiomeGenBase.forest, NewBiomeGenBase.extremeHills, NewBiomeGenBase.swampland, NewBiomeGenBase.plains, NewBiomeGenBase.taiga
                });
        parent = genlayer;
    }

    public int[] getInts(int i, int j, int k, int l) {
        int[] ai = parent.getInts(i, j, k, l);
        int[] ai1 = IntCache.getIntCache(k * l);
        for (int i1 = 0; i1 < l; i1++) {
            for (int j1 = 0; j1 < k; j1++) {
                initChunkSeed(j1 + i, i1 + j);
                ai1[j1 + i1 * k] = ai[j1 + i1 * k] <= 0 ? 0 : allowedBiomes[nextInt(allowedBiomes.length)].biomeID;
            }
        }

        return ai1;
    }
}
