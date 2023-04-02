package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleNoCramming;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleNoCramming;

import java.util.function.Predicate;

@Mixin(EntityPredicates.class)
public class MixinEntityPredicates {

    @Inject(method = "canBePushedBy", at = @At("RETURN"), cancellable = true)
    private static void hookNoCramming(Entity entity, CallbackInfoReturnable<Predicate<Entity>> cir) {
        if (entity == MinecraftClient.getInstance().player) {
            ModuleNoCramming moduleNoCramming = ManagerModule.INSTANCE.get(ModuleNoCramming.class);
            if (moduleNoCramming.getEnabled().getValue())
                cir.setReturnValue(o -> moduleNoCramming.getMode().isSelected(0));
        }
    }

}
