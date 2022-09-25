package de.enzaxd.viaforge.injection.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import de.enzaxd.viaforge.injection.access.IClientConnection_Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public abstract ClientPlayNetworkHandler getNetworkHandler();

    @Redirect(method = "doItemUse",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V", ordinal = 0))
    private void redirectDoItemUse(HeldItemRenderer heldItemRenderer, Hand hand) {
        if (ProtocolEquals.isNewerTo(VersionList.R1_8) || !(player.getStackInHand(hand).getItem() instanceof SwordItem))
            heldItemRenderer.resetEquipProgress(hand);
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasRidingInventory()Z"))
    private void onInventoryKeyPressed(CallbackInfo ci) throws Exception {
        final UserConnection viaConnection = ((IClientConnection_Protocol) getNetworkHandler().getConnection()).florianMichael_getViaConnection();

        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_11_1) && viaConnection != null) {
            final PacketWrapper clickStatus = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_STATUS, viaConnection);

            clickStatus.write(Type.VAR_INT, 2); // Open Inventory Achievement
            clickStatus.sendToServer(Protocol1_12To1_11_1.class);
        }
    }
}
