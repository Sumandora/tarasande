/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 7/13/22, 3:47 AM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.clampclient.injection.instrumentation_1_12_2.mouse;

import java.util.Arrays;

public class SensitivityCalculations {

    private static final float[] sensitivities1_12 = new float[201];

    static  {
        float sliderValue;
        int xPosition = 4;
        int width = 150;
        Arrays.fill(sensitivities1_12, Float.NaN);
        for (int mouseX = -4; mouseX <= xPosition + width + 4; mouseX++) {
            sliderValue = (float)(mouseX - (xPosition + 4)) / (float)(width - 8);
            if (sliderValue < 0 || sliderValue > 1.0F) {
                continue;
            }
            sensitivities1_12[(int)(sliderValue * 200.0F)] = sliderValue;
        }
    }

    public static float get1_12SensitivityFor1_19(double value) {
        double biggestDelta = Double.MAX_VALUE;
        float bestSensitivity = 0F;
        for (float f : sensitivities1_12) {
            if (!Float.isFinite(f)) continue;
            double absoluteDelta = Math.abs(value - f);
            if (absoluteDelta < biggestDelta) {
                biggestDelta = absoluteDelta;
                bestSensitivity = f;
            }
        }
        return bestSensitivity;
    }

    public static int getPercentage(float value) {
        return (int) (value * 200.0F);
    }
}
