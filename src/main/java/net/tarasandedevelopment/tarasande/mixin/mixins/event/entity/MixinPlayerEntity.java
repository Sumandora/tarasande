package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventKeepSprint;
import net.tarasandedevelopment.tarasande.event.EventTagName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    public void hookEventKeepSprint(PlayerEntity instance, boolean b) {
        EventKeepSprint eventKeepSprint = new EventKeepSprint(b);
        EventDispatcher.INSTANCE.call(eventKeepSprint);
        if (!eventKeepSprint.getSprinting())
            instance.setSprinting(b);
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    public void hookEventTagName(CallbackInfoReturnable<Text> cir) {
        EventTagName eventTagName = new EventTagName((PlayerEntity) (Object) this, cir.getReturnValue());
        EventDispatcher.INSTANCE.call(eventTagName);
        cir.setReturnValue(eventTagName.getDisplayName());
    }

}
