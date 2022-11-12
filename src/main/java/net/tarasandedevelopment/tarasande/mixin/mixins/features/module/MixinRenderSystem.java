package net.tarasandedevelopment.tarasande.mixin.mixins.features.module;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleFog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {

    @Redirect(method = "clearColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"))
    private static void hookedFog_clearColor(float red, float green, float blue, float alpha) {
        ModuleFog moduleFog = TarasandeMain.Companion.get().getModuleSystem().get(ModuleFog.class);
        if (moduleFog.getEnabled()) {
            red = moduleFog.getColor().getColor().getRed() / 255.0f;
            green = moduleFog.getColor().getColor().getGreen() / 255.0f;
            blue = moduleFog.getColor().getColor().getBlue() / 255.0f;
        }
        GlStateManager._clearColor(red, green, blue, alpha);
    }

}
