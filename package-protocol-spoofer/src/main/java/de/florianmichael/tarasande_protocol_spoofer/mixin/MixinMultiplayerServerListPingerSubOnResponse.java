package de.florianmichael.tarasande_protocol_spoofer.mixin;

import de.florianmichael.tarasande_protocol_spoofer.spoofer.EntrySidebarPanelToggleableForgeFaker;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.server.ServerMetadata;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import de.florianmichael.tarasande_protocol_spoofer.spoofer.forgefaker.payload.IForgePayload;
import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerInfo;
import de.florianmichael.tarasande_protocol_spoofer.accessor.IServerMetadata;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.ScreenExtensionSidebarMultiplayerScreen;
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

            TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionSidebarMultiplayerScreen.class).getSidebar().get(EntrySidebarPanelToggleableForgeFaker.class).getForgeInfoTracker().put((InetSocketAddress) instance.getAddress(), payload);
        }

        instance.send(packet); // Original Code
    }

    @Redirect(method = "onResponse", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/query/QueryResponseS2CPacket;getServerMetadata()Lnet/minecraft/server/ServerMetadata;"))
    public ServerMetadata trackMetadata(QueryResponseS2CPacket instance) {
        this.tarasande_metadata = instance.getServerMetadata();

        return instance.getServerMetadata(); // Original Code
    }
}
