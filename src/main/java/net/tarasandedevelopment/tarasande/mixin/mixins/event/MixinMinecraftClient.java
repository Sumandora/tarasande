package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Final
    private Window window;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "tick", at = @At("HEAD"))
    public void hookEventTickPre(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventTick(EventTick.State.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void hookEventTickPost(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventTick(EventTick.State.POST));
    }

    @Redirect(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setScaleFactor(D)V"))
    public void hookEventResolutionUpdate(Window instance, double scaleFactor) {
        double prevWidth = instance.getScaledWidth();
        double prevHeight = instance.getScaledHeight();
        instance.setScaleFactor(scaleFactor);
        TarasandeMain.Companion.get().getManagerEvent().call(new EventResolutionUpdate(prevWidth, prevHeight, this.window.getScaledWidth(), this.window.getScaledHeight()));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;tick()V")))
    public long hookEventTimeTravel() {
        EventTimeTravel eventTimeTravel = new EventTimeTravel(Util.getMeasuringTimeMs());
        TarasandeMain.Companion.get().getManagerEvent().call(eventTimeTravel);
        return eventTimeTravel.getTime();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", shift = At.Shift.BEFORE), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")))
    public void hookEventAttack(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventAttack());
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
    public void hookEventHandleBlockBreaking(MinecraftClient instance, boolean bl) {
        EventHandleBlockBreaking eventHandleBlockBreaking = new EventHandleBlockBreaking(bl);
        TarasandeMain.Companion.get().getManagerEvent().call(eventHandleBlockBreaking);
        instance.handleBlockBreaking(eventHandleBlockBreaking.getParameter());
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void hookEventChangeScreen(Screen screen, CallbackInfo ci) {
        final EventChangeScreen eventChangeScreen = new EventChangeScreen(screen);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChangeScreen);

        if (eventChangeScreen.getCancelled()) {
            ci.cancel();
            return;
        }
        if (eventChangeScreen.getDirty()) {
            this.setScreen(eventChangeScreen.getNewScreen());
            ci.cancel();
        }
    }

    @Redirect(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;showsDeathScreen()Z"))
    public boolean hookEventRespawn(ClientPlayerEntity instance) {
        EventRespawn eventRespawn = new EventRespawn(instance.showsDeathScreen());
        TarasandeMain.Companion.get().getManagerEvent().call(eventRespawn);
        return eventRespawn.getShowDeathScreen();
    }
}
