package de.florianmichael.clampclient.injection.instrumentation_1_8.definition;

public class LegacyConstants_1_8 {

    public static final float PLAYER_MODEL_WIDTH = 0.6F;
    public static final float PLAYER_MODEL_HEIGHT = 1.8F;

    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) meta = 0;

        return (float)(meta + 1) / 9.0F;
    }
}
