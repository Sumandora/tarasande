package net.tarasandedevelopment.tarasande.injection.mixin.core.connection;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "exceptionCaught", at = @At("HEAD"))
    public void printException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        ex.printStackTrace();
    }
}
