package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.connection;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.features.module.misc.ModuleAntiPacketKick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 1001)
public class MixinClientConnection {

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    public void printException(ChannelHandlerContext context, Throwable ex, CallbackInfo ci) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiPacketKick.class).getEnabled()) {
            ci.cancel();
        }
    }
}
