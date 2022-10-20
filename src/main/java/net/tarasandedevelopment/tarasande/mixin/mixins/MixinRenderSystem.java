package net.tarasandedevelopment.tarasande.mixin.mixins;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventScreenInput;
import net.tarasandedevelopment.tarasande.module.render.ModuleFog;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
        if (VersionList.isNewerOrEqualTo(VersionList.R1_13)) {
            EventScreenInput eventScreenInput = new EventScreenInput(false);
            TarasandeMain.Companion.get().getEventDispatcher().call(eventScreenInput);
        }

        RotationUtil.INSTANCE.updateFakeRotation(false);
    }

    @Redirect(method = "_setShaderFogStart", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogStart:F"), remap = false)
    private static void hookedShaderFogStart(float value) {
        shaderFogStart = value;
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if(moduleFog.getEnabled()) {
                shaderFogStart *= moduleFog.getDistance().getMinValue();
            }
        }
    }

    @Redirect(method = "_setShaderFogEnd", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogEnd:F"), remap = false)
    private static void hookedShaderFogEnd(float value) {
        shaderFogEnd = value;
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if(moduleFog.getEnabled()) {
                shaderFogEnd *= moduleFog.getDistance().getMaxValue();
            }
        }
    }

    @Inject(method = "_setShaderFogColor", at = @At("TAIL"), remap = false)
    private static void inject_setShaderFogColor(float f, float g, float h, float i, CallbackInfo ci) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if(moduleFog.getEnabled()) {
                shaderFogColor[0] = moduleFog.getColor().getColor().getRed() / 255.0f;
                shaderFogColor[1] = moduleFog.getColor().getColor().getGreen() / 255.0f;
                shaderFogColor[2] = moduleFog.getColor().getColor().getBlue() / 255.0f;
            }
        }
    }

    @Redirect(method = "clearColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"), remap = false)
    private static void injectClearColor(float red, float green, float blue, float alpha) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if (moduleFog.getEnabled()) {
                red = moduleFog.getColor().getColor().getRed() / 255.0f;
                green = moduleFog.getColor().getColor().getGreen() / 255.0f;
                blue = moduleFog.getColor().getColor().getBlue() / 255.0f;
            }
        }
        GlStateManager._clearColor(red, green, blue, alpha);
    }

}
