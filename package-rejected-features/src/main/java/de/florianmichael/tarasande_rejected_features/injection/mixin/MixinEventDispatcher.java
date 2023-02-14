package de.florianmichael.tarasande_rejected_features.injection.mixin;

import de.florianmichael.tarasande_rejected_features.tarasandevalues.ClosedInventory;
import net.tarasandedevelopment.tarasande.event.EventAttack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.Event;
import su.mandora.event.EventDispatcher;

@Mixin(value = EventDispatcher.class, remap = false)
public class MixinEventDispatcher {

    @Inject(method = "call", at = @At("HEAD"), cancellable = true)
    public void dontCallEventAttack(Event event, CallbackInfo ci) {
        if(event instanceof EventAttack && ClosedInventory.INSTANCE.shouldBlock())
            ci.cancel();
    }

}
