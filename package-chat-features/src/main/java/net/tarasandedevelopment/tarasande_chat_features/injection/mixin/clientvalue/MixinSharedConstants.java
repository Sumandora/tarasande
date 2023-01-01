package net.tarasandedevelopment.tarasande_chat_features.injection.mixin.clientvalue;

import net.minecraft.SharedConstants;
import net.tarasandedevelopment.tarasande_chat_features.clientvalue.ChatValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void hookAllowEveryCharacter(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (ChatValues.INSTANCE.getAllowAllCharacters().getValue()) {
            cir.setReturnValue(true);
        }
    }
}
