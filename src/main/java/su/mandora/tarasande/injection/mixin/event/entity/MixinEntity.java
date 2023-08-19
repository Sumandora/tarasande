package su.mandora.tarasande.injection.mixin.event.entity;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.*;
import su.mandora.tarasande.injection.accessor.IEntity;
import su.mandora.tarasande.util.extension.minecraft.math.Vec3dKt;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Unique
    private boolean tarasande_forceFlagRetrieval = false;

    @Shadow
    public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Shadow
    protected abstract boolean getFlag(int index);

    @Shadow
    public abstract void setVelocity(Vec3d velocity);

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookEventVelocityYaw(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(yaw);
            EventDispatcher.INSTANCE.call(eventVelocityYaw);
            yaw = eventVelocityYaw.getYaw();
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getStepHeight()F"))
    public float hookEventStepPre(Entity instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep(instance.getStepHeight(), EventStep.State.PRE);
            EventDispatcher.INSTANCE.call(eventStep);
            return eventStep.getStepHeight();
        }

        return instance.getStepHeight();
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    public void hookEventStepPost(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep((float) cir.getReturnValue().y, EventStep.State.POST);
            EventDispatcher.INSTANCE.call(eventStep);
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void hookEventMovement(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        EventMovement eventMovement = new EventMovement((Entity) (Object) this, Vec3dKt.copy(movement));
        EventDispatcher.INSTANCE.call(eventMovement);
        if (eventMovement.getDirty()) {
            movement.x = eventMovement.getVelocity().x;
            movement.y = eventMovement.getVelocity().y;
            movement.z = eventMovement.getVelocity().z;
        }
    }

    @Inject(method = "getFlag", at = @At("RETURN"), cancellable = true)
    public void hookEventEntityFlag(int index, CallbackInfoReturnable<Boolean> cir) {
        if (tarasande_forceFlagRetrieval) {
            tarasande_forceFlagRetrieval = false;
            return;
        }
        EventEntityFlag eventEntityFlag = new EventEntityFlag((Entity) (Object) this, index, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventEntityFlag);
        cir.setReturnValue(eventEntityFlag.getEnabled());
    }

    @Override
    public boolean tarasande_forceGetFlag(int index) {
        tarasande_forceFlagRetrieval = true;
        return getFlag(index);
    }

    @Redirect(method = "getVelocityMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getVelocityMultiplier()F"))
    public float hookEventVelocityMultiplier(Block instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(instance, instance.getVelocityMultiplier());
            EventDispatcher.INSTANCE.call(eventVelocityMultiplier);
            if (eventVelocityMultiplier.getDirty())
                return (float) eventVelocityMultiplier.getVelocityMultiplier();
        }
        return instance.getVelocityMultiplier();
    }
}
