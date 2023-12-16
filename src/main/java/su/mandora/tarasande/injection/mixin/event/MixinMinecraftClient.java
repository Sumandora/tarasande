package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.*;
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    public GameOptions options;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public Screen currentScreen;
    @Shadow
    @Final
    private Window window;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "stop", at = @At("HEAD"))
    public void unloadClient(CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventShutdown());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void hookEventTickPre(CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventTick(EventTick.State.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void hookEventTickPost(CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventTick(EventTick.State.POST));
    }

    @Inject(method = "onResolutionChanged", at = @At("HEAD"))
    public void hookEventResolutionUpdate(CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventResolutionUpdate(this.window.getFramebufferWidth(), this.window.getFramebufferHeight()));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;tick()V")))
    public long hookEventTimeTravel() {
        EventTimeTravel eventTimeTravel = new EventTimeTravel(Util.getMeasuringTimeMs());
        EventDispatcher.INSTANCE.call(eventTimeTravel);
        return eventTimeTravel.getTime();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")))
    public void hookEventAttack(CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventAttack());
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
    public void hookEventHandleBlockBreaking(MinecraftClient instance, boolean bl) {
        EventHandleBlockBreaking eventHandleBlockBreaking = new EventHandleBlockBreaking(bl);
        EventDispatcher.INSTANCE.call(eventHandleBlockBreaking);
        instance.handleBlockBreaking(eventHandleBlockBreaking.getParameter());
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void hookEventChangeScreen(Screen screen, CallbackInfo ci) {
        final EventChangeScreen eventChangeScreen = new EventChangeScreen(screen);
        EventDispatcher.INSTANCE.call(eventChangeScreen);

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
        EventShowsDeathScreen eventShowsDeathScreen = new EventShowsDeathScreen(instance.showsDeathScreen());
        EventDispatcher.INSTANCE.call(eventShowsDeathScreen);
        return eventShowsDeathScreen.getShowsDeathScreen();
    }

    @Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
    public void hookEventDoAttack(CallbackInfoReturnable<Boolean> cir) {
        EventDoAttack eventDoAttack = new EventDoAttack();
        EventDispatcher.INSTANCE.call(eventDoAttack);
        if (eventDoAttack.getCancelled())
            cir.setReturnValue(false);
    }

    @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
            ordinal = 4, shift = At.Shift.BEFORE))
    public void injectTick(CallbackInfo ci) {
        if (TarasandeValues.INSTANCE.getExecuteScreenInputsInTicks().getValue()) // Counterpart in MixinRenderSystem
            EventDispatcher.INSTANCE.call(new EventScreenInput(false));
    }

    @Inject(method = "getTargetMillisPerTick", at = @At("RETURN"), cancellable = true)
    public void hookEventTickRate(float millis, CallbackInfoReturnable<Float> cir) {
        EventTickTime eventTickTime = new EventTickTime(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventTickTime);
        cir.setReturnValue(eventTickTime.getTickTime());
    }
}
