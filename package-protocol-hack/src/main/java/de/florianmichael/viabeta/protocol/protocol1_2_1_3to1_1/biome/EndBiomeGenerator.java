package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;

import java.util.Arrays;

public class EndBiomeGenerator implements IWorldChunkManager {

    private static final byte[] END_BIOME_DATA;

    static {
        END_BIOME_DATA = new byte[256];
        Arrays.fill(END_BIOME_DATA, (byte) NewBiomeGenBase.sky.biomeID);
    }

    @Override
    public byte[] getBiomeDataAt(int chunkX, int chunkZ) {
        return END_BIOME_DATA;
    }

}
