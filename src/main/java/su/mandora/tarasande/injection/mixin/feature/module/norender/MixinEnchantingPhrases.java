package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.gui.screen.ingame.EnchantingPhrases;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(EnchantingPhrases.class)
public class MixinEnchantingPhrases {

    @Redirect(method = "generatePhrase", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;fillStyle(Lnet/minecraft/text/Style;)Lnet/minecraft/text/MutableText;"))
    public MutableText hookEnchantmentTranslation(MutableText instance, Style styleOverride) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getEnchantmentTableText().should()) {
            return instance;
        }

        return instance.fillStyle(styleOverride);
    }


}
