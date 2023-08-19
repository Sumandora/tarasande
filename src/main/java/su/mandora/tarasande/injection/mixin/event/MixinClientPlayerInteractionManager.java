package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventAttackEntity;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void hookEventAttackEntityPre(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (player == MinecraftClient.getInstance().player)
            EventDispatcher.INSTANCE.call(new EventAttackEntity(target, EventAttackEntity.State.PRE));
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    public void hookEventAttackEntityPost(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (player == MinecraftClient.getInstance().player)
            EventDispatcher.INSTANCE.call(new EventAttackEntity(target, EventAttackEntity.State.POST));
    }
}
