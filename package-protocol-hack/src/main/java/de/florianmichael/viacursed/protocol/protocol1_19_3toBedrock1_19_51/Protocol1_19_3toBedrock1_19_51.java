package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractSimpleProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.authentication.BedrockAuthenticator;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.netty.LimboNettyServer;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AsciiString;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
                    final String username = wrapper.read(Type.STRING);

                    final BedrockSessionStorage bedrockSessionStorage = wrapper.user().get(BedrockSessionStorage.class);
                    if (bedrockSessionStorage == null) throw new IllegalStateException("BedrockSessionStorage is null?");

                    bedrockSessionStorage.connect(bedrockClient -> {
                        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Connected fake nukkit client to server");
                        wrapper.user().put(new IncomingBedrockPacketHandler(wrapper.user(), bedrockClient.getSession()));
                        bedrockClient.getSession().setPacketHandler(wrapper.user().get(IncomingBedrockPacketHandler.class));

                        final LoginPacket loginPacket = new LoginPacket();
                        loginPacket.setProtocolVersion(BedrockSessionStorage.CODEC.getProtocolVersion());
                        try {
                            loginPacket.setChainData(new AsciiString(BedrockAuthenticator.INSTANCE.getOnlineChainData()));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        Gson gson = new Gson();
                        String publicKeyBase64 = Base64.getEncoder().encodeToString(BedrockAuthenticator.INSTANCE.getPublicKey().getEncoded());

                        JsonObject jwtHeader = new JsonObject();
                        jwtHeader.addProperty("alg", "ES384");
                        jwtHeader.addProperty("x5u", publicKeyBase64);

                        JsonObject skinData = new JsonObject();

                        skinData.add("AnimatedImageData", new JsonArray());
                        skinData.addProperty("ArmSize", "");
                        skinData.addProperty("CapeData", "");
                        skinData.addProperty("CapeId", "");
                        skinData.addProperty("CapeImageHeight", 0);
                        skinData.addProperty("CapeImageWidth", 0);
                        skinData.addProperty("CapeOnClassicSkin", false);
                        skinData.addProperty("ClientRandomId", new Random().nextLong());//erm? i hope this works?
                        skinData.addProperty("CurrentInputMode", 1);
                        skinData.addProperty("DefaultInputMode", 1);
                        skinData.addProperty("DeviceId", UUID.randomUUID().toString());
                        skinData.addProperty("DeviceModel", "");
                        skinData.addProperty("DeviceOS", 7);//windows 10?
                        skinData.addProperty("GameVersion", BedrockSessionStorage.CODEC.getMinecraftVersion());
                        skinData.addProperty("GuiScale", 0);
                        skinData.addProperty("LanguageCode", "en_US");
                        skinData.add("PersonaPieces", new JsonArray());
                        skinData.addProperty("PersonaSkin", false);
                        skinData.add("PieceTintColors", new JsonArray());
                        skinData.addProperty("PlatformOfflineId", "");
                        skinData.addProperty("PlatformOnlineId", "");
                        skinData.addProperty("PremiumSkin", false);
                        skinData.addProperty("SelfSignedId", UUID.randomUUID().toString());//erm? i hope this works?
                        skinData.addProperty("ServerAddress", bedrockSessionStorage.targetAddress.getHostString());
                        skinData.addProperty("SkinAnimationData", "");
                        skinData.addProperty("SkinColor", "#0");
                        try {
                            skinData.addProperty("SkinGeometryData", new String(IOUtils.toByteArray(Protocol1_19_3toBedrock1_19_51.class.getResourceAsStream("/assets/viacursed/default_geometry_data.b64")), StandardCharsets.US_ASCII));
                            skinData.addProperty("SkinData", new String(IOUtils.toByteArray(Protocol1_19_3toBedrock1_19_51.class.getResourceAsStream("/assets/viacursed/default_skindata.b64")), StandardCharsets.US_ASCII));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        skinData.addProperty("SkinId", UUID.randomUUID() + ".Custom" + UUID.randomUUID());//ok..? :shrug:
                        skinData.addProperty("SkinImageHeight", 64);
                        skinData.addProperty("SkinImageWidth", 64);
                        skinData.addProperty("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiZGVmYXVsdCIgOiAiZ2VvbWV0cnkuaHVtYW5vaWQuY3VzdG9tIgogICB9Cn0K");//base 64 of course
                        skinData.addProperty("ThirdPartyName", username);
                        skinData.addProperty("ThirdPartyNameOnly", false);
                        skinData.addProperty("UIProfile", 0);

                        final String header = Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(jwtHeader).getBytes());
                        final String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(gson.toJson(skinData).getBytes());
                        try {
                            loginPacket.setSkinData(new AsciiString(header + "." + payload + "." + BedrockAuthenticator.INSTANCE.signBytes((header + "." + payload).getBytes())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

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
                if (bedrockSessionStorage != null) LIMBO_NETTY_SERVERS.remove(bedrockSessionStorage.targetAddress).stopServer();
                super.close(ctx, promise);
            }
        });
    }
}
