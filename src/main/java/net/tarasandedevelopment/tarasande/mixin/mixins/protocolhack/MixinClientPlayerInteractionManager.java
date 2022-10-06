package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientPlayerEntity_Protocol;
import net.tarasandedevelopment.tarasande.protocol.provider.FabricHandItemProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MixinClientPlayerInteractionManager {

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8) && player instanceof IClientPlayerEntity_Protocol) {
            player.swingHand(Hand.MAIN_HAND);
            ((IClientPlayerEntity_Protocol) player).tarasande_cancelSwingOnce();
        }
    }

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    private void injectHasLimitedAttackSpeed(CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            ci.setReturnValue(false);
    }

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0)))
    private void redirectInteractItem(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
        if (VersionList.isNewerOrEqualTo(VersionList.R1_17))
            clientPlayNetworkHandler.sendPacket(packet);
    }

    @Inject(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0, shift = At.Shift.BEFORE))
    public void injectInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        FabricHandItemProvider.Companion.setLastUsedItem(player.getStackInHand(hand).copy());
    }

    @Inject(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", shift = At.Shift.BEFORE))
    public void injectInteractBlock(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        FabricHandItemProvider.Companion.setLastUsedItem(player.getStackInHand(hand).copy());
    }
}
