package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.misc.ModulePotionSaver;

@Mixin(StatusEffectInstance.class)
public class MixinStatusEffectInstance {

    @Shadow private int duration;

    @Inject(method = "updateDuration", at = @At("HEAD"), cancellable = true)
    public void hookPotionSaver(CallbackInfoReturnable<Integer> cir) {
        ModulePotionSaver modulePotionSaver = ManagerModule.INSTANCE.get(ModulePotionSaver.class);
        if (modulePotionSaver.getEnabled().getValue() && modulePotionSaver.getResyncDurations().getValue() && modulePotionSaver.getHalting())
            cir.setReturnValue(duration);
    }

}
