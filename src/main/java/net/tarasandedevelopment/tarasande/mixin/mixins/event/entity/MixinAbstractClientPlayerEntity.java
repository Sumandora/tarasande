package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.tarasandedevelopment.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.events.EventMovementFovMultiplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {

    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    public void hookEventMovementFovMultiplier(CallbackInfoReturnable<Float> cir) {
        EventMovementFovMultiplier eventMovementFovMultiplier = new EventMovementFovMultiplier(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventMovementFovMultiplier);
        cir.setReturnValue(eventMovementFovMultiplier.getMovementFovMultiplier());
    }
}
