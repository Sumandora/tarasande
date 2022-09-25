package de.enzaxd.viaforge.injection.mixin.entity;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import de.enzaxd.viaforge.injection.access.IClientPlayerEntity_Protocol;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ClientPlayerEntity.class, priority = 2000)
public class ClientPlayerEntityMixin implements IClientPlayerEntity_Protocol {

    @Shadow private boolean lastOnGround;

    @Shadow public Input input;

    @Unique
    private boolean areSwingCanceledThisTick = false;

    @Redirect(method = "sendMovementPackets", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerEntity;lastOnGround:Z", opcode = Opcodes.GETFIELD))
    public boolean redirectSendMovementPackets(ClientPlayerEntity player) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8))
            return !this.lastOnGround; // make sure player packets are sent every tick to tick the server-side player entity
        else
            return this.lastOnGround;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;square(D)D"))
    public double redirectSendMovementPackets_2(double n) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8))
            n = 9.0E-4D;
        return MathHelper.square(n);
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8) && areSwingCanceledThisTick)
            ci.cancel();
        areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_14_4))
            if (this.input.sneaking) {
                this.input.movementSideways = (float)((double)this.input.movementSideways / 0.3D);
                this.input.movementForward = (float)((double)this.input.movementForward / 0.3D);
            }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_14_1))
            return false;
        else
            return _this.isSwimming();
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void easierUnderwaterSprinting(CallbackInfoReturnable<Boolean> ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_14_1))
            ci.setReturnValue(input.movementForward >= 0.8);
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_14_1))
            return input.movementForward >= 0.8F;
        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_12_2))
            return false; // disable all water related movement
        return self.isTouchingWater();
    }

    @Override
    public void florianMichael_cancelSwingOnce() {
        areSwingCanceledThisTick = true;
    }
}
