package net.tarasandedevelopment.tarasande.mixin.mixins.module;

import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.player.ModuleEveryItemOnArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/PlayerScreenHandler$1")
public class MixinPlayerArmorSlot {

    @Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    public void everyItemOnArmor_getMaxItemCount(CallbackInfoReturnable<Integer> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleEveryItemOnArmor.class).getEnabled()) {
            cir.setReturnValue(64);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void everyItemOnArmor_canInsert(CallbackInfoReturnable<Boolean> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleEveryItemOnArmor.class).getEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void everyItemOnArmor_canTakeItems(CallbackInfoReturnable<Boolean> cir) {
        if (!TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleEveryItemOnArmor.class).getEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
