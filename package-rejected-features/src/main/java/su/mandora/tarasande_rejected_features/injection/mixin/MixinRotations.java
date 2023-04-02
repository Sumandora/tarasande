package su.mandora.tarasande_rejected_features.injection.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import su.mandora.tarasande.event.impl.EventRotation;
import su.mandora.tarasande.feature.rotation.Rotations;
import su.mandora.tarasande.util.math.rotation.Rotation;
import su.mandora.tarasande_rejected_features.tarasandevalues.ClosedInventory;

@Mixin(value = Rotations.class, remap = false)
public class MixinRotations {

    @Shadow
    private static Rotation fakeRotation;

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "createRotationEvent", at = @At(value = "INVOKE", target = "Lsu/mandora/tarasande/event/EventDispatcher;call(Lsu/mandora/tarasande/event/Event;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void injectOldRotation(CallbackInfo ci, Rotation realRotation, EventRotation eventRotation) {
        if(fakeRotation != null && ClosedInventory.INSTANCE.shouldBlock())
            eventRotation.setRotation(fakeRotation);
    }

}
