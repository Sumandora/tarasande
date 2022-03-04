package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.ILanguageManager;

@Mixin(LanguageManager.class)
public class MixinLanguageManager implements ILanguageManager {
    @Shadow
    @Final
    private static LanguageDefinition ENGLISH_US;

    @Override
    public LanguageDefinition getEnglishUS() {
        return ENGLISH_US;
    }
}
