package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.connection;

import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleAntiParticleHide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityStatusEffectS2CPacket.class)
public class MixinEntityStatusEffectS2CPacket {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void hookAntiParticleHideHud(CallbackInfoReturnable<Boolean> cir) {
        ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.managerModule().get(ModuleAntiParticleHide.class);
        if (moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getHud().isEnabled() && moduleAntiParticleHide.getHud().getValue())
            cir.setReturnValue(true);
    }

    @Inject(method = "shouldShowParticles", at = @At("HEAD"), cancellable = true)
    public void hookAntiParticleHideInventory(CallbackInfoReturnable<Boolean> cir) {
        ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.managerModule().get(ModuleAntiParticleHide.class);
        if (moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getInventory().getValue())
            cir.setReturnValue(true);
    }
}
