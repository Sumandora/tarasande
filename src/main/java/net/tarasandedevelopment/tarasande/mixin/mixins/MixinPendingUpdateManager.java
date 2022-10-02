package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.PendingUpdateManager;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.misc.ModuleDisableSequencePackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PendingUpdateManager.class)
public class MixinPendingUpdateManager {

    @Inject(method = "incrementSequence", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/PendingUpdateManager;pendingSequence:Z", shift = At.Shift.BEFORE), cancellable = true)
    public void injectIncrementSequence(CallbackInfoReturnable<PendingUpdateManager> cir) {
        if(!TarasandeMain.Companion.get().getDisabled())
            if(TarasandeMain.Companion.get().getManagerModule().get(ModuleDisableSequencePackets.class).getEnabled())
                cir.setReturnValue((PendingUpdateManager) (Object) this);
    }


}
