package de.florianmichael.viacursed.netty;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.netty.decoder.NukkitPacketDecoder;
import de.florianmichael.viacursed.netty.encoder.NukkitPacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.SizePrepender;
import net.minecraft.network.SplitterHandler;
import org.jetbrains.annotations.NotNull;

import java.net.InetSocketAddress;
import java.util.logging.Level;

@SuppressWarnings("Guava")
public class LocalNettyServer {
    public static final Supplier<NioEventLoopGroup> DEFAULT_CHANNEL = Suppliers.memoize(() -> new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    public static final Supplier<EpollEventLoopGroup> EPOLL_CHANNEL = Suppliers.memoize(() -> new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));

    private ChannelFuture future;
    private final InetSocketAddress targetAddress;

    public LocalNettyServer(InetSocketAddress targetAddress) {
        this.targetAddress = targetAddress;
    }

    public void startServer() {
        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Starting local ProxyServer...");

        this.future = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(@NotNull Channel channel) {
                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(5));
                channel.pipeline().addLast("splitter", new SplitterHandler());
                channel.pipeline().addLast("decoder", new NukkitPacketDecoder());
                channel.pipeline().addLast("prepender", new SizePrepender());
                channel.pipeline().addLast("encoder", new NukkitPacketEncoder());
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                super.exceptionCaught(ctx, cause);
                if (cause instanceof ReadTimeoutException) {
                    stopServer();
                }
            }
        }).group(Epoll.isAvailable() ? EPOLL_CHANNEL.get() : DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Started local ProxyServer!");
    }

    public void stopServer() {
        if (future == null) return;

        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Stopping local ProxyServer...");
        this.future.channel().close().syncUninterruptibly();
    }

    public ChannelFuture getFuture() {
        return future;
    }

    public InetSocketAddress getTargetAddress() {
        return targetAddress;
    }
}
