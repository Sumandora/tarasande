package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.beta;

public class OldBiomeGenBase {

    private static final OldBiomeGenBase[] biomeLookupTable = new OldBiomeGenBase[4096];

    public static final OldBiomeGenBase rainforest = new OldBiomeGenBase();
    public static final OldBiomeGenBase swampland = new OldBiomeGenBase();
    public static final OldBiomeGenBase seasonalForest = new OldBiomeGenBase();
    public static final OldBiomeGenBase forest = new OldBiomeGenBase();
    public static final OldBiomeGenBase savanna = new OldBiomeGenBase();
    public static final OldBiomeGenBase shrubland = new OldBiomeGenBase();
    public static final OldBiomeGenBase taiga = new OldBiomeGenBase();
    public static final OldBiomeGenBase desert = new OldBiomeGenBase();
    public static final OldBiomeGenBase plains = new OldBiomeGenBase();
    public static final OldBiomeGenBase iceDesert = new OldBiomeGenBase();
    public static final OldBiomeGenBase tundra = new OldBiomeGenBase();
    public static final OldBiomeGenBase hell = new OldBiomeGenBase();
    public static final OldBiomeGenBase sky = new OldBiomeGenBase();

    static {
        generateBiomeLookup();
    }

    protected OldBiomeGenBase() {
    }

    public static void generateBiomeLookup() {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                biomeLookupTable[i + j * 64] = getBiome((float) i / 63F, (float) j / 63F);
            }

        }
    }

    public static OldBiomeGenBase getBiomeFromLookup(double d, double d1) {
        int i = (int) (d * 63D);
        int j = (int) (d1 * 63D);
        return biomeLookupTable[i + j * 64];
    }

    public static OldBiomeGenBase getBiome(float f, float f1) {
        f1 *= f;
        if (f < 0.1F) {
            return tundra;
        }
        if (f1 < 0.2F) {
            if (f < 0.5F) {
                return tundra;
            }
            if (f < 0.95F) {
                return savanna;
            } else {
                return desert;
            }
        }
        if (f1 > 0.5F && f < 0.7F) {
            return swampland;
        }
        if (f < 0.5F) {
            return taiga;
        }
        if (f < 0.97F) {
            if (f1 < 0.35F) {
                return shrubland;
            } else {
                return forest;
            }
        }
        if (f1 < 0.45F) {
            return plains;
        }
        if (f1 < 0.9F) {
            return seasonalForest;
        } else {
            return rainforest;
        }
    }

    public int getSkyColorByTemp(float f) {
        f /= 3F;
        if (f < -1F) {
            f = -1F;
        }
        if (f > 1.0F) {
            f = 1.0F;
        }
        return java.awt.Color.getHSBColor(0.6222222F - f * 0.05F, 0.5F + f * 0.1F, 1.0F).getRGB();
    }

}
