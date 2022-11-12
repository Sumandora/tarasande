package net.tarasandedevelopment.tarasande.mixin.mixins.event.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.events.EventDispatcher;
import net.tarasandedevelopment.events.impl.EventKeepSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V"))
    public void hookEventKeepSprint(PlayerEntity instance, boolean b) {
        EventKeepSprint eventKeepSprint = new EventKeepSprint(b);
        EventDispatcher.INSTANCE.call(eventKeepSprint);
        if (!eventKeepSprint.getSprinting())
            instance.setSprinting(b);
    }
}
