package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import net.minecraft.client.network.PendingUpdateManager;
import net.tarasandedevelopment.tarasande.features.protocol.platform.ProtocolHackValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PendingUpdateManager.class)
public class MixinPendingUpdateManager {

    @Inject(method = "incrementSequence", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/PendingUpdateManager;pendingSequence:Z", shift = At.Shift.BEFORE), cancellable = true)
    public void injectIncrementSequence(CallbackInfoReturnable<PendingUpdateManager> cir) {
        if (ProtocolHackValues.INSTANCE.getDisableSequencing().getValue())
            cir.setReturnValue((PendingUpdateManager) (Object) this);
    }
}
