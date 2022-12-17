package net.tarasandedevelopment.tarasande.injection.mixin.event.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.event.*;
import net.tarasandedevelopment.tarasande.injection.accessor.IClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.event.EventDispatcher;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V"), cancellable = true)
    public void hookEventUpdatePre(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        EventUpdate eventUpdate = new EventUpdate(EventUpdate.State.PRE);
        EventDispatcher.INSTANCE.call(eventUpdate);
        if (eventUpdate.getCancelled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    public void hookEventPacketPrePacket(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        EventDispatcher.INSTANCE.call(new EventUpdate(EventUpdate.State.PRE_PACKET));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void hookEventUpdatePost(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        EventDispatcher.INSTANCE.call(new EventUpdate(EventUpdate.State.POST));
    }

    @Inject(method = "canSprint", at = @At("RETURN"), cancellable = true)
    public void hookEventCanSprint(CallbackInfoReturnable<Boolean> cir) {
        EventCanSprint eventCanSprint = new EventCanSprint(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventCanSprint);
        cir.setReturnValue(eventCanSprint.getCanSprint());
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        EventMovement eventMovement = new EventMovement(movement);
        EventDispatcher.INSTANCE.call(eventMovement);
        setVelocity(eventMovement.getVelocity());
        super.move(movementType, eventMovement.getVelocity());
    }

    @Override
    public float getFovMultiplier() {
        EventMovementFovMultiplier eventMovementFovMultiplier = new EventMovementFovMultiplier(super.getFovMultiplier());
        EventDispatcher.INSTANCE.call(eventMovementFovMultiplier);
        return eventMovementFovMultiplier.getMovementFovMultiplier();
    }

    @Override
    public void jump() {
        float originalYaw;
        EventJump eventJump = new EventJump(originalYaw = getYaw(), EventJump.State.PRE);
        EventDispatcher.INSTANCE.call(eventJump);

        setYaw(eventJump.getYaw());

        if (eventJump.getCancelled())
            return;

        super.jump();

        eventJump = new EventJump(originalYaw, EventJump.State.POST);
        EventDispatcher.INSTANCE.call(eventJump);

        setYaw(eventJump.getYaw());
    }

    @Override
    public void swingHand(Hand hand) {
        EventSwing eventSwing = new EventSwing(hand);
        EventDispatcher.INSTANCE.call(eventSwing);
        if (eventSwing.getCancelled())
            return;
        super.swingHand(hand);
    }

    @Override
    public void updateVelocity(float speed, Vec3d movementInput) {
        float originalYaw;
        EventVelocityYaw eventVelocityYaw = new EventVelocityYaw(originalYaw = getYaw());
        EventDispatcher.INSTANCE.call(eventVelocityYaw);
        setYaw(eventVelocityYaw.getYaw());
        super.updateVelocity(speed, movementInput);
        setYaw(originalYaw);
    }
}
