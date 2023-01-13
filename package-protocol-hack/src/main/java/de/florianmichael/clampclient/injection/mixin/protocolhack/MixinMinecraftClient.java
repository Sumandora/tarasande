/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.clampclient.injection.mixin.protocolhack;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import de.florianmichael.clampclient.injection.mixininterface.IMouse_Protocol;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove;
import net.tarasandedevelopment.tarasande_protocol_hack.TarasandeProtocolHack;
import net.tarasandedevelopment.tarasande_protocol_hack.util.values.ProtocolHackValues;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
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

    @Shadow @Final public Mouse mouse;

    @Shadow protected int attackCooldown;

    @Redirect(method = "doItemUse",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;resetEquipProgress(Lnet/minecraft/util/Hand;)V", ordinal = 0))
    private void redirectDoItemUse(HeldItemRenderer heldItemRenderer, Hand hand) {
        if (ViaLoadingBase.getTargetVersion().isNewerThan(VersionListEnum.r1_8) || !(player.getStackInHand(hand).getItem() instanceof SwordItem)) {
            heldItemRenderer.resetEquipProgress(hand);
        }
    }

    @Redirect(method = "doItemUse",
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;interactEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;isAccepted()Z", ordinal = 0))
    private boolean preventGenericInteract(ActionResult instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_7_6tor1_7_10)) {
            return true;
        }

        return instance.isAccepted();
    }

    /**
     * This code removes the cooldown if
     */
    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;attackCooldown:I", ordinal = 1))
    public int unwrapOperation(MinecraftClient instance) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            return 0;
        }
        return attackCooldown;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleInputEvents()V", shift = At.Shift.BEFORE))
    public void updateCooldown(CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_8)) {
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasRidingInventory()Z"))
    private void onInventoryKeyPressed(CallbackInfo ci) throws Exception {
        final UserConnection viaConnection = TarasandeProtocolHack.Companion.getViaConnection();

        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_11_1to1_11_2) && viaConnection != null) {
            if (ManagerModule.INSTANCE.get(ModuleInventoryMove.class).getEnabled().getValue() && TarasandeProtocolHack.Companion.getCancelOpenPacket().getValue()) {
                return;
            }

            final PacketWrapper clientStatus = PacketWrapper.create(ServerboundPackets1_9_3.CLIENT_STATUS, viaConnection);
            clientStatus.write(Type.VAR_INT, 2); // Open Inventory Achievement

            clientStatus.sendToServer(Protocol1_12To1_11_1.class);
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;tick()V", ordinal = 0, shift = At.Shift.BEFORE))
    public void tickMouseEmulationFilter(CallbackInfo ci) {
        if (ProtocolHackValues.INSTANCE.getEmulateMouseInputs().getValue()) {
            ((IMouse_Protocol) this.mouse).protocolhack_getMouseEmulation().tickFilter();
        }
    }
}
