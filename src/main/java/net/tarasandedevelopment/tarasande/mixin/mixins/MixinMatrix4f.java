package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.util.math.Matrix4f;
import net.tarasandedevelopment.tarasande.mixin.accessor.IMatrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public class MixinMatrix4f implements IMatrix4f {

    @Shadow
    protected float a00;
    @Shadow
    protected float a01;
    @Shadow
    protected float a02;
    @Shadow
    protected float a03;
    @Shadow
    protected float a10;
    @Shadow
    protected float a11;
    @Shadow
    protected float a12;
    @Shadow
    protected float a13;
    @Shadow
    protected float a20;
    @Shadow
    protected float a21;
    @Shadow
    protected float a22;
    @Shadow
    protected float a23;
    @Shadow
    protected float a30;
    @Shadow
    protected float a31;
    @Shadow
    protected float a32;
    @Shadow
    protected float a33;

    @Override
    public float tarasande_getA00() {
        return a00;
    }

    @Override
    public void tarasande_setA00(float a00) {
        this.a00 = a00;
    }

    @Override
    public float tarasande_getA01() {
        return a01;
    }

    @Override
    public void tarasande_setA01(float a01) {
        this.a01 = a01;
    }

    @Override
    public float tarasande_getA02() {
        return a02;
    }

    @Override
    public void tarasande_setA02(float a02) {
        this.a02 = a02;
    }

    @Override
    public float tarasande_getA03() {
        return a03;
    }

    @Override
    public void tarasande_setA03(float a03) {
        this.a03 = a03;
    }

    @Override
    public float tarasande_getA10() {
        return a10;
    }

    @Override
    public void tarasande_setA10(float a10) {
        this.a10 = a10;
    }

    @Override
    public float tarasande_getA11() {
        return a11;
    }

    @Override
    public void tarasande_setA11(float a11) {
        this.a11 = a11;
    }

    @Override
    public float tarasande_getA12() {
        return a12;
    }

    @Override
    public void tarasande_setA12(float a12) {
        this.a12 = a12;
    }

    @Override
    public float tarasande_getA13() {
        return a13;
    }

    @Override
    public void tarasande_setA13(float a13) {
        this.a13 = a13;
    }

    @Override
    public float tarasande_getA20() {
        return a20;
    }

    @Override
    public void tarasande_setA20(float a20) {
        this.a20 = a20;
    }

    @Override
    public float tarasande_getA21() {
        return a21;
    }

    @Override
    public void tarasande_setA21(float a21) {
        this.a21 = a21;
    }

    @Override
    public float tarasande_getA22() {
        return a22;
    }

    @Override
    public void tarasande_setA22(float a22) {
        this.a22 = a22;
    }

    @Override
    public float tarasande_getA23() {
        return a23;
    }

    @Override
    public void tarasande_setA23(float a23) {
        this.a23 = a23;
    }

    @Override
    public float tarasande_getA30() {
        return a30;
    }

    @Override
    public void tarasande_setA30(float a30) {
        this.a30 = a30;
    }

    @Override
    public float tarasande_getA31() {
        return a31;
    }

    @Override
    public void tarasande_setA31(float a31) {
        this.a31 = a31;
    }

    @Override
    public float tarasande_getA32() {
        return a32;
    }

    @Override
    public void tarasande_setA32(float a32) {
        this.a32 = a32;
    }

    @Override
    public float tarasande_getA33() {
        return a33;
    }

    @Override
    public void tarasande_setA33(float a33) {
        this.a33 = a33;
    }
}
