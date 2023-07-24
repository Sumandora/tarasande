package su.mandora.tarasande.injection.mixin.event.connection;

import io.netty.channel.ChannelFuture;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventConnectServer;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/ClientConnection;)Lio/netty/channel/ChannelFuture;"))
    public ChannelFuture hookEventConnectServer(InetSocketAddress address, boolean useEpoll, ClientConnection connection) {
        final ChannelFuture channelFuture = ClientConnection.connect(address, useEpoll, connection);
        EventDispatcher.INSTANCE.call(new EventConnectServer(connection));
        return channelFuture;
    }
}
