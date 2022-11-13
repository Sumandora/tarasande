package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.text.Text;
import net.tarasandedevelopment.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventDisconnect;
import net.tarasandedevelopment.tarasande.event.EventPacket;
import net.tarasandedevelopment.tarasande.mixin.accessor.IClientConnection;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ClientConnection.class)
public abstract class MixinClientConnection implements IClientConnection {

    @Unique
    private static final ArrayList<Packet<?>> tarasande_forced = new ArrayList<>();
    @Shadow
    @Final
    private NetworkSide side;

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void hookEventPacketReceive(Packet<T> packet, PacketListener listener, CallbackInfo ci) {
        if (listener.getConnection().getSide() == NetworkSide.CLIENTBOUND) {
            EventPacket eventPacket = new EventPacket(EventPacket.Type.RECEIVE, packet);
            EventDispatcher.INSTANCE.call(eventPacket);
            if (eventPacket.getCancelled())
                ci.cancel();
        }
    }

    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    public void hookEventPacketSend(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
        if (!tarasande_forced.contains(packet)) {
            if (side == NetworkSide.CLIENTBOUND) {
                EventPacket eventPacket = new EventPacket(EventPacket.Type.SEND, packet);
                EventDispatcher.INSTANCE.call(eventPacket);
                if (eventPacket.getCancelled())
                    ci.cancel();
            }
        } else
            tarasande_forced.remove(packet);
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    public void hookEventDisconnect(Text disconnectReason, CallbackInfo ci) {
        EventDispatcher.INSTANCE.call(new EventDisconnect());
    }

    @Override
    public void tarasande_addForcePacket(Packet<?> packet) {
        tarasande_forced.add(packet);
    }

    @Override
    public void tarasande_forceSend(Packet<?> packet) {
        tarasande_addForcePacket(packet);
        send(packet);
    }
}
