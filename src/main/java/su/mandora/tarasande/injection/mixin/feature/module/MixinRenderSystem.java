package su.mandora.tarasande.injection.mixin.feature.module;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleFog;

@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {

    @Redirect(method = "clearColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_clearColor(FFFF)V"))
    private static void hookFog(float red, float green, float blue, float alpha) {
        ModuleFog moduleFog = ManagerModule.INSTANCE.get(ModuleFog.class);
        if (moduleFog.getEnabled().getValue()) {
            red = moduleFog.getColor().getColor().getRed() / 255.0F;
            green = moduleFog.getColor().getColor().getGreen() / 255.0F;
            blue = moduleFog.getColor().getColor().getBlue() / 255.0F;
        }
        GlStateManager._clearColor(red, green, blue, alpha);
    }

}
