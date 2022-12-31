package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 1001)
public class MixinClientConnection {

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    public void printException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (TarasandeMain.Companion.clientValues().getDontDisconnectOnNettyTimeout().getValue()) {
            ci.cancel();
        }
    }
}
