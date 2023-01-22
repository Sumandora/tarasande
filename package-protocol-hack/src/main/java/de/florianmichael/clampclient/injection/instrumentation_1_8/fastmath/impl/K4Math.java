package de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.Math;

public class K4Math implements Math {


    private static final float radToIndex = roundToFloat(651.8986469044033D);
    private static final float[] SIN_TABLE_FAST = new float[4096];

    static {
        for (int j = 0; j < SIN_TABLE_FAST.length; ++j)
        {
            SIN_TABLE_FAST[j] = roundToFloat(java.lang.Math.sin((double)j * java.lang.Math.PI * 2.0D / 4096.0D));
        }
    }

    public static float roundToFloat(double d)
    {
        return (float)((double) java.lang.Math.round(d * 1.0E8D) / 1.0E8D);
    }

    @Override
    public float sin(float x) {
        return SIN_TABLE_FAST[(int)(x * radToIndex) & 4095];
    }

    @Override
    public float cos(float x) {
        return SIN_TABLE_FAST[(int)(x * radToIndex + 1024.0F) & 4095];
    }
}
