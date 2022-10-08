package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventAttackEntity;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    @Shadow
    private float currentBreakingProgress;

    @Shadow
    protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);

    @Inject(method = "attackEntity", at = @At("HEAD"))
    public void injectPreAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventAttackEntity(target, EventAttackEntity.State.PRE));
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    public void injectPostAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventAttackEntity(target, EventAttackEntity.State.POST));
    }

    @Override
    public float tarasande_getCurrentBreakingProgress() {
        return currentBreakingProgress;
    }

    @Override
    public void tarasande_setCurrentBreakingProgress(float currentBreakingProgress) {
        this.currentBreakingProgress = currentBreakingProgress;
    }

    @Override
    public void tarasande_invokeSendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator) {
        sendSequencedPacket(world, packetCreator);
    }
}
