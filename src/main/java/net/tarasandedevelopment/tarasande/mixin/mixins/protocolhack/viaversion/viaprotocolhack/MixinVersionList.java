package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion.viaprotocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VersionList.class)
public abstract class MixinVersionList {

    @Shadow
    private static boolean isSingleplayer() {
        return false;
    }

    @Inject(method = "isSingleplayer", at = @At("HEAD"), cancellable = true)
    private static void dontCare(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

}
