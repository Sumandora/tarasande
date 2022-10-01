package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventEntityStatusGUI;

@Mixin(EntityStatusEffectS2CPacket.class)
public class MixinEntityStatusEffectS2CPacket {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void injectShouldShowIcon(CallbackInfoReturnable<Boolean> cir) {
        final EventEntityStatusGUI eventEntityStatusGUI = new EventEntityStatusGUI(EventEntityStatusGUI.Type.ICON, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventEntityStatusGUI);

        cir.setReturnValue(eventEntityStatusGUI.getState());
    }

    @Inject(method = "shouldShowParticles", at = @At("HEAD"), cancellable = true)
    public void injectShouldShowParticles(CallbackInfoReturnable<Boolean> cir) {
        final EventEntityStatusGUI eventEntityStatusGUI = new EventEntityStatusGUI(EventEntityStatusGUI.Type.PARTICLES, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventEntityStatusGUI);

        cir.setReturnValue(eventEntityStatusGUI.getState());
    }
}
