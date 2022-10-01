package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventResetEquipProgress;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    public void injectResetEquipProgress(Hand hand, CallbackInfo ci) {
        EventResetEquipProgress eventResetEquipProgress = new EventResetEquipProgress();
        TarasandeMain.Companion.get().getManagerEvent().call(eventResetEquipProgress);
        if (eventResetEquipProgress.getCancelled())
            ci.cancel();
    }

}
