package net.tarasandedevelopment.tarasande.mixin.mixins.forgefaker;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.IServerInfo;
import net.tarasandedevelopment.tarasande.mixin.accessor.IServerMetadata;
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuToggleForgeFaker;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.payload.IForgePayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public class MixinMultiplayerServerListPingerSubOnResponse {

    @Unique
    private ServerMetadata metadata;

    @Final
    @Shadow
    ServerInfo field_3776;

    @Redirect(method = "onResponse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V"))
    public void injectOnResponse(ClientConnection instance, Packet<?> packet) {
        final IForgePayload payload = ((IServerMetadata) metadata).getForgePayload();

        if (payload != null) {
            ((IServerInfo) field_3776).setForgePayload(payload);

            TarasandeMain.Companion.get().getManagerClientMenu().get(ElementMenuToggleForgeFaker.class).getForgeInfoTracker().put((InetSocketAddress) instance.getAddress(), payload);
        }

        instance.send(packet); // Original Code
    }

    @Redirect(method = "onResponse", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"))
    public ServerMetadata redirectOnResponse_Data(QueryResponseS2CPacket instance) {
        this.metadata = instance.getServerMetadata();

        return instance.getServerMetadata(); // Original Code
    }
}
