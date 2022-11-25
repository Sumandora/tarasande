package net.tarasandedevelopment.tarasande.injection.mixin.core.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LivingEntity.class, priority = 999 /* baritone fix */)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    public int bodyTrackingIncrements;

    @Shadow
    protected double serverYaw;

    @Shadow
    protected double serverPitch;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyTrackingIncrements:I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateTrackedPosition(DDD)V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setYaw(F)V")))
    public void preventRotationLeak(CallbackInfo ci) {
        if (this.bodyTrackingIncrements > 0 && (Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            Rotation rotation = RotationUtil.INSTANCE.getFakeRotation();
            RotationUtil.INSTANCE.setFakeRotation(new Rotation(
                    (rotation.getYaw() + (float) MathHelper.wrapDegrees(this.serverYaw - (double) rotation.getYaw()) / (float) this.bodyTrackingIncrements) % 360.0F,
                    (rotation.getPitch() + (float) (this.serverPitch - (double) rotation.getPitch()) / (float) this.bodyTrackingIncrements) % 360.0F
            ));
        }
    }
}
