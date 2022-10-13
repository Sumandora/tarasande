package net.tarasandedevelopment.tarasande.mixin.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventChat;
import net.tarasandedevelopment.tarasande.event.EventIsWalking;
import net.tarasandedevelopment.tarasande.event.EventUpdate;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity;
import net.tarasandedevelopment.tarasande.module.movement.ModuleFlight;
import net.tarasandedevelopment.tarasande.module.movement.ModuleNoSlowdown;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Unique
    boolean bypassChat;

    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Shadow private float mountJumpStrength;

    @Shadow private int field_3938;

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void injectSendChatMessagePacket(String message, Text preview, CallbackInfo ci) {
        if (bypassChat)
            return;
        EventChat eventChat = new EventChat(message);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChat);
        if (eventChat.getCancelled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE), cancellable = true)
    public void preTick(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        EventUpdate eventUpdate = new EventUpdate(EventUpdate.State.PRE);
        TarasandeMain.Companion.get().getManagerEvent().call(eventUpdate);
        if (eventUpdate.getCancelled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    public void prePacketTick(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdate(EventUpdate.State.PRE_PACKET));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void postTick(CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player)
            return;

        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdate(EventUpdate.State.POST));
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getFoodLevel()I"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z")))
    public boolean hookedIsUsingItem(ClientPlayerEntity clientPlayerEntity) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleNoSlowdown moduleNoSlowdown = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSlowdown.class);
            if(moduleNoSlowdown.getEnabled()) {
                if(moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                    return false;
            }
        }
        return clientPlayerEntity.isUsingItem();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    public float slowdownAmount(float original) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleNoSlowdown moduleNoSlowdown = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSlowdown.class);
            if(moduleNoSlowdown.getEnabled()) {
                if(moduleNoSlowdown.isActionEnabled(moduleNoSlowdown.getActions()))
                    return (float) moduleNoSlowdown.getSlowdown().getValue();
            }
        }
        return original;
    }

    @Inject(method = "isWalking", at = @At("RETURN"), cancellable = true)
    public void injectIsWalking(CallbackInfoReturnable<Boolean> cir) {
        EventIsWalking eventIsWalking = new EventIsWalking(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventIsWalking);
        cir.setReturnValue(eventIsWalking.getWalking());
    }

    @Unique
    boolean flight;

    @Unique
    float flightSpeed;

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;knockDownwards()V"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z")))
    public boolean flying(PlayerAbilities instance) {
        flight = false;
        flightSpeed = 0.05f;
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFlight moduleFlight = TarasandeMain.Companion.get().getManagerModule().get(ModuleFlight.class);
            if (moduleFlight.getEnabled() && moduleFlight.getMode().isSelected(0)) {
                flight = true;
                flightSpeed = (float) moduleFlight.getFlightSpeed().getValue();
                return true;
            }
        }
        return instance.flying;
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    public float hookedGetFlySpeed(PlayerAbilities instance) {
        return flight ? flightSpeed : instance.getFlySpeed();
    }

    @Override
    public void travel(Vec3d movementInput) {
        boolean fallFlying = this.getFlag(Entity.FALL_FLYING_FLAG_INDEX);

        boolean flying = getAbilities().flying;
        float flySpeed = getAbilities().getFlySpeed();

        if (flight) {
            getAbilities().flying = flight;
            getAbilities().setFlySpeed(flightSpeed);
        }

        super.travel(movementInput);

        getAbilities().flying = flying;
        getAbilities().setFlySpeed(flySpeed);

        if (flight)
            this.setFlag(Entity.FALL_FLYING_FLAG_INDEX, fallFlying);
    }

    @Override
    public float tarasande_getLastYaw() {
        return lastYaw;
    }

    @Override
    public float tarasande_getLastPitch() {
        return lastPitch;
    }

    @Override
    public boolean tarasande_getBypassChat() {
        return bypassChat;
    }

    @Override
    public void tarasande_setBypassChat(boolean bypassChat) {
        this.bypassChat = bypassChat;
    }

    @Override
    public void tarasande_setMountJumpStrength(float jumpPower) {
        this.mountJumpStrength = jumpPower;
    }

    @Override
    public void tarasande_setField_3938(int jumpPowerCounter) {
        this.field_3938 = jumpPowerCounter;
    }
}
