package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.beta;

import java.util.Random;

public class NoiseGeneratorOctaves2 {

    public NoiseGeneratorOctaves2(Random random, int i) {
        field_4233_b = i;
        field_4234_a = new NoiseGenerator2[i];
        for (int j = 0; j < i; j++) {
            field_4234_a[j] = new NoiseGenerator2(random);
        }

    }

    public double[] func_4112_a(double ad[], double d, double d1, int i, int j,
                                double d2, double d3, double d4) {
        return func_4111_a(ad, d, d1, i, j, d2, d3, d4, 0.5D);
    }

    public double[] func_4111_a(double ad[], double d, double d1, int i, int j,
                                double d2, double d3, double d4, double d5) {
        d2 /= 1.5D;
        d3 /= 1.5D;
        if (ad == null || ad.length < i * j) {
            ad = new double[i * j];
        } else {
            for (int k = 0; k < ad.length; k++) {
                ad[k] = 0.0D;
            }

        }
        double d6 = 1.0D;
        double d7 = 1.0D;
        for (int l = 0; l < field_4233_b; l++) {
            field_4234_a[l].func_4157_a(ad, d, d1, i, j, d2 * d7, d3 * d7, 0.55000000000000004D / d6);
            d7 *= d4;
            d6 *= d5;
        }

        return ad;
    }

    private final NoiseGenerator2[] field_4234_a;
    private final int field_4233_b;

}
