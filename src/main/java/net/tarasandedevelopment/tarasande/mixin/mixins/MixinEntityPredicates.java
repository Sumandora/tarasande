package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventCanBePushedBy;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("RETURN"), cancellable = true)
    private static void injectCanBePushedBy(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        EventCanBePushedBy eventCanBePushedBy = new EventCanBePushedBy(entity, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventCanBePushedBy);
        cir.setReturnValue(eventCanBePushedBy.getPredicate());
    }

}
