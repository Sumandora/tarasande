package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
    public void noRender_spawnItemParticles(ItemStack stack, int count, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getOverlay().getEatParticles().should() && stack.isFood()) {
            ci.cancel();
        }
    }
}
