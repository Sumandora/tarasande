package net.tarasandedevelopment.tarasande_mod_fixes.mixin.clientcommands;

import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "net.earthcomputer.multiconnect.api.MultiConnectAPI", remap = false)
public class MixinMultiConnectAPI {

    @Inject(method = "getProtocolVersion", at = @At("HEAD"), cancellable = true)
    public void fixProtocolVersion(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(TarasandeMain.Companion.protocolHack().getClientsideVersion());
    }
}
