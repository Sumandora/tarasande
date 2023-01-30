package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class RandomMathTable implements MathTable {

    @Override
    public float sin(float x) {
        return (float) Math.random() * x;
    }

    @Override
    public float cos(float x) {
        return (float) Math.random() * x;
    }
}
