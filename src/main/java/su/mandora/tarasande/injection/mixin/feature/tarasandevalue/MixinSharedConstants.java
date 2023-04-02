package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.SharedConstants;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.Chat;

@Mixin(SharedConstants.class)
public class MixinSharedConstants {

    @Inject(method = "isValidChar", at = @At("HEAD"), cancellable = true)
    private static void hookAllowEveryCharacter(char chr, CallbackInfoReturnable<Boolean> cir) {
        if (Chat.INSTANCE.getAllowAllCharacters().getValue()) {
            cir.setReturnValue(true);
        }
    }
}
