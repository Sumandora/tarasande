package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventAttackEntity;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientPlayerInteractionManager;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager implements IClientPlayerInteractionManager {

    boolean onlyPackets = false;

    @Shadow
    protected abstract void syncSelectedSlot();

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

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"))
    public void hookedSyncSelectedSlot(ClientPlayerInteractionManager instance) {
        if (!onlyPackets)
            syncSelectedSlot();
    }

    @Override
    public void tarasande_setOnlyPackets(boolean onlyPackets) {
        this.onlyPackets = onlyPackets;
    }

    @Override
    public boolean tarasande_getOnlyPackets() {
        return onlyPackets;
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
