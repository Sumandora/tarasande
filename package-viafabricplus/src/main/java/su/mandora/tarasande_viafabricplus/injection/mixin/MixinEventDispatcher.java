package su.mandora.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.Event;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventScreenInput;
import su.mandora.tarasande_viafabricplus.injection.accessor.IEventScreenInput;

@Mixin(value = EventDispatcher.class, remap = false)
public class MixinEventDispatcher {

    @Inject(method = "call", at = @At("HEAD"), cancellable = true)
    public void cancelOriginalScreenInputEvent(Event event, CallbackInfo ci) {
        if (event instanceof EventScreenInput && DebugSettings.INSTANCE.executeInputsInSync.getValue()) {
            if (((IEventScreenInput) event).tarasande_getOriginal()) {
                ci.cancel();
            }
        }
    }
}
