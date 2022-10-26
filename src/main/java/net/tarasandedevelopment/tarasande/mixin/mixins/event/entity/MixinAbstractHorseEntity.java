package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.entity.passive.AbstractHorseEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventIsSaddled;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorseEntity.class)
public class MixinAbstractHorseEntity {

    @Inject(method = "isSaddled", at = @At("RETURN"), cancellable = true)
    public void hookEventIsSaddled(CallbackInfoReturnable<Boolean> cir) {
        EventIsSaddled eventIsSaddled = new EventIsSaddled(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventIsSaddled);
        cir.setReturnValue(eventIsSaddled.getSaddled());
    }

}
