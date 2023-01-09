package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.netty.LimboNettyServer;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AsciiString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_19_3toBedrock1_19_51 extends AbstractSimpleProtocol {
    public static final Map<InetSocketAddress, LimboNettyServer> LIMBO_NETTY_SERVERS = new HashMap<>();

    public static SocketAddress createLimboServer(final InetSocketAddress targetAddress) {
        final LimboNettyServer limboNettyServer = new LimboNettyServer();
        limboNettyServer.startServer();
        LIMBO_NETTY_SERVERS.put(targetAddress, limboNettyServer);
        return limboNettyServer.getFuture().channel().localAddress();
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final BedrockSessionStorage bedrockSessionStorage = wrapper.user().get(BedrockSessionStorage.class);
                    if (bedrockSessionStorage == null) throw new IllegalStateException("BedrockSessionStorage is null?");

                    bedrockSessionStorage.connect(bedrockClient -> {
                        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Connected fake nukkit client to server");
                        bedrockClient.getSession().setPacketHandler(new IncomingBedrockPacketHandler(wrapper.user()));

                        final LoginPacket loginPacket = new LoginPacket();
                        loginPacket.setProtocolVersion(BedrockSessionStorage.CODEC.getProtocolVersion());
                        loginPacket.setSkinData(new AsciiString(""));
                        loginPacket.setChainData(new AsciiString(""));

                        bedrockClient.getSession().sendPacketImmediately(loginPacket);
                    });
                });
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.getChannel().pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                final BedrockSessionStorage bedrockSessionStorage = connection.get(BedrockSessionStorage.class);
                if (bedrockSessionStorage != null) {
                    LIMBO_NETTY_SERVERS.remove(bedrockSessionStorage.targetAddress).stopServer();
                }

                super.close(ctx, promise);
            }
        });
    }
}
