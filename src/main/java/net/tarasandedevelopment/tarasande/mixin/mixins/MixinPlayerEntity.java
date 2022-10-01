package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventClipAtLedge;
import net.tarasandedevelopment.tarasande.event.EventKeepSprint;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "clipAtLedge", at = @At("RETURN"), cancellable = true)
    public void injectClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        EventClipAtLedge eventClipAtLedge = new EventClipAtLedge(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventClipAtLedge);
        cir.setReturnValue(eventClipAtLedge.getClipping());
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    public void hookedSetSprinting(PlayerEntity instance, boolean b) {
        EventKeepSprint eventKeepSprint = new EventKeepSprint(b);
        TarasandeMain.Companion.get().getManagerEvent().call(eventKeepSprint);
        if (!eventKeepSprint.getSprinting())
            instance.setSprinting(b);
    }
}
