package net.tarasandedevelopment.tarasande.mixin.mixins.entity;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventMovementFovMultiplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class MixinAbstractClientPlayerEntity {

    @Inject(method = "getFovMultiplier", at = @At("RETURN"), cancellable = true)
    public void injectUpdateMovementFovMultiplier(CallbackInfoReturnable<Float> cir) {
        EventMovementFovMultiplier eventMovementFovMultiplier = new EventMovementFovMultiplier(cir.getReturnValue());
        TarasandeMain.Companion.get().getEventDispatcher().call(eventMovementFovMultiplier);
        cir.setReturnValue(eventMovementFovMultiplier.getMovementFovMultiplier());
    }
}
