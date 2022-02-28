package su.mandora.tarasande.mixin.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.*;
import su.mandora.tarasande.mixin.accessor.IClientPlayerEntity;
import su.mandora.tarasande.util.math.rotation.Rotation;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    Rotation cachedRotation;
    EventVanillaFlight cachedEventVanillaFlight = null;

    @Shadow
    private float lastYaw;
    @Shadow
    private float lastPitch;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void injectSendChatMessage(String message, CallbackInfo ci) {
        EventChat eventChat = new EventChat(message);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChat);
        if (eventChat.getCancelled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE))
    public void preTick(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdate(EventUpdate.State.PRE));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    public void prePacketTick(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdate(EventUpdate.State.PRE_PACKET));

        cachedRotation = new Rotation(getYaw(), getPitch());
        if (RotationUtil.INSTANCE.getFakeRotation() != null) {
            Rotation rotation = RotationUtil.INSTANCE.getFakeRotation();
            setYaw(rotation.getYaw());
            setPitch(rotation.getPitch());
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE))
    public void postTick(CallbackInfo ci) {
        setYaw(cachedRotation.getYaw());
        setPitch(cachedRotation.getPitch());

        TarasandeMain.Companion.get().getManagerEvent().call(new EventUpdate(EventUpdate.State.POST));
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;getFoodLevel()I"), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;hasForwardMovement()Z")))
    public boolean hookedIsUsingItem(ClientPlayerEntity clientPlayerEntity) {
        EventSlowdown eventSlowdown = new EventSlowdown(clientPlayerEntity.isUsingItem());
        TarasandeMain.Companion.get().getManagerEvent().call(eventSlowdown);
        return eventSlowdown.getUsingItem();
    }

    @ModifyConstant(method = "tickMovement", constant = @Constant(floatValue = 0.2F))
    public float slowdownAmount(float original) {
        EventSlowdownAmount eventSlowdownAmount = new EventSlowdownAmount(original);
        TarasandeMain.Companion.get().getManagerEvent().call(eventSlowdownAmount);
        return eventSlowdownAmount.getSlowdownAmount();
    }

    @Redirect(method = "tickMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerAbilities;flying:Z", ordinal = 6))
    public boolean flying(PlayerAbilities instance) {
        EventVanillaFlight eventVanillaFlight = new EventVanillaFlight(instance.flying, instance.getFlySpeed());
        TarasandeMain.Companion.get().getManagerEvent().call(eventVanillaFlight);
        cachedEventVanillaFlight = eventVanillaFlight;
        return eventVanillaFlight.getFlying();
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerAbilities;getFlySpeed()F"))
    public float hookedGetFlySpeed(PlayerAbilities instance) {
        return cachedEventVanillaFlight.getDirty() ? cachedEventVanillaFlight.getFlightSpeed() : instance.getFlySpeed();
    }

    @Override
    public void travel(Vec3d movementInput) {
        boolean flying = getAbilities().flying;
        float flySpeed = getAbilities().getFlySpeed();

        if (cachedEventVanillaFlight.getDirty()) {
            getAbilities().flying = cachedEventVanillaFlight.getFlying();
            getAbilities().setFlySpeed(cachedEventVanillaFlight.getFlightSpeed());
        }

        super.travel(movementInput);

        getAbilities().flying = flying;
        getAbilities().setFlySpeed(flySpeed);
    }

    @Override
    public float getLastYaw() {
        return lastYaw;
    }

    @Override
    public float getLastPitch() {
        return lastPitch;
    }
}
