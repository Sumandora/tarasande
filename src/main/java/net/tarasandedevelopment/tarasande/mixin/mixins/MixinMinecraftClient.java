package net.tarasandedevelopment.tarasande.mixin.mixins;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.SocialInteractionsManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.network.encryption.SignatureVerifier;
import net.minecraft.util.Util;
import net.tarasandedevelopment.tarasande.mixin.accessor.IMinecraftClient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.*;
import net.tarasandedevelopment.tarasande.util.render.RenderUtil;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClient {
    @Shadow
    private static int currentFps;
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    protected int attackCooldown;
    @Shadow
    @Final
    private Window window;
    @Mutable
    @Shadow
    @Final
    private Session session;
    @Mutable
    @Shadow
    @Final
    private MinecraftSessionService sessionService;
    private long startTime = 0L;
    @Shadow
    @Final
    private RenderTickCounter renderTickCounter;
    @Mutable
    @Shadow
    @Final
    private UserApiService userApiService;
    @Mutable
    @Shadow
    @Final
    private ProfileKeys profileKeys;
    @Mutable
    @Shadow
    @Final
    private YggdrasilAuthenticationService authenticationService;
    @Mutable
    @Shadow
    @Final
    private SignatureVerifier servicesSignatureVerifier;
    @Mutable
    @Shadow
    @Final
    private SocialInteractionsManager socialInteractionsManager;

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract void handleBlockBreaking(boolean bl);

    @Shadow
    protected abstract boolean doAttack();

    @Shadow
    private int itemUseCooldown;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;createUserApiService(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Lnet/minecraft/client/RunArgs;)Lcom/mojang/authlib/minecraft/UserApiService;"))
    public void injectPreInit(RunArgs args, CallbackInfo ci) {
        TarasandeMain.Companion.get().onPreLoad();
    }

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V", shift = At.Shift.AFTER))
    public void injectPostInit(RunArgs args, CallbackInfo ci) {
        TarasandeMain.Companion.get().onLateLoad();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void injectPreTick(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventTick(EventTick.State.PRE));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void injectPostTick(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventTick(EventTick.State.POST));
    }

    @Inject(method = "stop", at = @At("HEAD"))
    public void injectStop(CallbackInfo ci) {
        TarasandeMain.Companion.get().onUnload();
    }

    @Redirect(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/Window;setScaleFactor(D)V"))
    public void hookedSetScaleFactor(Window instance, double scaleFactor) {
        double prevWidth = instance.getScaledWidth();
        double prevHeight = instance.getScaledHeight();
        instance.setScaleFactor(scaleFactor);
        TarasandeMain.Companion.get().getManagerEvent().call(new EventResolutionUpdate(prevWidth, prevHeight, this.window.getScaledWidth(), this.window.getScaledHeight()));
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void injectPreRender(boolean tick, CallbackInfo ci) {
        this.startTime = System.nanoTime();
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void injectPostRender(boolean tick, CallbackInfo ci) {
        RenderUtil.INSTANCE.setDeltaTime((System.nanoTime() - this.startTime) / 1000000.0);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int hookedMin(int a, int b) {
        EventTicksPerFrame eventTicksPerFrame = new EventTicksPerFrame(b, a);
        TarasandeMain.Companion.get().getManagerEvent().call(eventTicksPerFrame);
        if (eventTicksPerFrame.getCancelled()) {
            return eventTicksPerFrame.getTicks();
        }
        return Math.min(eventTicksPerFrame.getMax(), eventTicksPerFrame.getTicks());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;tick()V")))
    public long hookedGetMeasuringTimeMs() {
        EventTimeTravel eventTimeTravel = new EventTimeTravel(Util.getMeasuringTimeMs());
        TarasandeMain.Companion.get().getManagerEvent().call(eventTimeTravel);
        return eventTimeTravel.getTime();
    }

    @Redirect(method = "hasOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
    public boolean hookedIsGlowing(Entity entity) {
        EventIsGlowing eventIsGlowing = new EventIsGlowing(entity, entity.isGlowing());
        TarasandeMain.Companion.get().getManagerEvent().call(eventIsGlowing);
        return eventIsGlowing.getGlowing();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", shift = At.Shift.BEFORE), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")))
    public void injectHandleInputEvents(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventAttack());
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
    public void hookedHandleBlockBreaking(MinecraftClient instance, boolean bl) {
        EventHandleBlockBreaking eventHandleBlockBreaking = new EventHandleBlockBreaking(bl);
        TarasandeMain.Companion.get().getManagerEvent().call(eventHandleBlockBreaking);
        ((IMinecraftClient) instance).tarasande_invokeHandleBlockBreaking(eventHandleBlockBreaking.getParameter());
    }

    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void injectGetSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        EventSessionService eventSessionService = new EventSessionService(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventSessionService);
        cir.setReturnValue(eventSessionService.getSessionService());
    }

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void injectSetScreen(Screen screen, CallbackInfo ci) {
        final EventChangeScreen eventChangeScreen = new EventChangeScreen(screen);
        TarasandeMain.Companion.get().getManagerEvent().call(eventChangeScreen);

        if (eventChangeScreen.getDirty()) {
            this.setScreen(eventChangeScreen.getNewScreen());
            ci.cancel();
        }
    }

    @Override
    public void tarasande_setSession(Session session) {
        this.session = session;
    }

    @Override
    public int tarasande_getAttackCooldown() {
        return this.attackCooldown;
    }

    @Override
    public void tarasande_setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public void tarasande_invokeDoItemUse() {
        this.doItemUse();
    }

    @Override
    public boolean tarasande_invokeDoAttack() {
        return this.doAttack();
    }

    @Override
    public RenderTickCounter tarasande_getRenderTickCounter() {
        return renderTickCounter;
    }

    @Override
    public void tarasande_invokeHandleBlockBreaking(boolean bl) {
        handleBlockBreaking(bl);
    }

    @Override
    public int tarasande_getCurrentFPS() {
        return currentFps;
    }

    @Override
    public void tarasande_setAuthenticationService(YggdrasilAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void tarasande_setSessionService(MinecraftSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void tarasande_setUserApiService(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @Override
    public void tarasande_setServicesSignatureVerifier(SignatureVerifier signatureVerifier) {
        this.servicesSignatureVerifier = signatureVerifier;
    }

    @Override
    public void tarasande_setSocialInteractionsManager(SocialInteractionsManager socialInteractionsManager) {
        this.socialInteractionsManager = socialInteractionsManager;
    }

    @Override
    public UserApiService tarasande_getUserApiService() {
        return userApiService;
    }

    @Override
    public void tarasande_setProfileKeys(ProfileKeys profileKeys) {
        this.profileKeys = profileKeys;
    }

    @Override
    public int tarasande_getItemUseCooldown() {
        return itemUseCooldown;
    }

    @Override
    public void tarasande_setItemUseCooldown(int itemUseCooldown) {
        this.itemUseCooldown = itemUseCooldown;
    }
}
