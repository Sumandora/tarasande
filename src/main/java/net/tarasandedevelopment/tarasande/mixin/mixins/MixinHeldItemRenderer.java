package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.util.Hand;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleNoSwing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    public void injectResetEquipProgress(Hand hand, CallbackInfo ci) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleNoSwing moduleNoSwing = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSwing.class);
            if(moduleNoSwing.getEnabled() && moduleNoSwing.getFixAnimations().getValue())
                ci.cancel();
        }
    }

}
