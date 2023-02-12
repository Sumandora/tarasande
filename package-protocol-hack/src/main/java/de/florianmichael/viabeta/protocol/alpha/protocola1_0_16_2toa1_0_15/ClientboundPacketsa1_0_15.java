package de.florianmichael.viabeta.protocol.alpha.protocola1_0_16_2toa1_0_15;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.type.PreNettyTypes;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ClientboundPacketsa1_0_15 implements ClientboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
    }),
    JOIN_GAME(1, (user, buf) -> {
        buf.skipBytes(4);
        PreNettyTypes.readUTF(buf);
        PreNettyTypes.readUTF(buf);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    }),
    PLAYER_POSITION_ONLY_ONGROUND(10, (user, buf) -> {
        buf.skipBytes(1);
    }),
    PLAYER_POSITION_ONLY_POSITION(11, (user, buf) -> {
        buf.skipBytes(33);
    }),
    PLAYER_POSITION_ONLY_LOOK(12, (user, buf) -> {
        buf.skipBytes(9);
    }),
    PLAYER_POSITION(13, (user, buf) -> {
        buf.skipBytes(41);
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.skipBytes(6);
    }),
    ADD_TO_INVENTORY(17, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ENTITY_ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    SPAWN_PLAYER(20, (user, buf) -> {
        buf.skipBytes(4);
        PreNettyTypes.readUTF(buf);
        buf.skipBytes(16);
    }),
    SPAWN_ITEM(21, (user, buf) -> {
        buf.skipBytes(22);
    }),
    COLLECT_ITEM(22, (user, buf) -> {
        buf.skipBytes(8);
    }),
    SPAWN_ENTITY(23, (user, buf) -> {
        buf.skipBytes(17);
    }),
    DESTROY_ENTITIES(29, (user, buf) -> {
        buf.skipBytes(4);
    }),
    ENTITY_MOVEMENT(30, (user, buf) -> {
        buf.skipBytes(4);
    }),
    ENTITY_POSITION(31, (user, buf) -> {
        buf.skipBytes(7);
    }),
    ENTITY_ROTATION(32, (user, buf) -> {
        buf.skipBytes(6);
    }),
    ENTITY_POSITION_AND_ROTATION(33, (user, buf) -> {
        buf.skipBytes(9);
    }),
    ENTITY_TELEPORT(34, (user, buf) -> {
        buf.skipBytes(18);
    }),
    PRE_CHUNK(50, (user, buf) -> {
        buf.skipBytes(9);
    }),
    CHUNK_DATA(51, (user, buf) -> {
        buf.skipBytes(13);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    MULTI_BLOCK_CHANGE(52, (user, buf) -> {
        buf.skipBytes(8);
        short x = buf.readShort();
        for (int i = 0; i < x; i++) buf.readShort();
        for (int i = 0; i < x; i++) buf.readByte();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    BLOCK_CHANGE(53, (user, buf) -> {
        buf.skipBytes(11);
    }),
    DISCONNECT(255, (user, buf) -> {
        PreNettyTypes.readUTF(buf);
    });

    private static final ClientboundPacketsa1_0_15[] REGISTRY = new ClientboundPacketsa1_0_15[256];

    static {
        for (ClientboundPacketsa1_0_15 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPacketsa1_0_15 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPacketsa1_0_15(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
        this.id = id;
        this.packetReader = packetReader;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public BiConsumer<UserConnection, ByteBuf> getPacketReader() {
        return this.packetReader;
    }

}
