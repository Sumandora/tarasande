package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class TaylorMathTable implements MathTable {

    private static final float BF_SIN_TO_COS;
    static {
        BF_SIN_TO_COS = (float)(Math.PI * 0.5f);
    }

    @Override
    public float sin(float x) {
        double xV = x;

        double x2 = xV * xV;
        double x3 = x2 * xV;
        double x5 = x2 * x3;
        double x7 = x2 * x5;
        double x9 = x2 * x7;
        double x11 = x2 * x9;
        double x13 = x2 * x11;
        double x15 = x2 * x13;
        double x17 = x2 * x15;

        double val = xV;
        val -= x3 * 0.16666666666666666666666666666667;
        val += x5 * 0.00833333333333333333333333333333;
        val -= x7 * 1.984126984126984126984126984127e-4;
        val += x9 * 2.7557319223985890652557319223986e-6;
        val -= x11 * 2.5052108385441718775052108385442e-8;
        val += x13 * 1.6059043836821614599392377170155e-10;
        val -= x15 * 7.6471637318198164759011319857881e-13;
        val += x17 * 2.8114572543455207631989455830103e-15;
        return (float) val;
    }

    @Override
    public float cos(float x) {
        return sin(x + BF_SIN_TO_COS);
    }
}
