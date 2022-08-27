package su.mandora.tarasande.mixin.mixins.connection;

import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventPacket;
import su.mandora.tarasande.mixin.accessor.IClientConnection;

import java.util.ArrayList;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection {

    private static final ArrayList<Packet<?>> forced = new ArrayList<>();
    @Shadow
    private Channel channel;

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void injectHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        EventPacket eventPacket = new EventPacket(EventPacket.Type.RECEIVE, packet);
        TarasandeMain.Companion.get().getManagerEvent().call(eventPacket);
        if (eventPacket.getCancelled())
            ci.cancel();
    }

    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    public void injectSend(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        if (!forced.contains(packet)) {
            EventPacket eventPacket = new EventPacket(EventPacket.Type.SEND, packet);
            TarasandeMain.Companion.get().getManagerEvent().call(eventPacket);
            if (eventPacket.getCancelled())
                ci.cancel();
        } else
            forced.remove(packet);
    }

    @Override
    public Channel tarasande_getChannel() {
        return channel;
    }

    @Override
    public void tarasande_forceSend(Packet<?> packet) {
        forced.add(packet);
        send(packet);
    }
}
