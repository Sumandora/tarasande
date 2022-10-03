package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Vec3d.class)
public class MixinVec3d implements IVec3d {
    @Mutable
    @Shadow
    @Final
    public double x;

    @Mutable
    @Shadow
    @Final
    public double y;

    @Mutable
    @Shadow
    @Final
    public double z;

    @Override
    public void tarasande_setX(double x) {
        this.x = x;
    }

    @Override
    public void tarasande_setY(double y) {
        this.y = y;
    }

    @Override
    public void tarasande_setZ(double z) {
        this.z = z;
    }

    @Override
    public void tarasande_copy(Vec3d other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
}
