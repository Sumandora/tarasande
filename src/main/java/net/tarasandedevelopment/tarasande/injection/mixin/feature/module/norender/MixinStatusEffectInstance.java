package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class MixinStatusEffectInstance {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void hookNoRender(CallbackInfoReturnable<Boolean> cir) {
        final ModuleNoRender moduleNoRender = ManagerModule.INSTANCE.get(ModuleNoRender.class);
        if (!moduleNoRender.getEnabled() || moduleNoRender.getHud().getPotionIcons().isSelected(0)) return;

        cir.setReturnValue(moduleNoRender.getHud().getPotionIcons().isSelected(2));
    }
}
