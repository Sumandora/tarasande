package su.mandora.tarasande_custom_minecraft.injection.mixin.detailedconnectionstatus;

import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreen_1 {

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AllowedAddressResolver;resolve(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;"))
    public void resolvingSRV(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.RESOLVING_SRV);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;"))
    public void startingNettyConnection(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.STARTING_NETTY_CONNECTION);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;", shift = At.Shift.AFTER))
    public void resolvingIp(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.RESOLVING_IP);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 0))
    public void connecting(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.CONNECTING);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1))
    public void sendingLoginPackets(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.SENDING_LOGIN_PACKETS);
    }

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/packet/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
    public void waitingForResponse(CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.WAITING_FOR_RESPONSE);
    }
}
