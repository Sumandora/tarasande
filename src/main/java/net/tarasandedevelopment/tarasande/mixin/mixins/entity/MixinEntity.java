package net.tarasandedevelopment.tarasande.mixin.mixins.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventEntityFlag;
import net.tarasandedevelopment.tarasande.event.EventMovement;
import net.tarasandedevelopment.tarasande.event.EventStep;
import net.tarasandedevelopment.tarasande.event.EventVelocityYaw;
import net.tarasandedevelopment.tarasande.mixin.accessor.IEntity;
import net.tarasandedevelopment.tarasande.mixin.accessor.IVec3d;
import net.tarasandedevelopment.tarasande.module.render.ModuleESP;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {

    @Shadow
    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        return null;
    }

    @Shadow
    protected abstract Vec3d getRotationVector(float pitch, float yaw);

    @Unique
    private boolean forceFlagRetrieval = false;

    @Inject(method = "getRotationVec", at = @At("HEAD"), cancellable = true)
    public void injectGetRotationVec(float tickDelta, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null) {
            cir.setReturnValue(this.getRotationVector(RotationUtil.INSTANCE.getFakeRotation().getPitch(), RotationUtil.INSTANCE.getFakeRotation().getYaw()));
        }
    }

    @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;movementInputToVelocity(Lnet/minecraft/util/math/Vec3d;FF)Lnet/minecraft/util/math/Vec3d;"))
    public Vec3d hookedMovementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(yaw);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventVelocityYaw);
            yaw = eventVelocityYaw.getYaw();
        }
        return movementInputToVelocity(movementInput, speed, yaw);
    }

    @Inject(method = "move", at = @At("HEAD"))
    public void injectMove(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        EventMovement eventMovement = new EventMovement((Entity) (Object) this, movement);
        TarasandeMain.Companion.get().getEventDispatcher().call(eventMovement);
        ((IVec3d) movement).tarasande_copy(eventMovement.getVelocity());
    }

    @Inject(method = "getTeamColorValue", at = @At("RETURN"), cancellable = true)
    public void injectGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleESP moduleESP = TarasandeMain.Companion.get().getManagerModule().get(ModuleESP.class);
            if(moduleESP.getEnabled()) {
                Color c = moduleESP.getEntityColor().getColor((Entity) (Object) this);
                if(c != null)
                    cir.setReturnValue(c.getRGB());
            }
        }
    }

    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/Entity;stepHeight:F"))
    public float hookedStepHeight(Entity instance) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep(instance.stepHeight, EventStep.State.PRE);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventStep);
            return eventStep.getStepHeight();
        }

        return instance.stepHeight;
    }

    @Inject(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;", at = @At("RETURN"))
    public void injectPostAdjustMovementForCollisions(Vec3d movement, CallbackInfoReturnable<Vec3d> cir) {
        if ((Object) this == MinecraftClient.getInstance().player) {
            EventStep eventStep = new EventStep((float) cir.getReturnValue().y, EventStep.State.POST);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventStep);
        }
    }

    @Shadow
    protected abstract boolean getFlag(int index);

    @Inject(method = "getFlag", at = @At("RETURN"), cancellable = true)
    public void injectGetFlag(int index, CallbackInfoReturnable<Boolean> cir) {
        if (forceFlagRetrieval) {
            forceFlagRetrieval = false;
            return;
        }
        EventEntityFlag eventEntityFlag = new EventEntityFlag((Entity) (Object) this, index, cir.getReturnValue());
        TarasandeMain.Companion.get().getEventDispatcher().call(eventEntityFlag);
        cir.setReturnValue(eventEntityFlag.getEnabled());
    }

    @Override
    public boolean tarasande_forceGetFlag(int index) {
        forceFlagRetrieval = true;
        return getFlag(index);
    }
}
