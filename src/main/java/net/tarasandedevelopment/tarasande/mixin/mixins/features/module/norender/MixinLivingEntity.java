package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "spawnItemParticles", at = @At("HEAD"), cancellable = true)
    public void noRender_spawnItemParticles(ItemStack stack, int count, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getModuleSystem().get(ModuleNoRender.class).getOverlay().getEatParticles().should() && stack.isFood()) {
            ci.cancel();
        }
    }
}
