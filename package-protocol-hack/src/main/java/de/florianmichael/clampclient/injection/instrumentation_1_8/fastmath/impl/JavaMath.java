package de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.impl;

import de.florianmichael.clampclient.injection.instrumentation_1_8.fastmath.Math;

public class JavaMath implements Math {
    @Override
    public float sin(float x) {
        return (float) java.lang.Math.sin(x);
    }

    @Override
    public float cos(float x) {
        return (float) java.lang.Math.cos(x);
    }
}
