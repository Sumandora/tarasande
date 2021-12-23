package su.mandora.tarasande.mixin.mixins.connection;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventPacket;
import su.mandora.tarasande.mixin.accessor.IClientConnection;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection {

    @Shadow
    private Channel channel;

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void injectHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        EventPacket eventPacket = new EventPacket(EventPacket.Type.RECEIVE, packet);
        TarasandeMain.Companion.get().getManagerEvent().call(eventPacket);
        if (eventPacket.getCancelled())
            ci.cancel();
    }

    @Inject(method = "sendImmediately", at = @At("HEAD"), cancellable = true)
    public void injectSendImmediately(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> callback, CallbackInfo ci) {
        EventPacket eventPacket = new EventPacket(EventPacket.Type.SEND, packet);
        TarasandeMain.Companion.get().getManagerEvent().call(eventPacket);
        if (eventPacket.getCancelled())
            ci.cancel();
    }

    @Override
    public Channel getChannel() {
        return channel;
    }
}
