package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.movement.ModuleNoCramming;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("RETURN"), cancellable = true)
    private static void injectCanBePushedBy(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleNoCramming moduleNoCramming = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoCramming.class);
            if(moduleNoCramming.getEnabled())
                cir.setReturnValue(o -> moduleNoCramming.getMode().isSelected(0));
        }
    }

}
