package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.netty;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol.BedrockSessionBaseProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("Guava")
public class LimboNettyServer {
    private static final Supplier<NioEventLoopGroup> DEFAULT_CHANNEL = Suppliers.memoize(() -> new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build()));
    private static final Supplier<EpollEventLoopGroup> EPOLL_CHANNEL = Suppliers.memoize(() -> new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build()));

    private ChannelFuture future;

    public void startServer(final InetSocketAddress targetAddress) {
        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(targetAddress, "Starting local Limbo NettyServer...");

        this.future = new ServerBootstrap().channel(LocalServerChannel.class).childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast("decoder", new ByteToMessageDecoder() {
                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {}
                });
                // It's impossible that the Limbo server sends packets, so we don't need an encoder here
            }
        }).group(Epoll.isAvailable() ? EPOLL_CHANNEL.get() : DEFAULT_CHANNEL.get()).localAddress(LocalAddress.ANY).bind().syncUninterruptibly();
        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(targetAddress, "Started local Limbo NettyServer!");
    }

    public void stopServer() {
        if (future == null) return;

        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Stopping local ProxyServer...");
        this.future.channel().close().syncUninterruptibly();
    }

    public ChannelFuture getFuture() {
        return future;
    }
}
