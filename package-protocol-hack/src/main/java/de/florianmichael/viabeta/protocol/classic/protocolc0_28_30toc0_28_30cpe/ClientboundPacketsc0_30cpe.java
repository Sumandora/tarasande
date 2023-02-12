package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ClientboundPacketsc0_30cpe implements ClientboundPacketType, PreNettyPacketType {

    JOIN_GAME(0, (user, buf) -> {
        buf.skipBytes(130);
    }),
    KEEP_ALIVE(1, (user, buf) -> {
    }),
    LEVEL_INIT(2, (user, buf) -> {
    }),
    LEVEL_DATA(3, (user, buf) -> {
        buf.skipBytes(1027);
    }),
    LEVEL_FINALIZE(4, (user, buf) -> {
        buf.skipBytes(6);
    }),
    BLOCK_CHANGE(6, (user, buf) -> {
        buf.skipBytes(7);
    }),
    SPAWN_PLAYER(7, (user, buf) -> {
        buf.skipBytes(73);
    }),
    ENTITY_TELEPORT(8, (user, buf) -> {
        buf.skipBytes(9);
    }),
    ENTITY_POSITION_AND_ROTATION(9, (user, buf) -> {
        buf.skipBytes(6);
    }),
    ENTITY_POSITION(10, (user, buf) -> {
        buf.skipBytes(4);
    }),
    ENTITY_ROTATION(11, (user, buf) -> {
        buf.skipBytes(3);
    }),
    DESTROY_ENTITIES(12, (user, buf) -> {
        buf.skipBytes(1);
    }),
    CHAT_MESSAGE(13, (user, buf) -> {
        buf.skipBytes(65);
    }),
    DISCONNECT(14, (user, buf) -> {
        buf.skipBytes(64);
    }),
    OP_LEVEL_UPDATE(15, (user, buf) -> {
        buf.skipBytes(1);
    }),
    EXTENSION_PROTOCOL_INFO(16, (user, buf) -> {
        buf.skipBytes(66);
    }),
    EXTENSION_PROTOCOL_ENTRY(17, (user, buf) -> {
        buf.skipBytes(68);
    }),
    EXT_CUSTOM_BLOCKS_SUPPORT_LEVEL(19, (user, buf) -> {
        buf.skipBytes(1);
    }),
    EXT_SET_BLOCK_PERMISSION(28, (user, buf) -> {
        buf.skipBytes(3);
    }),
    EXT_HACK_CONTROL(32, (user, buf) -> {
        buf.skipBytes(7);
    }),
    EXT_BULK_BLOCK_UPDATE(38, (user, buf) -> {
        buf.skipBytes(1281);
    }),
    EXT_TWO_WAY_PING(43, (user, buf) -> {
        buf.skipBytes(3);
    }),
    EXT_WEATHER_TYPE(31, (user, buf) -> {
        buf.readByte();
    }),
    EXT_SET_SPAWN_POINT(46, (user, buf) -> {
        buf.skipBytes(8);
    });

    private static final ClientboundPacketsc0_30cpe[] REGISTRY = new ClientboundPacketsc0_30cpe[256];

    static {
        for (ClientboundPacketsc0_30cpe packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPacketsc0_30cpe getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPacketsc0_30cpe(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
