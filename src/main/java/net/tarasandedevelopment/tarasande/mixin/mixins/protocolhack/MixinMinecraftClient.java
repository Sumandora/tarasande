package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol;
import net.tarasandedevelopment.tarasande.module.movement.ModuleInventoryMove;
import net.tarasandedevelopment.tarasande.protocol.util.ArmorUpdater1_8_0;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

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
        if (VersionList.isNewerTo(VersionList.R1_8) || !(player.getStackInHand(hand).getItem() instanceof SwordItem))
            heldItemRenderer.resetEquipProgress(hand);
    }

    @Redirect(method = "doItemUse",
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;isAccepted()Z", ordinal = 0))
    private boolean preventGenericInteract(ActionResult instance) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.R1_7_10))
            return true;
        return instance.isAccepted();
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasRidingInventory()Z"))
    private void onInventoryKeyPressed(CallbackInfo ci) throws Exception {
        final UserConnection viaConnection = ((IClientConnection_Protocol) getNetworkHandler().getConnection()).protocolhack_getViaConnection();

        if (VersionList.isOlderOrEqualTo(VersionList.R1_11_1) && viaConnection != null) {
            ModuleInventoryMove moduleInventoryMove = TarasandeMain.Companion.get().getManagerModule().get(ModuleInventoryMove.class);
            if (moduleInventoryMove.isEnabled() && moduleInventoryMove.getCancelOpenPacket().getValue())
                return;

            final PacketWrapper clickStatus = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_STATUS, viaConnection);

            clickStatus.write(Type.VAR_INT, 2); // Open Inventory Achievement
            clickStatus.sendToServer(Protocol1_12To1_11_1.class);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void injectTick(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8)) {
            ArmorUpdater1_8_0.INSTANCE.update(); // Fixes Armor HUD
        }
    }
}
