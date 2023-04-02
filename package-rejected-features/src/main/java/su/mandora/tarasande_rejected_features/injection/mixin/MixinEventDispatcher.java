package su.mandora.tarasande_rejected_features.injection.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.Event;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventAttack;
import su.mandora.tarasande_rejected_features.tarasandevalues.ClosedInventory;

@Mixin(value = EventDispatcher.class, remap = false)
public class MixinEventDispatcher {

    @Inject(method = "call", at = @At("HEAD"), cancellable = true)
    public void dontCallEventAttack(Event event, CallbackInfo ci) {
        if(event instanceof EventAttack && ClosedInventory.INSTANCE.shouldBlock())
            ci.cancel();
    }

}
