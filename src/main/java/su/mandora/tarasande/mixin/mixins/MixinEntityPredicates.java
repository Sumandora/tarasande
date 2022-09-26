package su.mandora.tarasande.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.module.movement.ModuleNoCramming;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("HEAD"), cancellable = true)
    private static void injectIsPushable(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        ModuleNoCramming moduleNoCramming = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoCramming.class);
        if (moduleNoCramming.getEnabled())
            cir.setReturnValue(ignored -> moduleNoCramming.getMode().isSelected(0)); // mode == "enabled"
    }

}
