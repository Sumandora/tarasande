package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.tarasandedevelopment.tarasande.features.protocol.provider.FabricHandItemProvider;
import net.tarasandedevelopment.tarasande.features.protocol.util.MinecraftViaItemRewriter;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientPlayerEntity_Protocol;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IScreenHandler_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private ItemStack protocolhack_oldCursorStack;

    @Unique
    private List<ItemStack> protocolhack_oldItems;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void injectAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8) && player instanceof IClientPlayerEntity_Protocol) {
            player.swingHand(Hand.MAIN_HAND);
            ((IClientPlayerEntity_Protocol) player).protocolhack_cancelSwingOnce();
        }
    }

    @ModifyVariable(method = "clickSlot", at = @At(value = "STORE"), ordinal = 0)
    private List<ItemStack> captureOldItems(List<ItemStack> oldItems) {
        assert client.player != null;
        protocolhack_oldCursorStack = client.player.currentScreenHandler.getCursorStack().copy();
        return this.protocolhack_oldItems = oldItems;
    }

    // Special Cases
    @Unique
    private boolean shouldEmpty(final SlotActionType type, final int slot) {
        // quick craft always uses empty stack for verification
        if (type == SlotActionType.QUICK_CRAFT) return true;

        // quick move always uses empty stack for verification since 1.12
        if (type == SlotActionType.QUICK_MOVE && VersionList.isNewerTo(VersionList.R1_11_1)) return true;

        // pickup with slot -999 (outside window) to throw items always uses empty stack for verification
        return type == SlotActionType.PICKUP && slot == -999;
    }

    @Redirect(method = "clickSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V"))
    private void modifySlotClickPacket(ClientPlayNetworkHandler instance, Packet<?> packet) {
        try {
            if (VersionList.isOlderOrEqualTo(VersionList.R1_16_5) && packet instanceof ClickSlotC2SPacket clickSlot) {
                ItemStack slotItemBeforeModification;

                if (this.shouldEmpty(clickSlot.getActionType(), clickSlot.getSlot()))
                    slotItemBeforeModification = ItemStack.EMPTY;
                else if (clickSlot.getSlot() < 0 || clickSlot.getSlot() >= protocolhack_oldItems.size())
                    slotItemBeforeModification = protocolhack_oldCursorStack;
                else
                    slotItemBeforeModification = protocolhack_oldItems.get(clickSlot.getSlot());

                final PacketWrapper clickSlotPacket = PacketWrapper.create(ServerboundPackets1_16_2.CLICK_WINDOW, ((IClientConnection_Protocol) client.getNetworkHandler().getConnection()).protocolhack_getViaConnection());

                clickSlotPacket.write(Type.UNSIGNED_BYTE, (short) clickSlot.getSyncId());
                clickSlotPacket.write(Type.SHORT, (short) clickSlot.getSlot());
                clickSlotPacket.write(Type.BYTE, (byte) clickSlot.getButton());
                assert client.player != null;
                clickSlotPacket.write(Type.SHORT, ((IScreenHandler_Protocol) client.player.currentScreenHandler).protocolhack_getAndIncrementLastActionId());
                clickSlotPacket.write(Type.VAR_INT, clickSlot.getActionType().ordinal());
                clickSlotPacket.write(Type.FLAT_VAR_INT_ITEM, MinecraftViaItemRewriter.INSTANCE.minecraftToViaItem(slotItemBeforeModification, VersionList.R1_16.getVersion()));

                clickSlotPacket.sendToServer(Protocol1_17To1_16_4.class);

                protocolhack_oldCursorStack = null;
                protocolhack_oldItems = null;

                return;
            }
        } catch (Exception ignored) {
        }

        instance.sendPacket(packet);
    }

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    private void injectHasLimitedAttackSpeed(CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8)) {
            ci.setReturnValue(false);
        }
    }

    @Redirect(method = "interactItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;)V", ordinal = 0),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;syncSelectedSlot()V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V", ordinal = 0)))
    private void redirectInteractItem(ClientPlayNetworkHandler clientPlayNetworkHandler, Packet<?> packet) {
        if (VersionList.isNewerOrEqualTo(VersionList.R1_17)) {
            clientPlayNetworkHandler.sendPacket(packet);
        }
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
