package net.tarasandedevelopment.tarasande.mixin.mixins.event.connection;

import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventConnectServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreenSubConnect {

    @Inject(method = "run", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;",
            shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    public void hookEventConnectServer(CallbackInfo ci, InetSocketAddress inetSocketAddress) {
        EventDispatcher.INSTANCE.call(new EventConnectServer(inetSocketAddress));
    }
}
