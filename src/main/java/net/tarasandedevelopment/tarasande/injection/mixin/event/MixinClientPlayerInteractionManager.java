package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventAttackEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void hookEventAttackEntityPre(PlayerEntity player, Entity target, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventAttackEntity(target, EventAttackEntity.State.PRE));
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    public void hookEventAttackEntityPost(PlayerEntity player, Entity target, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventAttackEntity(target, EventAttackEntity.State.POST));
    }
}
