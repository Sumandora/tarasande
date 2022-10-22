package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection;

import de.florianmichael.viaprotocolhack.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.tarasandedevelopment.tarasande.util.connection.EventDecoder;
import net.tarasandedevelopment.tarasande.util.connection.EventEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/ClientConnection$1", priority = 1001 /* after the protocol hack */)
public class MixinClientConnectionSubOne {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hookEventEnDeCoder(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            channel.pipeline()
                    .addBefore(NettyConstants.HANDLER_ENCODER_NAME, "tarasande-encoder", new EventEncoder())
                    .addBefore(NettyConstants.HANDLER_DECODER_NAME, "tarasande-decoder", new EventDecoder());
        }
    }
}
