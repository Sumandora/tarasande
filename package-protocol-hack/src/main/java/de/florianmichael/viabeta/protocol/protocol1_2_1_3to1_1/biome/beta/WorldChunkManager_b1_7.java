package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.beta;

import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.model.ChunkCoord;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.IWorldChunkManager;

import java.util.Random;

public class WorldChunkManager_b1_7 implements IWorldChunkManager {

    private NoiseGeneratorOctaves2 field_4194_e;
    private NoiseGeneratorOctaves2 field_4193_f;
    private NoiseGeneratorOctaves2 field_4192_g;
    public double[] temperature;
    public double[] humidity;
    public double[] field_4196_c;
    public OldBiomeGenBase[] field_4195_d;

    private final boolean remapBasedOnColor;

    public WorldChunkManager_b1_7(final long seed) {
        field_4194_e = new NoiseGeneratorOctaves2(new Random(seed * 9871L), 4);
        field_4193_f = new NoiseGeneratorOctaves2(new Random(seed * 39811L), 4);
        field_4192_g = new NoiseGeneratorOctaves2(new Random(seed * 0x84a59L), 2);
        this.remapBasedOnColor = ViaBeta.getConfig().isRemapBasedOnColor();
    }

    @Override
    public byte[] getBiomeDataAt(int chunkX, int chunkZ) {
        final byte[] biomeData = new byte[256];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (this.remapBasedOnColor) {
                    biomeData[z << 4 | x] = (byte) this.getBiomeGenAt((chunkX * 16) + x, (chunkZ * 16) + z).colorBiomeID;
                } else {
                    biomeData[z << 4 | x] = (byte) this.getBiomeGenAt((chunkX * 16) + x, (chunkZ * 16) + z).biomeID;
                }
            }
        }
        return biomeData;
    }

    public NewBiomeGenBase getBiomeGenAtChunkCoord(ChunkCoord chunkcoordintpair) {
        return getBiomeGenAt(chunkcoordintpair.chunkX << 4, chunkcoordintpair.chunkZ << 4);
    }

    public NewBiomeGenBase getBiomeGenAt(int i, int j) {
        final OldBiomeGenBase oldBiomeGenBase = func_4069_a(i, j, 1, 1)[0];
        if (oldBiomeGenBase.equals(OldBiomeGenBase.rainforest)) {
            return NewBiomeGenBase.jungle;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.swampland)) {
            return NewBiomeGenBase.swampland;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.seasonalForest)) {
            return NewBiomeGenBase.forest;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.forest)) {
            return NewBiomeGenBase.forest;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.savanna)) {
            return NewBiomeGenBase.savanna;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.shrubland)) {
            return NewBiomeGenBase.mutatedJungleEdge;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.taiga)) {
            return NewBiomeGenBase.taiga;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.desert)) {
            return NewBiomeGenBase.desert;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.plains)) {
            return NewBiomeGenBase.plains;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.iceDesert)) {
            return NewBiomeGenBase.icePlains;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.tundra)) {
            return NewBiomeGenBase.icePlains;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.hell)) {
            return NewBiomeGenBase.hell;
        } else if (oldBiomeGenBase.equals(OldBiomeGenBase.sky)) {
            return NewBiomeGenBase.sky;
        } else {
            return NewBiomeGenBase.plains;
        }
    }

    public double getTemperature(int i, int j) {
        temperature = field_4194_e.func_4112_a(temperature, i, j, 1, 1, 0.02500000037252903D, 0.02500000037252903D, 0.5D);
        return temperature[0];
    }

    public OldBiomeGenBase[] func_4069_a(int i, int j, int k, int l) {
        field_4195_d = loadBlockGeneratorData(field_4195_d, i, j, k, l);
        return field_4195_d;
    }

    public double[] getTemperatures(double[] ad, int i, int j, int k, int l) {
        if (ad == null || ad.length < k * l) {
            ad = new double[k * l];
        }
        ad = field_4194_e.func_4112_a(ad, i, j, k, l, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
        field_4196_c = field_4192_g.func_4112_a(field_4196_c, i, j, k, l, 0.25D, 0.25D, 0.58823529411764708D);
        int i1 = 0;
        for (int j1 = 0; j1 < k; j1++) {
            for (int k1 = 0; k1 < l; k1++) {
                double d = field_4196_c[i1] * 1.1000000000000001D + 0.5D;
                double d1 = 0.01D;
                double d2 = 1.0D - d1;
                double d3 = (ad[i1] * 0.14999999999999999D + 0.69999999999999996D) * d2 + d * d1;
                d3 = 1.0D - (1.0D - d3) * (1.0D - d3);
                if (d3 < 0.0D) {
                    d3 = 0.0D;
                }
                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }
                ad[i1] = d3;
                i1++;
            }

        }

        return ad;
    }

    public OldBiomeGenBase[] loadBlockGeneratorData(OldBiomeGenBase abiomegenbase[], int i, int j, int k, int l) {
        if (abiomegenbase == null || abiomegenbase.length < k * l) {
            abiomegenbase = new OldBiomeGenBase[k * l];
        }
        temperature = field_4194_e.func_4112_a(temperature, i, j, k, k, 0.02500000037252903D, 0.02500000037252903D, 0.25D);
        humidity = field_4193_f.func_4112_a(humidity, i, j, k, k, 0.05000000074505806D, 0.05000000074505806D, 0.33333333333333331D);
        field_4196_c = field_4192_g.func_4112_a(field_4196_c, i, j, k, k, 0.25D, 0.25D, 0.58823529411764708D);
        int i1 = 0;
        for (int j1 = 0; j1 < k; j1++) {
            for (int k1 = 0; k1 < l; k1++) {
                double d = field_4196_c[i1] * 1.1000000000000001D + 0.5D;
                double d1 = 0.01D;
                double d2 = 1.0D - d1;
                double d3 = (temperature[i1] * 0.14999999999999999D + 0.69999999999999996D) * d2 + d * d1;
                d1 = 0.002D;
                d2 = 1.0D - d1;
                double d4 = (humidity[i1] * 0.14999999999999999D + 0.5D) * d2 + d * d1;
                d3 = 1.0D - (1.0D - d3) * (1.0D - d3);
                if (d3 < 0.0D) {
                    d3 = 0.0D;
                }
                if (d4 < 0.0D) {
                    d4 = 0.0D;
                }
                if (d3 > 1.0D) {
                    d3 = 1.0D;
                }
                if (d4 > 1.0D) {
                    d4 = 1.0D;
                }
                temperature[i1] = d3;
                humidity[i1] = d4;
                abiomegenbase[i1++] = OldBiomeGenBase.getBiomeFromLookup(d3, d4);
            }

        }

        return abiomegenbase;
    }

}
