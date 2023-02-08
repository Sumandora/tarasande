package net.tarasandedevelopment.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations;
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity implements ILivingEntity {

    @Shadow
    public int bodyTrackingIncrements;

    @Shadow
    public double serverX;

    @Shadow
    public double serverY;

    @Shadow
    public double serverZ;

    @Shadow
    public double serverYaw;

    @Shadow
    protected double serverPitch;

    @Unique
    private Vec3d tarasande_oldServerPos;

    @Unique
    private float tarasande_headPitch;
    @Unique
    private float tarasande_prevHeadPitch;

    @Unique
    private float tarasande_headYaw;
    @Unique
    private float tarasande_prevHeadYaw;

    public MixinLivingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow protected double serverHeadYaw;

    @Shadow protected int headTrackingIncrements;

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;bodyTrackingIncrements:I"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updateTrackedPosition(DDD)V"), to = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;serverX:D")))
    public void leakRotations(CallbackInfo ci) {
        Rotation rotation = Rotations.INSTANCE.getFakeRotation();
        //noinspection ConstantValue
        if (this.bodyTrackingIncrements > 0 && (Object) this == MinecraftClient.getInstance().player && rotation != null) {
            Rotations.INSTANCE.setFakeRotation(new Rotation(
                    (rotation.getYaw() + (float) MathHelper.wrapDegrees(this.serverYaw - (double) rotation.getYaw()) / (float) this.bodyTrackingIncrements) % 360.0F,
                    (rotation.getPitch() + (float) (this.serverPitch - (double) rotation.getPitch()) / (float) this.bodyTrackingIncrements) % 360.0F
            ));
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"), slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;prevStepBobbingAmount:F"), to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;turnHead(FF)F")))
    public float replaceYaw_tick(LivingEntity instance) {
        Rotation rotation = Rotations.INSTANCE.getFakeRotation();
        //noinspection ConstantValue
        if((Object) this == MinecraftClient.getInstance().player && rotation != null) {
            return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Redirect(method = "turnHead", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getYaw()F"))
    public float replaceYaw_turnHead(LivingEntity instance) {
        Rotation rotation = Rotations.INSTANCE.getFakeRotation();
        //noinspection ConstantValue
        if((Object) this == MinecraftClient.getInstance().player && rotation != null) {
            return rotation.getYaw();
        }
        return instance.getYaw();
    }

    @Inject(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headTrackingIncrements:I", ordinal = 2))
    public void respectServerHeadYaw(CallbackInfo ci) {
        tarasande_headYaw += (float)MathHelper.wrapDegrees(this.serverHeadYaw - (double)this.tarasande_headYaw) / (float)this.headTrackingIncrements;
    }

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F"))
    public void setHeadRotation(EntityType entityType, World world, CallbackInfo ci) {
        tarasande_headYaw = getYaw();
        tarasande_headPitch = getPitch();
    }

    @Inject(method = "onSpawnPacket", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F", ordinal = 1))
    public void setHeadRotation(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        tarasande_headYaw = packet.getHeadYaw();
        tarasande_prevHeadYaw = tarasande_headYaw;

        tarasande_headPitch = packet.getPitch();
        tarasande_prevHeadPitch = tarasande_headPitch;
    }

    @Inject(method = "baseTick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;prevHeadYaw:F"))
    public void updateHeadRotation(CallbackInfo ci) {
        tarasande_prevHeadYaw = tarasande_headYaw;
        tarasande_prevHeadPitch = tarasande_headPitch;
    }

    @Inject(method = "updateTrackedPositionAndAngles", at = @At("HEAD"))
    public void saveOldServerPos(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci) {
        tarasande_oldServerPos = new Vec3d(serverX, serverY, serverZ);
    }

    @Override
    public Vec3d tarasande_prevServerPos() {
        return tarasande_oldServerPos;
    }

    @Override
    public float tarasande_getHeadPitch() {
        return tarasande_headPitch;
    }

    @Override
    public void tarasande_setHeadPitch(float headPitch) {
        tarasande_headPitch = headPitch;
    }

    @Override
    public float tarasande_getPrevHeadPitch() {
        return tarasande_prevHeadPitch;
    }

    @Override
    public void tarasande_setPrevHeadPitch(float prevHeadPitch) {
        tarasande_prevHeadPitch = prevHeadPitch;
    }

    @Override
    public float tarasande_getHeadYaw() {
        return tarasande_headYaw;
    }

    @Override
    public void tarasande_setHeadYaw(float headYaw) {
        tarasande_headYaw = headYaw;
    }

    @Override
    public float tarasande_getPrevHeadYaw() {
        return tarasande_prevHeadYaw;
    }

    @Override
    public void tarasande_setPrevHeadYaw(float prevHeadYaw) {
        tarasande_prevHeadYaw = prevHeadYaw;
    }
}
