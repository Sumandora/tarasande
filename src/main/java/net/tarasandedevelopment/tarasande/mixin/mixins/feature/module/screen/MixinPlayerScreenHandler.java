package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.screen;

import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.exploit.ModuleEveryItemOnArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public class MixinPlayerScreenHandler {

    @Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_getMaxItemCount(CallbackInfoReturnable<Integer> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModuleEveryItemOnArmor.class).getEnabled()) {
            cir.setReturnValue(64);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_canInsert(CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModuleEveryItemOnArmor.class).getEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
