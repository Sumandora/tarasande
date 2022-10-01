package su.mandora.tarasande.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventCanBePushedBy;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("RETURN"), cancellable = true)
    private static void injectIsPushable(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        EventCanBePushedBy eventCanBePushedBy = new EventCanBePushedBy(entity, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventCanBePushedBy);
        cir.setReturnValue(eventCanBePushedBy.getPredicate());
    }

}
