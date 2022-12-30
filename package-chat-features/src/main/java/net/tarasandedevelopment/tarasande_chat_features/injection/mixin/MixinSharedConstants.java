package net.tarasandedevelopment.tarasande_chat_features.injection.mixin;

import net.minecraft.SharedConstants;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande_chat_features.module.ModuleAllowAllCharacters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void hookAllowEveryCharacter(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModuleAllowAllCharacters.class).getEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
