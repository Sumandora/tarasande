package net.tarasandedevelopment.tarasande_protocol_hack.injection.mixin.module;

import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande_protocol_hack.module.ModuleEveryItemOnArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.PlayerScreenHandler$1")
public class MixinPlayerScreenHandler {

    @Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_getMaxItemCount(CallbackInfoReturnable<Integer> cir) {
        if (ManagerModule.INSTANCE.get(ModuleEveryItemOnArmor.class).getEnabled().getValue()) {
            cir.setReturnValue(64);
        }
    }

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void hookEveryItemOnArmor_canInsert(CallbackInfoReturnable<Boolean> cir) {
        if (ManagerModule.INSTANCE.get(ModuleEveryItemOnArmor.class).getEnabled().getValue()) {
            cir.setReturnValue(true);
        }
    }
}
