package de.florianmichael.viabeta.protocol.beta.protocolb1_4_0_1tob1_3_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ClientboundPacketsb1_3 implements ClientboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
    }),
    JOIN_GAME(1, (user, buf) -> {
        buf.skipBytes(4);
        readUTF(buf);
        readUTF(buf);
        buf.skipBytes(9);
    }),
    HANDSHAKE(2, (user, buf) -> {
        readUTF(buf);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readUTF(buf);
    }),
    TIME_UPDATE(4, (user, buf) -> {
        buf.skipBytes(8);
    }),
    ENTITY_EQUIPMENT(5, (user, buf) -> {
        buf.skipBytes(10);
    }),
    SPAWN_POSITION(6, (user, buf) -> {
        buf.skipBytes(12);
    }),
    UPDATE_HEALTH(8, (user, buf) -> {
        buf.skipBytes(2);
    }),
    RESPAWN(9, (user, buf) -> {
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
    USE_BED(17, (user, buf) -> {
        buf.skipBytes(14);
    }),
    ENTITY_ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    SPAWN_PLAYER(20, (user, buf) -> {
        buf.skipBytes(4);
        readUTF(buf);
        buf.skipBytes(16);
    }),
    SPAWN_ITEM(21, (user, buf) -> {
        buf.skipBytes(24);
    }),
    COLLECT_ITEM(22, (user, buf) -> {
        buf.skipBytes(8);
    }),
    SPAWN_ENTITY(23, (user, buf) -> {
        buf.skipBytes(17);
    }),
    SPAWN_MOB(24, (user, buf) -> {
        buf.skipBytes(19);
        readEntityMetadatab1_3(buf);
    }),
    SPAWN_PAINTING(25, (user, buf) -> {
        buf.skipBytes(4);
        readUTF(buf);
        buf.skipBytes(16);
    }),
    ENTITY_VELOCITY(28, (user, buf) -> {
        buf.skipBytes(10);
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
    ENTITY_STATUS(38, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ATTACH_ENTITY(39, (user, buf) -> {
        buf.skipBytes(8);
    }),
    ENTITY_METADATA(40, (user, buf) -> {
        buf.skipBytes(4);
        readEntityMetadatab1_3(buf);
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
    BLOCK_ACTION(54, (user, buf) -> {
        buf.skipBytes(12);
    }),
    EXPLOSION(60, (user, buf) -> {
        buf.skipBytes(28);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) {
            buf.skipBytes(3);
        }
    }),
    OPEN_WINDOW(100, (user, buf) -> {
        buf.skipBytes(2);
        readUTF(buf);
        buf.skipBytes(1);
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.skipBytes(1);
    }),
    SET_SLOT(103, (user, buf) -> {
        buf.skipBytes(3);
        readItemStackb1_2(buf);
    }),
    WINDOW_ITEMS(104, (user, buf) -> {
        buf.skipBytes(1);
        int x = buf.readShort();
        for (int i = 0; i < x; i++) readItemStackb1_2(buf);
    }),
    WINDOW_PROPERTY(105, (user, buf) -> {
        buf.skipBytes(5);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.skipBytes(4);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.skipBytes(10);
        readUTF(buf);
        readUTF(buf);
        readUTF(buf);
        readUTF(buf);
    }),
    DISCONNECT(255, (user, buf) -> {
        readUTF(buf);
    });

    private static final ClientboundPacketsb1_3[] REGISTRY = new ClientboundPacketsb1_3[256];

    static {
        for (ClientboundPacketsb1_3 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPacketsb1_3 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPacketsb1_3(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
