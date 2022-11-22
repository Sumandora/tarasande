package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.entity;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement.ModuleNoCramming;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("RETURN"), cancellable = true)
    private static void hookNoCramming(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        ModuleNoCramming moduleNoCramming = TarasandeMain.Companion.managerModule().get(ModuleNoCramming.class);
        if (moduleNoCramming.getEnabled())
            cir.setReturnValue(o -> moduleNoCramming.getMode().isSelected(0));
    }

}
