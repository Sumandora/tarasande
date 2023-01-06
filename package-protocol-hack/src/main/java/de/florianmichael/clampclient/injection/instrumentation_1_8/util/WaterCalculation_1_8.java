package de.florianmichael.clampclient.injection.instrumentation_1_8.util;

public class WaterCalculation_1_8 {

    public static float getLiquidHeightPercent(int meta) {
        if (meta >= 8) meta = 0;

        return (float)(meta + 1) / 9.0F;
    }
}
