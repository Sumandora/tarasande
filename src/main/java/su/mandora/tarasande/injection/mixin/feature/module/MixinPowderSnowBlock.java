package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.block.PowderSnowBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleAntiPowderSnow;

@Mixin(PowderSnowBlock.class)
public class MixinPowderSnowBlock {

    @Inject(method = "canWalkOnPowderSnow", at = @At("HEAD"), cancellable = true)
    private static void hookAntiPowderSnow(CallbackInfoReturnable<Boolean> cir) {
        if(ManagerModule.INSTANCE.get(ModuleAntiPowderSnow.class).getEnabled().getValue())
            cir.setReturnValue(true);
    }

}
