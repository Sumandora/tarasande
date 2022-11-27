package net.tarasandedevelopment.tarasande.injection.mixin.event.connection;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.tarasandedevelopment.tarasande.util.connection.MessageToMessageDecoderEvent;
import net.tarasandedevelopment.tarasande.util.connection.MessageToMessageEncoderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1", priority = 1001 /* after the protocol hack */)
public class MixinClientConnectionSubOne {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hookEventEncoderAndEventDecoder(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            channel.pipeline()
                    .addFirst("tarasande-encoder", new MessageToMessageEncoderEvent())
                    .addFirst("tarasande-decoder", new MessageToMessageDecoderEvent());
        }
    }
}
