package net.tarasandedevelopment.tarasande.mixin.mixins.core.connection;

import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.screen.clientmenu.clientmenu.ElementMenuToggleHAProxyHack;
import net.tarasandedevelopment.tarasande.util.connection.Proxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/ClientConnection$1")
public class MixinClientConnectionSubInitChannel {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void addChannelHandlers(Channel channel, CallbackInfo ci) {
        final Proxy proxy = TarasandeMain.Companion.get().getProxy();

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

        final ElementMenuToggleHAProxyHack haProxyHack = TarasandeMain.Companion.managerClientMenu().get(ElementMenuToggleHAProxyHack.class);

        if (haProxyHack.getState().getValue()) {
            channel.pipeline().addFirst("haproxy-encoder", HAProxyMessageEncoder.INSTANCE);
            channel.pipeline().addLast(haProxyHack.getHandler());
        }
    }
}
