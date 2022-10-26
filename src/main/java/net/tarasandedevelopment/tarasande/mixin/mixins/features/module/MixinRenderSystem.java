package net.tarasandedevelopment.tarasande.mixin.mixins.features.module;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleFog;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {

    @Shadow
    @Final
    private static float[] shaderFogColor;

    @Shadow
    private static float shaderFogStart;

    @Shadow
    private static float shaderFogEnd;

    @Redirect(method = "_setShaderFogStart", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogStart:F"))
    private static void hookedFog_setShaderFogStart(float value) {
        shaderFogStart = value;
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if (moduleFog.getEnabled()) {
                shaderFogStart *= moduleFog.getDistance().getMinValue();
            }
        }
    }

    @Redirect(method = "_setShaderFogEnd", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogEnd:F"))
    private static void hookedFog_setShaderFogEnd(float value) {
        shaderFogEnd = value;
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if (moduleFog.getEnabled()) {
                shaderFogEnd *= moduleFog.getDistance().getMaxValue();
            }
        }
    }

    @Inject(method = "_setShaderFogColor", at = @At("TAIL"))
    private static void hookedFog_setShaderFogColor(float f, float g, float h, float i, CallbackInfo ci) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleFog moduleFog = TarasandeMain.Companion.get().getManagerModule().get(ModuleFog.class);
            if (moduleFog.getEnabled()) {
                shaderFogColor[0] = moduleFog.getColor().getColor().getRed() / 255.0f;
                shaderFogColor[1] = moduleFog.getColor().getColor().getGreen() / 255.0f;
                shaderFogColor[2] = moduleFog.getColor().getColor().getBlue() / 255.0f;
            }
        }
    }

    @Redirect(method = "clearColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"))
    private static void hookedFog_clearColor(float red, float green, float blue, float alpha) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
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
