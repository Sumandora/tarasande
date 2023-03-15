package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.viafabricplus.TarasandeSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.PlayerScreenHandler$1")
public class MixinPlayerScreenHandler_1 {

    @Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_getMaxItemCount(CallbackInfoReturnable<Integer> cir) {
        if (TarasandeSettings.INSTANCE.getAllowEveryItemOnArmor().getValue()) {
            cir.setReturnValue(64);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_canInsert(CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeSettings.INSTANCE.getAllowEveryItemOnArmor().getValue()) {
            cir.setReturnValue(true);
        }
    }
}