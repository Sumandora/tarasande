package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol.BedrockSessionBaseProtocol;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data.SkinDataGenerator;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.netty.LimboNettyServer;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.provider.OnlineModeAuthProvider;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.EntityTracker_Bedrock_1_19_51;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AsciiString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_19_3toBedrock1_19_51 extends AbstractSimpleProtocol {
    public static final Map<InetSocketAddress, LimboNettyServer> LIMBO_NETTY_SERVERS = new HashMap<>();
    public static final Gson BEDROCKED_GSON = new Gson();

    public static SocketAddress createLimboServer(final InetSocketAddress targetAddress) {
        final LimboNettyServer limboNettyServer = new LimboNettyServer();
        limboNettyServer.startServer(targetAddress);
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
                    final String username = wrapper.read(Type.STRING);
                    wrapper.user().getProtocolInfo().setUsername(username);

                    final BedrockSessionStorage bedrockSessionStorage = wrapper.user().get(BedrockSessionStorage.class);
                    if (bedrockSessionStorage == null) throw new IllegalStateException("BedrockSessionStorage is null?");

                    bedrockSessionStorage.connect(bedrockClient -> {
                        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(bedrockSessionStorage.targetAddress, "Connected fake nukkit client to server");
                        wrapper.user().put(new IncomingBedrockPacketHandler(wrapper.user(), bedrockClient.getSession()));
                        bedrockClient.getSession().setPacketHandler(wrapper.user().get(IncomingBedrockPacketHandler.class));

                        final String authToken = Via.getManager().getProviders().get(OnlineModeAuthProvider.class).getAuthToken();

                        final LoginPacket loginPacket = new LoginPacket();
                        loginPacket.setProtocolVersion(BedrockSessionStorage.CODEC.getProtocolVersion());

                        if (authToken != null) {
                            // Online Mode login via token
                            loginPacket.setChainData(new AsciiString(bedrockSessionStorage.loginData.getOnlineChainData(authToken)));
                        } else {
                            // Cracked login without any token
                            loginPacket.setChainData(new AsciiString(bedrockSessionStorage.loginData.getOfflineChainData(wrapper.user().getProtocolInfo().getUsername())));
                        }

                        final String publicKeyBase64 = Base64.getEncoder().encodeToString(bedrockSessionStorage.loginData.getPublicKey().getEncoded());
                        final JsonObject jwtHeader = new JsonObject();
                        jwtHeader.addProperty("alg", "ES384");
                        jwtHeader.addProperty("x5u", publicKeyBase64);

                        final String header = Base64.getUrlEncoder().withoutPadding().encodeToString(BEDROCKED_GSON.toJson(jwtHeader).getBytes());
                        final String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(BEDROCKED_GSON.toJson(SkinDataGenerator.generateSkinTileData(wrapper.user())).getBytes());

                        loginPacket.setSkinData(new AsciiString(header + "." + payload + "." + bedrockSessionStorage.loginData.signBytes((header + "." + payload).getBytes())));
                        bedrockClient.getSession().sendPacketImmediately(loginPacket);

                        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(bedrockSessionStorage.targetAddress, "Sent login request...");
                    });
                });
            }
        });
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.require(OnlineModeAuthProvider.class);
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new EntityTracker_Bedrock_1_19_51(connection));

        connection.getChannel().pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                final BedrockSessionStorage bedrockSessionStorage = connection.get(BedrockSessionStorage.class);
                if (bedrockSessionStorage != null) {
                    LIMBO_NETTY_SERVERS.remove(bedrockSessionStorage.targetAddress).stopServer();
                    if (bedrockSessionStorage.bedrockClient.getRakNet().isRunning()) {
                        bedrockSessionStorage.bedrockClient.close();
                        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Fake nukkit client was still connected, disconnected now =)");
                    }
                }
                super.close(ctx, promise);
            }
        });
    }
}
