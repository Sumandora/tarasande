package de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.Math;

public class NoneMath implements Math {

    private static final float[] SIN_TABLE = new float[65536];

    static {
        for (int i = 0; i < 65536; ++i) {
            SIN_TABLE[i] = (float) java.lang.Math.sin((double) i * java.lang.Math.PI * 2.0D / 65536.0D);
        }
    }

    @Override
    public float sin(float p_76126_0_) {
        return SIN_TABLE[(int) (p_76126_0_ * 10430.378F) & 65535];
    }

    @Override
    public float cos(float value) {
        return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
    }
}
