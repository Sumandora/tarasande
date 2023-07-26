package su.mandora.tarasande_protocol_spoofer.injection.mixin.forgefaker;

import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande_protocol_spoofer.TarasandeProtocolSpoofer;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerInfo;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IServerMetadata;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.ForgeProtocolSpoofer;
import su.mandora.tarasande_protocol_spoofer.tarasandevalues.forge.payload.IForgePayload;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.network.MultiplayerServerListPinger$1")
public class MixinMultiplayerServerListPinger_1 {

    @Final
    @Shadow
    ServerInfo field_3776;
    @Unique
    private ServerMetadata tarasande_metadata;

    @Redirect(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V"))
    public void trackForgePayload(ClientConnection instance, Packet<?> packet) {
        if (!TarasandeProtocolSpoofer.Companion.getViaFabricPlusLoaded()) return;
        IForgePayload payload = ((IServerMetadata) (Object) tarasande_metadata).tarasande_getForgePayload();

        if (payload != null) {
            ((IServerInfo) field_3776).tarasande_setForgePayload(payload);

            ForgeProtocolSpoofer.INSTANCE.getForgeInfoTracker().put((InetSocketAddress) instance.getAddress(), payload);
        }

        instance.send(packet); // Original Code
    }

    @Redirect(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;metadata()Lnet/minecraft/server/ServerMetadata;"))
    public ServerMetadata trackMetadata(QueryResponseS2CPacket instance) {
        this.tarasande_metadata = instance.metadata();

        return instance.metadata(); // Original Code
    }
}
