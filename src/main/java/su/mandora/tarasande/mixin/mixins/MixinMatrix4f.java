package su.mandora.tarasande.mixin.mixins;

import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IMatrix4f;

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
    public float getA00() {
        return a00;
    }

    @Override
    public void setA00(float a00) {
        this.a00 = a00;
    }

    @Override
    public float getA01() {
        return a01;
    }

    @Override
    public void setA01(float a01) {
        this.a01 = a01;
    }

    @Override
    public float getA02() {
        return a02;
    }

    @Override
    public void setA02(float a02) {
        this.a02 = a02;
    }

    @Override
    public float getA03() {
        return a03;
    }

    @Override
    public void setA03(float a03) {
        this.a03 = a03;
    }

    @Override
    public float getA10() {
        return a10;
    }

    @Override
    public void setA10(float a10) {
        this.a10 = a10;
    }

    @Override
    public float getA11() {
        return a11;
    }

    @Override
    public void setA11(float a11) {
        this.a11 = a11;
    }

    @Override
    public float getA12() {
        return a12;
    }

    @Override
    public void setA12(float a12) {
        this.a12 = a12;
    }

    @Override
    public float getA13() {
        return a13;
    }

    @Override
    public void setA13(float a13) {
        this.a13 = a13;
    }

    @Override
    public float getA20() {
        return a20;
    }

    @Override
    public void setA20(float a20) {
        this.a20 = a20;
    }

    @Override
    public float getA21() {
        return a21;
    }

    @Override
    public void setA21(float a21) {
        this.a21 = a21;
    }

    @Override
    public float getA22() {
        return a22;
    }

    @Override
    public void setA22(float a22) {
        this.a22 = a22;
    }

    @Override
    public float getA23() {
        return a23;
    }

    @Override
    public void setA23(float a23) {
        this.a23 = a23;
    }

    @Override
    public float getA30() {
        return a30;
    }

    @Override
    public void setA30(float a30) {
        this.a30 = a30;
    }

    @Override
    public float getA31() {
        return a31;
    }

    @Override
    public void setA31(float a31) {
        this.a31 = a31;
    }

    @Override
    public float getA32() {
        return a32;
    }

    @Override
    public void setA32(float a32) {
        this.a32 = a32;
    }

    @Override
    public float getA33() {
        return a33;
    }

    @Override
    public void setA33(float a33) {
        this.a33 = a33;
    }
}
