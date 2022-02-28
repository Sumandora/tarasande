package su.mandora.tarasande.mixin.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventKeepSprint;
import su.mandora.tarasande.module.movement.ModuleSafeWalk;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void injectClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleSafeWalk.class).getEnabled())
            cir.setReturnValue(true);
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    public void hookedSetSprinting(PlayerEntity instance, boolean b) {
        EventKeepSprint eventKeepSprint = new EventKeepSprint(b);
        TarasandeMain.Companion.get().getManagerEvent().call(eventKeepSprint);
        if (!eventKeepSprint.getSprinting())
            instance.setSprinting(b);
    }

}
