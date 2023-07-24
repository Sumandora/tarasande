package su.mandora.tarasande.injection.mixin.feature.module;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModuleEveryItemOnArmor;

@Mixin(targets = "net.minecraft.screen.PlayerScreenHandler$1")
public class MixinPlayerScreenHandler_1 {

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