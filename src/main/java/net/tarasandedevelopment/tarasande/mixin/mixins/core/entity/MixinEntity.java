package net.tarasandedevelopment.tarasande.mixin.mixins.core.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Inject(method = "getRotationVec", at = @At("HEAD"), cancellable = true)
    public void injectFakeRotation(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            cir.setReturnValue(this.getRotationVector(RotationUtil.INSTANCE.getFakeRotation().getPitch(), RotationUtil.INSTANCE.getFakeRotation().getYaw()));
        }
    }
}
