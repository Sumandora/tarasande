package su.mandora.tarasande.injection.mixin.core;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.injection.accessor.IDrawContext;

@Mixin(DrawContext.class)
public class MixinDrawContext implements IDrawContext {

    @Unique
    private boolean tarasande_guiItemRendering = false;

    @Redirect(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    public void removeZTranslation_drawItem(MatrixStack instance, float x, float y, float z) {
        instance.translate(x, y, tarasande_guiItemRendering ? 0 : z);
    }

    @Redirect(method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;scale(FFF)V"))
    public void removeZScaling_drawItem(MatrixStack instance, float x, float y, float z) {
        instance.scale(x, y, tarasande_guiItemRendering ? 0 : z);
    }

    @Redirect(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V", ordinal = 0))
    public void removeZTranslation_drawItemInSlot(MatrixStack instance, float x, float y, float z) {
        instance.translate(x, y, tarasande_guiItemRendering ? 0 : z);
    }

    @Redirect(method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayer;getGuiOverlay()Lnet/minecraft/client/render/RenderLayer;"))
    public RenderLayer changeLayer() {
        if (tarasande_guiItemRendering)
            return RenderLayer.getGui();
        return RenderLayer.getGuiOverlay();
    }

    @Override
    public void tarasande_setGuiItemRendering(boolean enabled) {
        tarasande_guiItemRendering = enabled;
    }

    @Override
    public boolean tarasande_isGuiItemRendering() {
        return tarasande_guiItemRendering;
    }
}
