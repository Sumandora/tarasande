package net.tarasandedevelopment.tarasande.mixin.mixins.features.module;

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
    public void hookDisableSequencePackets(CallbackInfoReturnable<PendingUpdateManager> cir) {
        final ModuleDisableSequencePackets moduleDisableSequencePackets = TarasandeMain.Companion.get().getManagerModule().get(ModuleDisableSequencePackets.class);

        if (moduleDisableSequencePackets.getEnabled() || moduleDisableSequencePackets.isEnabled()) {
            cir.setReturnValue((PendingUpdateManager) (Object) this);
        }
    }
}
