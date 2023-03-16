package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreen.class)
public class MixinEnchantmentScreen {

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawTrimmed(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/StringVisitable;IIII)V"))
    public void hookEnchantmentTranslation(TextRenderer instance, MatrixStack matrices, StringVisitable text, int x, int y, int maxWidth, int color) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getEnchantmentTableText().should()) {
            text = Text.of(text.getString()); //Removes the style
        }

        instance.drawTrimmed(matrices, text, x, y, maxWidth, color);
    }
}
