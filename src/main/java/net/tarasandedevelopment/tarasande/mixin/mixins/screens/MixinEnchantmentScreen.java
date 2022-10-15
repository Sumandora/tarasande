package net.tarasandedevelopment.tarasande.mixin.mixins.screens;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.qualityoflife.ModuleEnchantmentTranslation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentScreen.class)
public class MixinEnchantmentScreen {

    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawTrimmed(Lnet/minecraft/text/StringVisitable;IIII)V"))
    public void modifyDrawTrimmed(TextRenderer instance, StringVisitable text, int x, int y, int maxWidth, int color) {
        if (TarasandeMain.Companion.get().getDisabled()) {
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleEnchantmentTranslation.class).getEnabled())
                text = Text.of(text.getString()); //Removes the style
        }

        instance.drawTrimmed(text, x, y, maxWidth, color);
    }
}
