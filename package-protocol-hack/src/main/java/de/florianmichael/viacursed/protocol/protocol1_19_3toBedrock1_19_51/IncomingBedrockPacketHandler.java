package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51;

import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.data.GameType;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.*;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.GameMode;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.baseprotocol.BedrockSessionBaseProtocol;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data.FakeDimensionData;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.model.BedrockPlayer;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.BedrockSessionStorage;
import de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage.EntityTracker_Bedrock_1_19_51;
import io.netty.buffer.Unpooled;

import javax.crypto.SecretKey;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("DataFlowIssue")
public class IncomingBedrockPacketHandler extends StoredObject implements BedrockPacketHandler {

    private final static Map<PlayStatusPacket.Status, String> TRANSLATIONS = new HashMap<>() {{
        put(PlayStatusPacket.Status.LOGIN_FAILED_CLIENT_OLD, "Could not connect: Outdated client!");
        put(PlayStatusPacket.Status.LOGIN_FAILED_SERVER_OLD, "Cloud not connect: Outdated server!");
        put(PlayStatusPacket.Status.LOGIN_FAILED_INVALID_TENANT, "Unable to connect to world. Your school does not have access to this server.");
        put(PlayStatusPacket.Status.LOGIN_FAILED_EDITION_MISMATCH_EDU_TO_VANILLA, "The server is not running Minecraft: Education Edition. Failed to connect.");
        put(PlayStatusPacket.Status.LOGIN_FAILED_EDITION_MISMATCH_VANILLA_TO_EDU, "The server is running Minecraft: Education Edition. Failed to connect.");
        put(PlayStatusPacket.Status.FAILED_SERVER_FULL_SUB_CLIENT, "Wow this server is popular! Check back later to see if spaces opens up. Server Full");
        put(PlayStatusPacket.Status.VANILLA_TO_EDITOR_MISMATCH, "The server is running an incompatible edition of Minecraft. Failed to connect.");
        put(PlayStatusPacket.Status.EDITOR_TO_VANILLA_MISMATCH, "The server is running an incompatible edition of Minecraft. Failed to connect.");
    }};
    private final static String[] DIMENSION_KEYS = new String[] {
            "minecraft:world", "minecraft:the_nether", "minecraft:the_end"
    };

    private final BedrockClientSession bedrockClientSession;
    private BedrockPlayer player;

    public IncomingBedrockPacketHandler(UserConnection user, BedrockClientSession bedrockClientSession) {
        super(user);
        this.bedrockClientSession = bedrockClientSession;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        quit(packet.getKickMessage());
        return true;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        final BedrockSessionStorage bedrockSessionStorage = getUser().get(BedrockSessionStorage.class);
        if (bedrockSessionStorage == null) return false;

        try {
            final SignedJWT saltJwt = SignedJWT.parse(packet.getJwt());
            final URI x5u = saltJwt.getHeader().getX509CertURL();

            final SecretKey sharedKey = EncryptionUtils.getSecretKey(
                    bedrockSessionStorage.loginData.getPrivateKey(),
                    EncryptionUtils.generateKey(x5u.toASCIIString()),
                    Base64.getDecoder().decode(saltJwt.getJWTClaimsSet().getStringClaim("salt"))
            );

            this.bedrockClientSession.enableEncryption(sharedKey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bedrockClientSession.sendPacketImmediately(new ClientToServerHandshakePacket());
        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(bedrockSessionStorage.targetAddress, "Server answered, sending back handshake packet...");
        return true;
    }

    @Override
    public boolean handle(TickSyncPacket packet) {
        final PacketWrapper keepAlive = new PacketWrapperImpl(ClientboundPackets1_19_3.KEEP_ALIVE.getId(), Unpooled.buffer(), getUser());
        keepAlive.write(Type.LONG, packet.getResponseTimestamp());

        try {
            keepAlive.send(Protocol1_19_3toBedrock1_19_51.class);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean handle(PlayStatusPacket packet) {
        if (packet.getStatus() == PlayStatusPacket.Status.LOGIN_SUCCESS) {
            BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(getUser().get(BedrockSessionStorage.class).targetAddress, "Server accepted connection, waiting for start game...");
        } else if (packet.getStatus() == PlayStatusPacket.Status.PLAYER_SPAWN) {
            // TODO | Implement this
            SetLocalPlayerAsInitializedPacket setLocalPlayerAsInitializedPacket = new SetLocalPlayerAsInitializedPacket();
            setLocalPlayerAsInitializedPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            bedrockClientSession.sendPacket(setLocalPlayerAsInitializedPacket);
        } else {
            if (!TRANSLATIONS.containsKey(packet.getStatus())) {
                quit("Quitting");
                return false;
            }
            quit(TRANSLATIONS.get(packet.getStatus()));
        }
        return true;
    }

    @Override
    public boolean handle(StartGamePacket packet) {
        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(getUser().get(BedrockSessionStorage.class).targetAddress, "Received StartGame, sending fake registry to client...");

        this.player = new BedrockPlayer(packet.getRuntimeEntityId(), packet.getUniqueEntityId());
        getUser().get(EntityTracker_Bedrock_1_19_51.class).getEntityMap().put(packet.getRuntimeEntityId(), packet.getUniqueEntityId());

        final PacketWrapper loginSuccess = new PacketWrapperImpl(ClientboundLoginPackets.GAME_PROFILE.getId(), Unpooled.buffer(), getUser());
        loginSuccess.write(Type.UUID, UUID.randomUUID());
        loginSuccess.write(Type.STRING, getUser().getProtocolInfo().getUsername());
        loginSuccess.write(Type.VAR_INT, 0);

        getUser().getProtocolInfo().setState(State.PLAY);

        final GameMode gameMode = translateGamemodeToJE(packet.getPlayerGameType());

        final PacketWrapper joinGame = new PacketWrapperImpl(ClientboundPackets1_19_3.JOIN_GAME.getId(), Unpooled.buffer(), getUser());
        joinGame.write(Type.INT, (int) packet.getRuntimeEntityId());
        joinGame.write(Type.BOOLEAN, false);
        joinGame.write(Type.UNSIGNED_BYTE, (short) gameMode.getId());
        joinGame.write(Type.BYTE, (byte) 0);
        joinGame.write(Type.STRING_ARRAY, DIMENSION_KEYS);
        joinGame.write(Type.NBT, FakeDimensionData.getDimensionRegistry());
        joinGame.write(Type.STRING, getDimensionById(packet.getDimensionId()));
        joinGame.write(Type.STRING, "minecraft:world");
        joinGame.write(Type.LONG, packet.getSeed());
        joinGame.write(Type.VAR_INT, 100);
        joinGame.write(Type.VAR_INT, (packet.getServerChunkTickRange() << 4) * 2);
        joinGame.write(Type.VAR_INT, (packet.getServerChunkTickRange() << 4) * 2);
        joinGame.write(Type.BOOLEAN, false);
        joinGame.write(Type.BOOLEAN, true);
        joinGame.write(Type.BOOLEAN, false);
        joinGame.write(Type.BOOLEAN, false);
        joinGame.write(Type.BOOLEAN, false);

        final PacketWrapper playerSpawnPosition = new PacketWrapperImpl(ClientboundPackets1_19_3.SPAWN_POSITION.getId(), Unpooled.buffer(), getUser());
        playerSpawnPosition.write(Type.POSITION1_14, new Position((int) packet.getPlayerPosition().getX(), (int) packet.getPlayerPosition().getY(), (int) packet.getPlayerPosition().getZ()));
        playerSpawnPosition.write(Type.FLOAT, packet.getRotation().getX());

        try {
            loginSuccess.sendRaw();
            joinGame.sendRaw();
            playerSpawnPosition.sendRaw();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final RequestChunkRadiusPacket requestChunkRadiusPacket = new RequestChunkRadiusPacket();
        requestChunkRadiusPacket.setRadius(128);

        bedrockClientSession.sendPacket(requestChunkRadiusPacket);

        final TickSyncPacket tickSyncPacket = new TickSyncPacket();
        tickSyncPacket.setRequestTimestamp(0);
        tickSyncPacket.setResponseTimestamp(0);

        bedrockClientSession.sendPacket(tickSyncPacket);

        return true;
    }

    @Override
    public boolean handle(ResourcePacksInfoPacket packet) {
        final ClientCacheStatusPacket clientCacheStatusPacket = new ClientCacheStatusPacket();
        clientCacheStatusPacket.setSupported(false);
        final ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.HAVE_ALL_PACKS);

        bedrockClientSession.sendPacketImmediately(clientCacheStatusPacket);
        bedrockClientSession.sendPacketImmediately(resourcePackClientResponsePacket);

        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(getUser().get(BedrockSessionStorage.class).targetAddress, "Received ResourcePack Information, completed all packets");
        return true;
    }

    @Override
    public boolean handle(ResourcePackStackPacket packet) {
        final ResourcePackClientResponsePacket resourcePackClientResponsePacket = new ResourcePackClientResponsePacket();
        resourcePackClientResponsePacket.setStatus(ResourcePackClientResponsePacket.Status.COMPLETED);

        bedrockClientSession.sendPacketImmediately(resourcePackClientResponsePacket);
        BedrockSessionBaseProtocol.INSTANCE.getConnectionProgress().put(getUser().get(BedrockSessionStorage.class).targetAddress, "Received ResourcePack Stack, completed all packets");
        return true;
    }

    private GameMode translateGamemodeToJE(final GameType gameType) {
        String gameModeString = gameType.name();
        if (gameModeString.equals("VIEWER")) {
            return GameMode.SPECTATOR;
        }
        return GameMode.valueOf(gameType.toString());
    }

    private String getDimensionById(final int dimensionId) {
        return dimensionId == 0 ? "minecraft:overworld" : (dimensionId == -1 ? "minecraft:the_nether" : "minecraft:the_end");
    }

    private void quit(final String reason) {
        final PacketWrapper disconnect = new PacketWrapperImpl(getUser().getProtocolInfo().getState() == State.LOGIN ? ClientboundLoginPackets.LOGIN_DISCONNECT.getId() : ClientboundPackets1_19_3.DISCONNECT.getId(), Unpooled.buffer(), getUser());
        disconnect.write(Type.STRING, reason);

        try {
            disconnect.send(Protocol1_19_3toBedrock1_19_51.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        getUser().getChannel().close();
    }
}
