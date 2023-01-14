package net.tarasandedevelopment.tarasande.injection.mixin.event.connection;

import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.event.EventConnectServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.event.EventDispatcher;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;"))
    public ClientConnection hookEventConnectServer(InetSocketAddress address, boolean useEpoll) {
        final ClientConnection connection = ClientConnection.connect(address, useEpoll);
        EventDispatcher.INSTANCE.call(new EventConnectServer(connection));
        return connection;
    }
}
