package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.tarasandevalue.impl.NetworkValues;

@Mixin(value = ClientConnection.class, priority = 1001)
public class MixinClientConnection {

    @Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;disconnect(Lnet/minecraft/text/Text;)V", ordinal = 0))
    public void noNettyTimeout(ClientConnection instance, Text disconnectReason) {
        if (!NetworkValues.INSTANCE.getRemoveNettyExceptionHandling().isSelected(0)) {
            instance.disconnect(disconnectReason);
        }
    }

    @Redirect(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;debug(Ljava/lang/String;Ljava/lang/Throwable;)V", ordinal = 2, remap = false))
    public void cancelWrongPacketHandling(Logger instance, String s, Throwable throwable) {
        if (!NetworkValues.INSTANCE.getRemoveNettyExceptionHandling().isSelected(1)) {
            instance.debug(s, throwable);
        }
    }

    @Inject(method = "exceptionCaught", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    public void printStacktrace(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (NetworkValues.INSTANCE.getPrintExceptionStacktrace().getValue()) {
            ex.printStackTrace();
        }
    }
}
