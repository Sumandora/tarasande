package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class JavaMathTable implements MathTable {
    @Override
    public float sin(float x) {
        return (float) Math.sin(x);
    }

    @Override
    public float cos(float x) {
        return (float) Math.cos(x);
    }
}
