package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayPingS2CPacket;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {

    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    public abstract void onEntityStatus(EntityStatusS2CPacket packet);

    @Inject(method = "onPing", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
    private void onPing(PlayPingS2CPacket packet, CallbackInfo ci) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_17))
            return;

        final int inventoryId = (packet.getParameter() >> 16) & 0xFF; // Fix Via Bug from 1.16.5 (Window Confirmation -> PlayPing) Usage for MiningFast Detection
        ScreenHandler handler = null;

        if (client.player == null) return;

        if (inventoryId == 0) handler = client.player.playerScreenHandler;
        if (inventoryId == client.player.currentScreenHandler.syncId) handler = client.player.currentScreenHandler;

        if (handler == null) ci.cancel();
    }


    @Inject(method = {"onGameJoin", "onPlayerRespawn"}, at = @At("TAIL"))
    private void injectOnOnGameJoinOrRespawn(CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8)) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            onEntityStatus(new EntityStatusS2CPacket(player, (byte) 28));
        }
    }
}
