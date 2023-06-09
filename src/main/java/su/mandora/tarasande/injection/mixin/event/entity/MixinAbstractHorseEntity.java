package su.mandora.tarasande.injection.mixin.event.entity;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventIsSaddled;

@Mixin(AbstractHorseEntity.class)
public class MixinAbstractHorseEntity {

    @Inject(method = "isSaddled", at = @At("RETURN"), cancellable = true)
    public void hookEventIsSaddled(CallbackInfoReturnable<Boolean> cir) {
        EventIsSaddled eventIsSaddled = new EventIsSaddled(cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventIsSaddled);
        cir.setReturnValue(eventIsSaddled.getSaddled());
    }

}
