package net.tarasandedevelopment.tarasande.mixin.mixins.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventKeepSprint;
import net.tarasandedevelopment.tarasande.module.movement.ModuleSafeWalk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void injectClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleSafeWalk moduleSafeWalk = TarasandeMain.Companion.get().getManagerModule().get(ModuleSafeWalk.class);
            if(moduleSafeWalk.getEnabled() && !moduleSafeWalk.getSneak().getValue())
                cir.setReturnValue(true);
        }
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    public void hookedSetSprinting(PlayerEntity instance, boolean b) {
        EventKeepSprint eventKeepSprint = new EventKeepSprint(b);
        TarasandeMain.Companion.get().getEventDispatcher().call(eventKeepSprint);
        if (!eventKeepSprint.getSprinting())
            instance.setSprinting(b);
    }
}
