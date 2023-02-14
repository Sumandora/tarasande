package de.florianmichael.tarasande_rejected_features.injection.mixin;

import de.florianmichael.tarasande_rejected_features.tarasandevalues.ClosedInventory;
import net.tarasandedevelopment.tarasande.event.EventRotation;
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = Rotations.class, remap = false)
public class MixinRotations {

    @Shadow
    private static Rotation fakeRotation;

    @Inject(method = "createRotationEvent", at = @At(value = "INVOKE", target = "Lsu/mandora/event/EventDispatcher;call(Lsu/mandora/event/Event;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void injectOldRotation(CallbackInfo ci, Rotation realRotation, EventRotation eventRotation) {
        if(fakeRotation != null && ClosedInventory.INSTANCE.shouldBlock())
            eventRotation.setRotation(fakeRotation);
    }

}
