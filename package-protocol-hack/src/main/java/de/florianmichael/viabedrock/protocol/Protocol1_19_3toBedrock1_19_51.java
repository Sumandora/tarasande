package de.florianmichael.viabedrock.protocol;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import de.florianmichael.viabedrock.ViaBedrock;
import de.florianmichael.viabedrock.api.BedrockProtocols;
import de.florianmichael.viabedrock.api.auth.AuthUtils;
import de.florianmichael.viabedrock.api.provider.AuthDataProvider;
import de.florianmichael.viabedrock.api.provider.SkinSettingsProvider;
import de.florianmichael.viabedrock.baseprotocol.BedrockSessionBaseProtocol;
import de.florianmichael.viabedrock.netty.LimboNettyServer;
import de.florianmichael.viabedrock.protocol.storage.BedrockSessionStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AsciiString;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
                        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(bedrockSessionStorage.getTargetAddress(), "Connected fake nukkit client to server");
                        wrapper.user().put(new IncomingBedrockPacketHandler(wrapper.user(), bedrockClient.getSession()));
                        bedrockClient.getSession().setPacketHandler(wrapper.user().get(IncomingBedrockPacketHandler.class));

                        final AuthDataProvider authDataProvider = wrapper.user().get(AuthDataProvider.class);

                        final LoginPacket loginPacket = new LoginPacket();
                        loginPacket.setProtocolVersion(BedrockProtocols.CODEC.getProtocolVersion());

                        if (authDataProvider != null) {
                            // Online Mode login via token
                            loginPacket.setChainData(new AsciiString(authDataProvider.getChainData()));
                        } else {
                            // Cracked login without any token
                            final KeyPair keyPair = EncryptionUtils.createKeyPair();
                            try {
                                wrapper.user().put(new AuthDataProvider(wrapper.user(), username,
                                        AuthUtils.getOfflineChainData(username, (ECPrivateKey) keyPair.getPrivate(), (ECPublicKey) keyPair.getPublic()),
                                        String.valueOf(ThreadLocalRandom.current().nextLong()),
                                        keyPair, input -> AuthUtils.signBytes((ECPrivateKey) keyPair.getPrivate(), input)));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                loginPacket.setChainData(new AsciiString(AuthUtils.getOfflineChainData(username, (ECPrivateKey) keyPair.getPrivate(), (ECPublicKey) keyPair.getPublic())));
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }

                        final String publicKeyBase64 = Base64.getEncoder().encodeToString(wrapper.user().get(AuthDataProvider.class).getKeyPair().getPublic().getEncoded());
                        final JsonObject jwtHeader = new JsonObject();
                        jwtHeader.addProperty("alg", "ES384");
                        jwtHeader.addProperty("x5u", publicKeyBase64);

                        final SkinSettingsProvider skinSettingsProvider = Via.getManager().getProviders().get(SkinSettingsProvider.class);

                        final String header = Base64.getUrlEncoder().withoutPadding().encodeToString(BEDROCKED_GSON.toJson(jwtHeader).getBytes());
                        final String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(BEDROCKED_GSON.toJson(skinSettingsProvider.generateSkinTileData(wrapper.user())).getBytes());

                        try {
                            loginPacket.setSkinData(new AsciiString(header + "." + payload + "." + wrapper.user().get(AuthDataProvider.class).getSigner().signBytes((header + "." + payload).getBytes())));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        bedrockClient.getSession().sendPacketImmediately(loginPacket);

                        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(bedrockSessionStorage.getTargetAddress(), "Sent login request...");
                    });
                });
            }
        });
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.register(SkinSettingsProvider.class, new SkinSettingsProvider());
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.getChannel().pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                final BedrockSessionStorage bedrockSessionStorage = connection.get(BedrockSessionStorage.class);
                if (bedrockSessionStorage != null) {
                    LIMBO_NETTY_SERVERS.remove(bedrockSessionStorage.getTargetAddress()).stopServer();
                    if (bedrockSessionStorage.getBedrockClient().getRakNet().isRunning()) {
                        bedrockSessionStorage.getBedrockClient().close();
                        ViaBedrock.getPlatform().getLogger().info("Fake nukkit client was still connected, disconnected now =)");
                    }
                }
                super.close(ctx, promise);
            }
        });
    }
}
