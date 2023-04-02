package su.mandora.tarasande.injection.mixin.event.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventFovMultiplier;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {

    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    public void hookEventFovMultiplier(CallbackInfoReturnable<Float> cir) {
        EventFovMultiplier eventFovMultiplier = new EventFovMultiplier(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventFovMultiplier);
        cir.setReturnValue(eventFovMultiplier.getMovementFovMultiplier());
    }

}
