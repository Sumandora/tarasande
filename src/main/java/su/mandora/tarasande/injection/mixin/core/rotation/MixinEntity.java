package su.mandora.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.feature.rotation.Rotations;
import su.mandora.tarasande.injection.accessor.ILivingEntity;
import su.mandora.tarasande.feature.rotation.api.Rotation;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Shadow
    public abstract float getPitch();

    @Shadow
    public abstract float getYaw();

    @Inject(method = "getRotationVec", at = @At("HEAD"), cancellable = true)
    public void injectFakeRotation(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        Rotation fakeRotation = Rotations.INSTANCE.getFakeRotation();
        //noinspection ConstantValue
        if ((Object) this == MinecraftClient.getInstance().player && fakeRotation != null) {
            cir.setReturnValue(this.getRotationVector(fakeRotation.getPitch(), fakeRotation.getYaw()));
        }
    }

    @Inject(method = "readNbt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setBodyYaw(F)V"))
    public void updateHeadRotation(NbtCompound nbt, CallbackInfo ci) {
        //noinspection ConstantValue
        if ((Object) this instanceof LivingEntity) {
            ILivingEntity accessor = (ILivingEntity) this;
            accessor.tarasande_setHeadYaw(getYaw());
            accessor.tarasande_setHeadPitch(getPitch());
        }
    }

}
