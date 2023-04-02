package su.mandora.tarasande.injection.mixin.event.connection;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import su.mandora.tarasande.TarasandeMainKt;
import su.mandora.tarasande.util.connection.MessageToMessageDecoderEvent;
import su.mandora.tarasande.util.connection.MessageToMessageEncoderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1", priority = 1001 /* after the protocol hack */)
public class MixinClientConnection_1 {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hookEventEncoderAndEventDecoder(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            channel.pipeline()
                    .addFirst(TarasandeMainKt.TARASANDE_NAME + "-encoder", new MessageToMessageEncoderEvent())
                    .addFirst(TarasandeMainKt.TARASANDE_NAME + "-decoder", new MessageToMessageDecoderEvent());
        }
    }
}
