package net.tarasandedevelopment.tarasande_protocol_hack.injection.mixin.tarasande;

import net.tarasandedevelopment.tarasande.event.EventBlockCollision;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/tarasandedevelopment/tarasande/system/feature/modulesystem/impl/movement/ModuleNoWeb$1", remap = false)
public class MixinModuleNoWebEventBlockCollision {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "accept(Lnet/tarasandedevelopment/tarasande/event/EventBlockCollision;)V", at = @At("HEAD"), cancellable = true)
    public void useOwnImplementation(EventBlockCollision eventBlockCollision, CallbackInfo ci) {
        ci.cancel();
    }

}
