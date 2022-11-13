package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import net.tarasandedevelopment.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.events.EventChat;
import net.tarasandedevelopment.tarasande.events.EventIsWalking;
import net.tarasandedevelopment.tarasande.events.EventUpdate;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements IClientPlayerEntity {

    @Unique
    private boolean tarasande_bypassChat;

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow
    public abstract float getYaw(float tickDelta);

    @Shadow
    public abstract float getPitch(float tickDelta);

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void hookEventChat(String message, Text preview, CallbackInfo ci) {
        if (tarasande_bypassChat)
            return;
        EventChat eventChat = new EventChat(message);
        EventDispatcher.INSTANCE.call(eventChat);
        if (eventChat.getCancelled())
            ci.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE), cancellable = true)
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

    @Inject(method = "isWalking", at = @At("RETURN"), cancellable = true)
    public void hookEventIsWalking(CallbackInfoReturnable<Boolean> cir) {
        EventIsWalking eventIsWalking = new EventIsWalking(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventIsWalking);
        cir.setReturnValue(eventIsWalking.getWalking());
    }

    @Override
    public boolean tarasande_getBypassChat() {
        return tarasande_bypassChat;
    }

    @Override
    public void tarasande_setBypassChat(boolean bypassChat) {
        this.tarasande_bypassChat = bypassChat;
    }
}
