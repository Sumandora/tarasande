package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventEntityFlag;
import net.tarasandedevelopment.tarasande.event.EventMovement;
import net.tarasandedevelopment.tarasande.event.EventStep;
import net.tarasandedevelopment.tarasande.event.EventVelocityYaw;
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Unique
    private boolean tarasande_forceFlagRetrieval = false;

    @Shadow
    public static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookEventVelocityYaw(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(yaw);
            EventDispatcher.INSTANCE.call(eventVelocityYaw);
            yaw = eventVelocityYaw.getYaw();
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void hookEventMovement(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        EventMovement eventMovement = new EventMovement((Entity) (Object) this, movement);
        EventDispatcher.INSTANCE.call(eventMovement);
        movement.x = eventMovement.getVelocity().x;
        movement.y = eventMovement.getVelocity().y;
        movement.z = eventMovement.getVelocity().z;
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F"))
    public float hookEventStepPre(Entity instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep(instance.stepHeight, EventStep.State.PRE);
            EventDispatcher.INSTANCE.call(eventStep);
            return eventStep.getStepHeight();
        }

        return instance.stepHeight;
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    public void hookEventStepPost(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep((float) cir.getReturnValue().y, EventStep.State.POST);
            EventDispatcher.INSTANCE.call(eventStep);
        }
    }

    @Shadow
    protected abstract boolean getFlag(int index);

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
}
