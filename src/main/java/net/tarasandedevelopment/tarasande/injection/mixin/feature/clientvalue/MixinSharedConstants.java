package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.SharedConstants;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void hookAllowEveryCharacter(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (Chat.INSTANCE.getAllowAllCharacters().getValue()) {
            cir.setReturnValue(true);
        }
    }
}
