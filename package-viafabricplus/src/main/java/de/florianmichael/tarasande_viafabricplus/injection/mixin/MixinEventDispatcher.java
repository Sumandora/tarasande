package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.tarasande_viafabricplus.injection.accessor.IEventScreenInput;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.tarasandedevelopment.tarasande.event.Event;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventScreenInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EventDispatcher.class, remap = false)
public class MixinEventDispatcher {

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "call", at = @At("HEAD"), cancellable = true)
    public void cancelOriginalScreenInputEvent(Event event, CallbackInfo ci) {
        if (event instanceof EventScreenInput && ViaLoadingBase.getClassWrapper().getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            if (((IEventScreenInput) event).getOriginal()) {
                ci.cancel();
            }
        }
    }
}
