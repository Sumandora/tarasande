package su.mandora.tarasande.mixin.mixins;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
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
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.base.screen.accountmanager.account.Account;
import su.mandora.tarasande.event.*;
import su.mandora.tarasande.mixin.accessor.IMinecraftClient;
import su.mandora.tarasande.module.render.ModuleESP;
import su.mandora.tarasande.util.reflection.ReflectionUtil;
import su.mandora.tarasande.util.reflection.ReflectorAny;
import su.mandora.tarasande.util.reflection.ReflectorClass;
import su.mandora.tarasande.util.render.RenderUtil;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient implements IMinecraftClient {
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    protected int attackCooldown;
    @Shadow
    @Final
    private Window window;
    @Mutable
    @Shadow
    @Final
    private Session session;
    @Shadow
    @Final
    private MinecraftSessionService sessionService;
    private long startTime = 0L;
    @Shadow
    @Final
    private RenderTickCounter renderTickCounter;

    @Shadow
    protected abstract void doItemUse();

    @Shadow
    protected abstract void handleBlockBreaking(boolean bl);

    @Shadow
    protected abstract boolean doAttack();

    @Shadow
    private static int currentFps;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;createUserApiService(Lcom/mojang/authlib/yggdrasil/YggdrasilAuthenticationService;Lnet/minecraft/client/RunArgs;)Lcom/mojang/authlib/minecraft/UserApiService;"))
    public void injectPreInit(RunArgs args, CallbackInfo ci) {
        TarasandeMain.Companion.get().onPreLoad();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
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

    @Inject(method = "onResolutionChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;resize(IIZ)V", shift = At.Shift.AFTER))
    public void injectOnResolutionChanged(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventResolutionUpdate((float) this.window.getFramebufferWidth(), (float) this.window.getFramebufferHeight()));
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
        if (TarasandeMain.Companion.get().getClientValues().getUnlockTicksPerFrame().getValue()) {
            return b;
        }
        return Math.min(a, b);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;tick()V")))
    public long hookedGetMeasuringTimeMs() {
        EventTimeTravel eventTimeTravel = new EventTimeTravel(Util.getMeasuringTimeMs());
        TarasandeMain.Companion.get().getManagerEvent().call(eventTimeTravel);
        return eventTimeTravel.getTime();
    }

    @Redirect(method = "hasOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isGlowing()Z"))
    public boolean hookedIsGlowing(Entity entity) {
        ModuleESP moduleESP = TarasandeMain.Companion.get().getManagerModule().get(ModuleESP.class);
        return (moduleESP.getEnabled() && moduleESP.filter(entity)) || entity.isGlowing();
    }

    @Inject(method = "createUserApiService", at = @At("HEAD"), cancellable = true)
    public void injectCreateUserApiService(YggdrasilAuthenticationService authService, RunArgs runArgs, CallbackInfoReturnable<UserApiService> cir) {
        ReflectorClass reflectorClass = ReflectionUtil.INSTANCE.createReflectorClass("net.fabricmc.loader.launch.common.FabricLauncherBase");
        if (reflectorClass != null) {
            ReflectorAny getLauncher = reflectorClass.invokeMethod("getLauncher");
            if (getLauncher != null) {
                ReflectorAny isDevelopment = getLauncher.asReflectorClass().invokeMethod("isDevelopment");
                if (isDevelopment != null && isDevelopment.interpretAs(Boolean.class)) {
                    cir.setReturnValue(UserApiService.OFFLINE);
                }
            }
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z", shift = At.Shift.BEFORE), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z")))
    public void injectHandleInputEvents(CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventAttack());
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V"))
    public void hookedHandleBlockBreaking(MinecraftClient instance, boolean bl) {
        EventHandleBlockBreaking eventHandleBlockBreaking = new EventHandleBlockBreaking(bl);
        TarasandeMain.Companion.get().getManagerEvent().call(eventHandleBlockBreaking);
        ((IMinecraftClient) instance).invokeHandleBlockBreaking(eventHandleBlockBreaking.getParameter());
    }

    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void injectGetSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        Account account = TarasandeMain.Companion.get().getScreens().getBetterScreenAccountManager().getCurrentAccount();
        if (account != null && account.getSessionService() != null) {
            cir.setReturnValue(account.getSessionService());
        }
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public int getAttackCooldown() {
        return this.attackCooldown;
    }

    @Override
    public void setAttackCooldown(int attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    @Override
    public void invokeDoItemUse() {
        this.doItemUse();
    }

    @Override
    public void invokeDoAttack() {
        this.doAttack();
    }

    @Override
    public RenderTickCounter getRenderTickCounter() {
        return renderTickCounter;
    }

    @Override
    public void invokeHandleBlockBreaking(boolean bl) {
        handleBlockBreaking(bl);
    }

    @Override
    public int getCurrentFPS() {
        return currentFps;
    }
}
