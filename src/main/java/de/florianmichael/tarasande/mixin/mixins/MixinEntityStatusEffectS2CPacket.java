package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.exploit.ModuleAntiParticleHide;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;

@Mixin(EntityStatusEffectS2CPacket.class)
public class MixinEntityStatusEffectS2CPacket {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void hookHudHack(CallbackInfoReturnable<Boolean> cir) {
        ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiParticleHide.class);
        if (moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getHud().getValue())
            cir.setReturnValue(true);
    }

    @Inject(method = "shouldShowParticles", at = @At("HEAD"), cancellable = true)
    public void hookInventoryHack(CallbackInfoReturnable<Boolean> cir) {
        ModuleAntiParticleHide moduleAntiParticleHide = TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiParticleHide.class);
        if (moduleAntiParticleHide.getEnabled() && moduleAntiParticleHide.getInventory().getValue())
            cir.setReturnValue(true);
    }
}
