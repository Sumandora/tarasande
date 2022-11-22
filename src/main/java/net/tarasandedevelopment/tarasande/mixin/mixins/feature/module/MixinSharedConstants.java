package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module;

import net.minecraft.SharedConstants;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.chat.ModuleAllowAllCharacters;
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
