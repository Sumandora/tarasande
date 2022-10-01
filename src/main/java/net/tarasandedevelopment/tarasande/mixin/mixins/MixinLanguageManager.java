package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;
import net.tarasandedevelopment.tarasande.mixin.accessor.ILanguageManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LanguageManager.class)
public class MixinLanguageManager implements ILanguageManager {
    @Shadow
    @Final
    private static LanguageDefinition ENGLISH_US;

    @Override
    public LanguageDefinition tarasande_getEnglishUS() {
        return ENGLISH_US;
    }
}
