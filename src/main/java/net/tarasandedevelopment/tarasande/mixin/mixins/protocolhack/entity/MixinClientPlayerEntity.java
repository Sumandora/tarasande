package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.entity;

import com.mojang.authlib.GameProfile;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventSkipIdlePacket;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientPlayerEntity_Protocol;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity_Protocol {

    @Shadow private boolean lastOnGround;

    @Shadow
    public Input input;

    @Shadow
    private int ticksSinceLastPositionPacketSent;

    @Shadow
    protected abstract boolean isCamera();

    @Shadow
    private double lastX;
    @Shadow
    private double lastBaseY;
    @Shadow
    private double lastZ;
    @Shadow
    public float lastYaw;
    @Shadow
    public float lastPitch;
    @Shadow
    @Final
    public ClientPlayNetworkHandler networkHandler;
    @Shadow
    public boolean autoJumpEnabled;
    @Shadow
    @Final
    protected MinecraftClient client;
    @Unique
    private boolean protocolhack_areSwingCanceledThisTick = false;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public boolean fixMovement(ClientPlayerEntity instance) {
        if (this.isCamera()) {
            boolean bl4;
            double d = this.getX() - this.lastX;
            double e = this.getY() - this.lastBaseY;
            double f = this.getZ() - this.lastZ;
            double g = this.getYaw() - this.lastYaw;
            double h = this.getPitch() - this.lastPitch;
            if (VersionList.isNewerTo(VersionList.R1_8))
                ++this.ticksSinceLastPositionPacketSent;
            double n = 2.05E-4;
            if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
                n = 9.0E-4D;
            boolean bl3 = MathHelper.squaredMagnitude(d, e, f) > MathHelper.square(n) || this.ticksSinceLastPositionPacketSent >= 20;
            bl4 = g != 0.0 || h != 0.0;
            if (this.hasVehicle()) {
                Vec3d vec3d = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(vec3d.x, -999.0, vec3d.z, this.getYaw(), this.getPitch(), this.onGround));
                bl3 = false;
            } else if (bl3 && bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Full(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.onGround));
            } else if (bl3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.onGround));
            } else if (bl4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(this.getYaw(), this.getPitch(), this.onGround));
            } else if (this.lastOnGround != this.onGround || VersionList.isOlderOrEqualTo(VersionList.R1_8)) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(this.onGround));
            } else {
                TarasandeMain.Companion.get().getEventDispatcher().call(new EventSkipIdlePacket());
            }
            if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
                ++this.ticksSinceLastPositionPacketSent;

            if (bl3) {
                this.lastX = this.getX();
                this.lastBaseY = this.getY();
                this.lastZ = this.getZ();
                this.ticksSinceLastPositionPacketSent = 0;
            }
            if (bl4) {
                this.lastYaw = this.getYaw();
                this.lastPitch = this.getPitch();
            }
            this.lastOnGround = this.onGround;
            this.autoJumpEnabled = this.client.options.getAutoJump().getValue();
        }
        return false;
    }

    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    public void injectSwingHand(Hand hand, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8) && protocolhack_areSwingCanceledThisTick)
            ci.cancel();
        protocolhack_areSwingCanceledThisTick = false;
    }

    @Inject(
            method = "tickMovement()V",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z")),
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;sneaking:Z", ordinal = 0)
    )
    private void injectTickMovement(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_4))
            if (this.input.sneaking) {
                this.input.movementSideways = (float)((double)this.input.movementSideways / 0.3D);
                this.input.movementForward = (float)((double)this.input.movementForward / 0.3D);
            }
    }

    @Redirect(method = "tickMovement",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isWalking()Z")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSwimming()Z", ordinal = 0))
    public boolean redirectIsSneakingWhileSwimming(ClientPlayerEntity _this) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_1))
            return false;
        else
            return _this.isSwimming();
    }

    @Inject(method = "isWalking", at = @At("HEAD"), cancellable = true)
    public void easierUnderwaterSprinting(CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_1))
            ci.setReturnValue(input.movementForward >= 0.8);
    }

    @Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z", ordinal = 0))
    private boolean disableSprintSneak(Input input) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_1))
            return input.movementForward >= 0.8F;
        return input.hasForwardMovement();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isTouchingWater()Z"))
    private boolean redirectTickMovement(ClientPlayerEntity self) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            return false; // disable all water related movement
        return self.isTouchingWater();
    }

    @Override
    public void protocolhack_cancelSwingOnce() {
        protocolhack_areSwingCanceledThisTick = true;
    }
}
