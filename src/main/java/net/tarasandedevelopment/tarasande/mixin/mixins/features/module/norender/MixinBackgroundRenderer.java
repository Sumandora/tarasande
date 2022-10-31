package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.features.module.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class MixinBackgroundRenderer {

    @Inject(method = "applyFog", at = @At("HEAD"))
    private static void noRender_applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class).getWorld().getFog().should()) {
            if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
                RenderSystem.setShaderFogStart(viewDistance * 4);
                RenderSystem.setShaderFogEnd(viewDistance * 4.25F);
            }
        }
    }
}
