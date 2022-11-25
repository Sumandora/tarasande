package net.tarasandedevelopment.tarasande.injection.mixin.core.forgefaker;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.injection.accessor.forgefaker.IServerInfo;
import net.tarasandedevelopment.tarasande.injection.accessor.forgefaker.IServerMetadata;
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.MultiplayerFeatureToggleableExploitsForgeFaker;
import net.tarasandedevelopment.tarasande.system.feature.multiplayerfeaturesystem.impl.forgefaker.payload.IForgePayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public class MixinMultiplayerServerListPingerSubOnResponse {

    @Final
    @Shadow
    ServerInfo field_3776;
    @Unique
    private ServerMetadata tarasande_metadata;

    @Redirect(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;)V"))
    public void trackForgePayload(ClientConnection instance, Packet<?> packet) {
        final IForgePayload payload = ((IServerMetadata) tarasande_metadata).tarasande_getForgePayload();

        if (payload != null) {
            ((IServerInfo) field_3776).tarasande_setForgePayload(payload);

            TarasandeMain.Companion.managerMultiplayerFeature().get(MultiplayerFeatureToggleableExploitsForgeFaker.class).getForgeInfoTracker().put((InetSocketAddress) instance.getAddress(), payload);
        }

        instance.send(packet); // Original Code
    }

    @Redirect(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"))
    public ServerMetadata trackMetadata(QueryResponseS2CPacket instance) {
        this.tarasande_metadata = instance.getServerMetadata();

        return instance.getServerMetadata(); // Original Code
    }
}
