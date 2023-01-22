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

public class MouseFilter_1_12_2 {

    private float field_76336_a;
    private float field_76334_b;
    private float field_76335_c;

    public float smooth(float p_76333_1_, float p_76333_2_) {
        this.field_76336_a += p_76333_1_;
        p_76333_1_ = (this.field_76336_a - this.field_76334_b) * p_76333_2_;
        this.field_76335_c += (p_76333_1_ - this.field_76335_c) * 0.5F;

        if (p_76333_1_ > 0.0F && p_76333_1_ > this.field_76335_c || p_76333_1_ < 0.0F && p_76333_1_ < this.field_76335_c) {
            p_76333_1_ = this.field_76335_c;
        }

        this.field_76334_b += p_76333_1_;
        return p_76333_1_;
    }

    public void reset() {
        this.field_76336_a = 0.0F;
        this.field_76334_b = 0.0F;
        this.field_76335_c = 0.0F;
    }
}
