package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.IntCache;

public class GenLayerTemperatureMix extends GenLayer {
    private final GenLayer field_35505_b;
    private final int field_35506_c;

    public GenLayerTemperatureMix(GenLayer genlayer, GenLayer genlayer1, int i) {
        super(0L);
        parent = genlayer1;
        field_35505_b = genlayer;
        field_35506_c = i;
    }

    public int[] getInts(int i, int j, int k, int l) {
        int[] ai = parent.getInts(i, j, k, l);
        int[] ai1 = field_35505_b.getInts(i, j, k, l);
        int[] ai2 = IntCache.getIntCache(k * l);
        for (int i1 = 0; i1 < k * l; i1++) {
            ai2[i1] = ai1[i1] + (NewBiomeGenBase.BIOME_LIST[ai[i1]].getIntTemperature() - ai1[i1]) / (field_35506_c * 2 + 1);
        }

        return ai2;
    }
}
