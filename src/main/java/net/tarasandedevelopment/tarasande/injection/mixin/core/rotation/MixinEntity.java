package net.tarasandedevelopment.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Shadow public abstract float getPitch();

    @Inject(method = "getRotationVec", at = @At("HEAD"), cancellable = true)
    public void injectFakeRotation(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        //noinspection ConstantValue
        if ((Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            cir.setReturnValue(this.getRotationVector(RotationUtil.INSTANCE.getFakeRotation().getPitch(), RotationUtil.INSTANCE.getFakeRotation().getYaw()));
        }
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setBodyYaw(F)V"))
    public void updateHeadYaw(NbtCompound nbt, CallbackInfo ci) {
        //noinspection ConstantValue
        if((Object) this instanceof LivingEntity) {
            ILivingEntity accessor = (ILivingEntity) this;
            accessor.tarasande_setHeadPitch(getPitch());
        }
    }

}
