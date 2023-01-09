package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.*;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.Direction;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ServerboundHandshakePackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import de.florianmichael.viacursed.ViaCursed;
import de.florianmichael.viacursed.netty.BedrockConnection;
import de.florianmichael.viacursed.netty.LocalNettyServer;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import io.netty.util.AsciiString;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

/**
 * How it works:
 * 1. ViaCursed starts a local netty server (proxy)
 * 2. the Minecraft client connects with Minecraft code on this server
 * 3. ViaCursed starts a Bedrock client with Nukkit and connects to this server as well.
 * 4. when a packet is read, it passes through the Nukkit library and is routed by ViaCursed to a Java Edition packet, and then via the bypass decoder to ViaVersion
 * 5. when a Java Edition packet is sent, it is remapped with ViaVersion and then sent to the Bedrock server via the Nukkit session
 */
@SuppressWarnings("DataFlowIssue")
public class Protocol1_19_3toBedrock1_19_51 extends AbstractProtocol {

    /**
     * This method starts the local netty server and returns the address of it
     */
    public static SocketAddress mConnect(final InetSocketAddress target) {
        final LocalNettyServer localNettyServer = new LocalNettyServer(target);
        localNettyServer.startServer();
        return localNettyServer.getFuture().channel().localAddress();
    }

    @Override
    public void transform(Direction direction, State state, PacketWrapper packetWrapper) throws Exception {
        super.transform(direction, state, packetWrapper);

        System.out.println(direction + " " + state + " " + packetWrapper.getId());
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final String username = wrapper.read(Type.STRING);
                    final BedrockSessionStorage bedrockSessionStorage = wrapper.user().get(BedrockSessionStorage.class);
                    if (bedrockSessionStorage == null) throw new IllegalStateException("BedrockSession is not connected at all!");

                    ViaCursed.getPlatform().getLogger().log(Level.INFO, "Trying to connect Bedrock Edition");

                    bedrockSessionStorage.connectToBedrock(() -> {
                        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Nukkit-Client connected to local ProxyServer");
                        final BedrockClientSession bedrockClientSession = bedrockSessionStorage.getBedrockConnection().getSession();
                        if (bedrockClientSession.getPacketHandler() instanceof ClientboundPacketHandler) {
                            ((ClientboundPacketHandler) bedrockClientSession.getPacketHandler()).connect(bedrockSessionStorage.getBedrockConnection(), wrapper.user());
                        }

                        JSONObject extraDataJSON = new JSONObject();
                        extraDataJSON.put("displayName", username);
                        extraDataJSON.put("identity", UUID.randomUUID().toString());
                        bedrockSessionStorage.xuid = ThreadLocalRandom.current().nextLong();
                        extraDataJSON.put("XUID", bedrockSessionStorage.xuid);
                        SignedJWT authData = forgeAuthData(wrapper.user(), extraDataJSON);

                        JSONObject skinDataJSON = new JSONObject();
                        skinDataJSON.put("ThirdPartyName", username);
                        skinDataJSON.put("ThirdPartyNameOnly", true);
                        skinDataJSON.put("ServerAddress", "127.0.0.1:19132");
                        skinDataJSON.put("GameVersion", "1.14.60");
                        skinDataJSON.put("LanguageCode", "en_US");
                        skinDataJSON.put("CurrentInputMode", 1);
                        skinDataJSON.put("DefaultInputMode", 1);
                        skinDataJSON.put("UIProfile", 0);
                        skinDataJSON.put("GuiScale", 0);
                        skinDataJSON.put("PlatformOfflineId", "");
                        skinDataJSON.put("PlatformOnlineId", "");
                        skinDataJSON.put("DeviceOS", ThreadLocalRandom.current().nextInt(1, 12));
                        skinDataJSON.put("DeviceModel", "Ubuntu 18.04 LTS");
                        skinDataJSON.put("DeviceId", UUID.randomUUID().toString());
                        skinDataJSON.put("SelfSignedId", UUID.randomUUID().toString());
                        skinDataJSON.put("ClientRandomId", ThreadLocalRandom.current().nextLong() & 0x7fffffff);
                        skinDataJSON.put("PremiumSkin", false);
                        try {
                            skinDataJSON.put("SkinGeometryData", new String(IOUtils.toByteArray(Protocol1_19_3toBedrock1_19_51.class.getResourceAsStream("/assets/viacursed/default_geometry_data.b64")), StandardCharsets.US_ASCII));
                            skinDataJSON.put("SkinData", new String(IOUtils.toByteArray(Protocol1_19_3toBedrock1_19_51.class.getResourceAsStream("/assets/viacursed/default_skindata.b64")), StandardCharsets.US_ASCII));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        skinDataJSON.put("SkinResourcePatch", "ewogICAiZ2VvbWV0cnkiIDogewogICAgICAiYW5pbWF0ZWRfZmFjZSIgOiAiZ2VvbWV0cnkuYW5pbWF0ZWRfZmFjZV9wZXJzb25hLTQwYjFmN2Q1NTY0NDM5MmEtMCIsCiAgICAgICJkZWZhdWx0IiA6ICJnZW9tZXRyeS5wZXJzb25hXzQwYjFmN2Q1NTY0NDM5MmEtMCIKICAgfQp9Cg==");
                        skinDataJSON.put("SkinId", "5eb65f73-af11-448e-82aa-1b7b165316ad.persona-40b1f7d55644392a-0");
                        skinDataJSON.put("ArmSize", "slim");
                        skinDataJSON.put("SkinImageWidth", 128);
                        skinDataJSON.put("SkinImageHeight", 256);
                        skinDataJSON.put("SkinColor", "#f2dbbd");
                        skinDataJSON.put("CapeImageWidth", 0);
                        skinDataJSON.put("CapeImageHeight", 0);
                        skinDataJSON.put("PersonaSkin", true);
                        skinDataJSON.put("AnimatedImageData", new JSONArray());

                        JWSObject skinData = forgeSkinData(wrapper.user(), skinDataJSON);

                        Gson gson = new Gson();
                        JsonObject chainObj = new JsonObject();
                        JsonArray ja = new JsonArray();
                        ja.add(new JsonParser().parse(authData.serialize()));
                        chainObj.add("chain", ja);
                        AsciiString chainData = new AsciiString(gson.toJson(chainObj));

                        LoginPacket login = new LoginPacket();
                        login.setProtocolVersion(BedrockConnection.CODEC.getProtocolVersion());
                        login.setChainData(chainData);
                        login.setSkinData(AsciiString.of(skinData.serialize()));
                        bedrockClientSession.sendPacketImmediately(login);
                        ViaCursed.getPlatform().getLogger().log(Level.INFO, "Sended Login-Packet to server and waiting for response...");
                    });
                });
            }
        });
    }

    private JWSObject forgeSkinData(final UserConnection connection, JSONObject skinData) {
        final KeyPair keyPair = connection.get(BedrockSessionStorage.class).keyPair;

        URI x5u = URI.create(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES384).x509CertURL(x5u).build();
        JWSObject jws = new JWSObject(header, new Payload(skinData));

        try {
            EncryptionUtils.signJwt(jws, (ECPrivateKey) keyPair.getPrivate());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jws;
    }

    private static SignedJWT forgeAuthData(final UserConnection connection, JSONObject extraData) {
        final KeyPair keyPair = connection.get(BedrockSessionStorage.class).keyPair;

        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        URI x5u = URI.create(publicKeyBase64);

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES384).x509CertURL(x5u).build();

        Date nbf = new Date(0);
        Date exp = new Date(Long.MAX_VALUE);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().notBeforeTime(nbf).expirationTime(exp).issueTime(exp)
                .issuer("Mojang").claim("certificateAuthority", true).claim("extraData", extraData)
                .claim("identityPublicKey", publicKeyBase64)
                .claim("randomNonce", ThreadLocalRandom.current().nextLong() & 0x7fffffff).build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        try {
            EncryptionUtils.signJwt(jwt, (ECPrivateKey) keyPair.getPrivate());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwt;
    }

    public BedrockClientSession bedrockSession(final UserConnection connection) {
        if (!connection.has(BedrockSessionStorage.class)) throw new IllegalStateException("BedrockSession is not connected at all!");
        return connection.get(BedrockSessionStorage.class).getBedrockConnection().getSession();
    }
}
