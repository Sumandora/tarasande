package su.mandora.tarasande.mixin.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventClearColor;
import su.mandora.tarasande.event.EventFogColor;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    @Shadow(remap = false)
    @Final
    private static float[] shaderFogColor;

    @Shadow(remap = false)
    private static float shaderFogStart;

    @Shadow(remap = false)
    private static float shaderFogEnd;

    @Inject(method = "flipFrame", at = @At("HEAD"), remap = false)
    private static void injectFlipFrame(long window, CallbackInfo ci) {
        RotationUtil.INSTANCE.updateFakeRotation(false);
    }

    @Inject(method = "_setShaderFogStart", at = @At("HEAD"), remap = false, cancellable = true)
    private static void inject_setShaderFogStart(float f, CallbackInfo ci) {
        EventFogColor eventFogColor = new EventFogColor(f, 0.0f, 0.0f, 0.0f, 0.0f);
        TarasandeMain.Companion.get().getManagerEvent().call(eventFogColor);
        shaderFogStart = eventFogColor.getStart();
        ci.cancel();
    }

    @Inject(method = "_setShaderFogEnd", at = @At("HEAD"), remap = false, cancellable = true)
    private static void inject_setShaderFogEnd(float f, CallbackInfo ci) {
        EventFogColor eventFogColor = new EventFogColor(0.0f, f, 0.0f, 0.0f, 0.0f);
        TarasandeMain.Companion.get().getManagerEvent().call(eventFogColor);
        shaderFogEnd = eventFogColor.getEnd();
        ci.cancel();
    }

    @Inject(method = "_setShaderFogColor", at = @At("HEAD"), remap = false, cancellable = true)
    private static void inject_setShaderFogColor(float f, float g, float h, float i, CallbackInfo ci) {
        EventFogColor eventFogColor = new EventFogColor(0.0f, 0.0f, f, g, h);
        TarasandeMain.Companion.get().getManagerEvent().call(eventFogColor);
        shaderFogColor[0] = eventFogColor.getRed();
        shaderFogColor[1] = eventFogColor.getGreen();
        shaderFogColor[2] = eventFogColor.getBlue();
        shaderFogColor[3] = i;
        ci.cancel();
    }

    @Inject(method = "clearColor", at = @At("HEAD"), remap = false, cancellable = true)
    private static void injectClearColor(float red, float green, float blue, float alpha, CallbackInfo ci) {
        EventClearColor eventClearColor = new EventClearColor(red, green, blue);
        TarasandeMain.Companion.get().getManagerEvent().call(eventClearColor);
        GlStateManager._clearColor(eventClearColor.getRed(), eventClearColor.getGreen(), eventClearColor.getBlue(), alpha);
        ci.cancel();
    }

}
