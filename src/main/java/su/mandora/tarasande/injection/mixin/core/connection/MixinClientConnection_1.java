package su.mandora.tarasande.injection.mixin.core.connection;

import io.netty.channel.Channel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension;
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen;
import su.mandora.tarasande.util.connection.Proxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void addChannelHandlers(Channel channel, CallbackInfo ci) {
        final Proxy proxy = ManagerScreenExtension.INSTANCE.get(ScreenExtensionButtonListMultiplayerScreen.class).getScreenBetterSlotListAccountManager().getScreenBetterProxy().getProxy();

        if (proxy != null) {
            switch (proxy.getType()) {
                case HTTP -> channel.pipeline().addFirst("http-proxy-handler",
                        new HttpProxyHandler(
                                proxy.getSocketAddress(),
                                proxy.getProxyAuthentication() != null ? proxy.getProxyAuthentication().getUsername() : "",
                                proxy.getProxyAuthentication() != null && proxy.getProxyAuthentication().getPassword() != null ? proxy.getProxyAuthentication().getPassword() : ""
                        )
                );
                case SOCKS4 -> channel.pipeline().addFirst("socks4-proxy-handler",
                        new Socks4ProxyHandler(
                                proxy.getSocketAddress(),
                                proxy.getProxyAuthentication() != null ? proxy.getProxyAuthentication().getUsername() : null
                        )
                );
                case SOCKS5 -> channel.pipeline().addFirst("socks5-proxy-handler",
                        new Socks5ProxyHandler(
                                proxy.getSocketAddress(),
                                proxy.getProxyAuthentication() != null ? proxy.getProxyAuthentication().getUsername() : null,
                                proxy.getProxyAuthentication() != null ? proxy.getProxyAuthentication().getPassword() : null
                        )
                );
            }
        }
    }
}
