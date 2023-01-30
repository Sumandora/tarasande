package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class OptifineD5MathTable implements MathTable {

    private static final float[] SIN_TABLE_FAST = new float[4096];

    static {
        int i;
        for (i = 0; i < 4096; ++i) {
            SIN_TABLE_FAST[i] = (float) Math.sin((double) (((float) i + 0.5F) / 4096.0F * ((float) Math.PI * 2F)));
        }
        for (i = 0; i < 360; i += 90) {
            SIN_TABLE_FAST[(int) ((float) i * 11.377778F) & 4095] = (float) Math.sin((double) ((float) i * 0.017453292F));
        }
    }

    @Override
    public float sin(float x) {
        return SIN_TABLE_FAST[(int) (x * 651.8986F) & 4095];
    }

    @Override
    public float cos(float x) {
        return SIN_TABLE_FAST[(int) ((x + ((float) Math.PI / 2F)) * 651.8986F) & 4095];
    }
}
