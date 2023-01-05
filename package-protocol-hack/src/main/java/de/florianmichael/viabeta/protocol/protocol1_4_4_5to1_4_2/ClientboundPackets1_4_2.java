package de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ClientboundPackets1_4_2 implements ClientboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.readInt();
    }),
    JOIN_GAME(1, (user, buf) -> {
        buf.readInt();
        readString(buf);
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readString(buf);
    }),
    TIME_UPDATE(4, (user, buf) -> {
        buf.readLong();
        buf.readLong();
    }),
    ENTITY_EQUIPMENT(5, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        readItemStack1_3_1(buf);
    }),
    SPAWN_POSITION(6, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readInt();
    }),
    UPDATE_HEALTH(8, (user, buf) -> {
        buf.readShort();
        buf.readShort();
        buf.readFloat();
    }),
    RESPAWN(9, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readShort();
        readString(buf);
    }),
    PLAYER_POSITION_ONLY_ONGROUND(10, (user, buf) -> {
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION_ONLY_POSITION(11, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION_ONLY_LOOK(12, (user, buf) -> {
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION(13, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    USE_BED(17, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readInt();
        buf.readByte();
        buf.readInt();
    }),
    ENTITY_ANIMATION(18, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    SPAWN_PLAYER(20, (user, buf) -> {
        buf.readInt();
        readString(buf);
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readShort();
        readEntityMetadata1_4_2(buf);
    }),
    SPAWN_ITEM(21, (user, buf) -> {
        buf.readInt();
        readItemStack1_3_1(buf);
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    COLLECT_ITEM(22, (user, buf) -> {
        buf.readInt();
        buf.readInt();
    }),
    SPAWN_ENTITY(23, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        int i = buf.readInt();
        if (i > 0) {
            buf.readShort();
            buf.readShort();
            buf.readShort();
        }
    }),
    SPAWN_MOB(24, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readShort();
        buf.readShort();
        buf.readShort();
        readEntityMetadata1_4_2(buf);
    }),
    SPAWN_PAINTING(25, (user, buf) -> {
        buf.readInt();
        readString(buf);
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readInt();
    }),
    SPAWN_EXPERIENCE_ORB(26, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readShort();
    }),
    ENTITY_VELOCITY(28, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readShort();
        buf.readShort();
    }),
    DESTROY_ENTITIES(29, (user, buf) -> {
        int x = buf.readUnsignedByte();
        for (int i = 0; i < x; i++) buf.readInt();
    }),
    ENTITY_MOVEMENT(30, (user, buf) -> {
        buf.readInt();
    }),
    ENTITY_POSITION(31, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    ENTITY_ROTATION(32, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
    }),
    ENTITY_POSITION_AND_ROTATION(33, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    ENTITY_TELEPORT(34, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readByte();
    }),
    ENTITY_HEAD_LOOK(35, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    ENTITY_STATUS(38, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    ATTACH_ENTITY(39, (user, buf) -> {
        buf.readInt();
        buf.readInt();
    }),
    ENTITY_METADATA(40, (user, buf) -> {
        buf.readInt();
        readEntityMetadata1_4_2(buf);
    }),
    ENTITY_EFFECT(41, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readShort();
    }),
    REMOVE_ENTITY_EFFECT(42, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    SET_EXPERIENCE(43, (user, buf) -> {
        buf.readFloat();
        buf.readShort();
        buf.readShort();
    }),
    CHUNK_DATA(51, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readBoolean();
        buf.readShort();
        buf.readShort();
        int x = buf.readInt();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    MULTI_BLOCK_CHANGE(52, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readShort();
        int x = buf.readInt();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    BLOCK_CHANGE(53, (user, buf) -> {
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readShort();
        buf.readUnsignedByte();
    }),
    BLOCK_ACTION(54, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readInt();
        buf.readUnsignedByte();
        buf.readUnsignedByte();
        buf.readShort();
    }),
    BLOCK_BREAK_ANIMATION(55, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readUnsignedByte();
    }),
    MAP_BULK_CHUNK(56, (user, buf) -> {
        int x = buf.readShort();
        int y = buf.readInt();
        for (int i = 0; i < y; i++) buf.readByte();
        for (int i = 0; i < x; i++) {
            buf.readInt();
            buf.readInt();
            buf.readShort();
            buf.readShort();
        }
    }),
    EXPLOSION(60, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readFloat();
        int x = buf.readInt();
        for (int i = 0; i < x; i++) {
            buf.readByte();
            buf.readByte();
            buf.readByte();
        }
        buf.readFloat();
        buf.readFloat();
        buf.readFloat();
    }),
    EFFECT(61, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readByte();
        buf.readInt();
        buf.readInt();
        buf.readBoolean();
    }),
    NAMED_SOUND(62, (user, buf) -> {
        readString(buf);
        buf.readInt();
        buf.readInt();
        buf.readInt();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    GAME_EVENT(70, (user, buf) -> {
        buf.readByte();
        buf.readByte();
    }),
    SPAWN_GLOBAL_ENTITY(71, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readInt();
        buf.readInt();
        buf.readInt();
    }),
    OPEN_WINDOW(100, (user, buf) -> {
        buf.readByte();
        buf.readByte();
        readString(buf);
        buf.readByte();
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.readByte();
    }),
    SET_SLOT(103, (user, buf) -> {
        buf.readByte();
        buf.readShort();
        readItemStack1_3_1(buf);
    }),
    WINDOW_ITEMS(104, (user, buf) -> {
        buf.readByte();
        int x = buf.readShort();
        for (int i = 0; i < x; i++) readItemStack1_3_1(buf);
    }),
    WINDOW_PROPERTY(105, (user, buf) -> {
        buf.readByte();
        buf.readShort();
        buf.readShort();
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.readByte();
        buf.readShort();
        buf.readByte();
    }),
    CREATIVE_INVENTORY_ACTION(107, (user, buf) -> {
        buf.readShort();
        readItemStack1_3_1(buf);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readInt();
        readString(buf);
        readString(buf);
        readString(buf);
        readString(buf);
    }),
    MAP_DATA(131, (user, buf) -> {
        buf.readShort();
        buf.readShort();
        short x = buf.readUnsignedByte();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    BLOCK_ENTITY_DATA(132, (user, buf) -> {
        buf.readInt();
        buf.readShort();
        buf.readInt();
        buf.readByte();
        readTag(buf);
    }),
    STATISTICS(200, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    PLAYER_INFO(201, (user, buf) -> {
        readString(buf);
        buf.readByte();
        buf.readShort();
    }),
    PLAYER_ABILITIES(202, (user, buf) -> {
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    TAB_COMPLETE(203, (user, buf) -> {
        readString(buf);
    }),
    PLUGIN_MESSAGE(250, (user, buf) -> {
        readString(buf);
        short s = buf.readShort();
        for (int i = 0; i < s; i++) buf.readByte();
    }),
    SHARED_KEY(252, (user, buf) -> {
        readByteArray(buf);
        readByteArray(buf);
    }),
    SERVER_AUTH_DATA(253, (user, buf) -> {
        readString(buf);
        readByteArray(buf);
        readByteArray(buf);
    }),
    DISCONNECT(255, (user, buf) -> {
        readString(buf);
    });

    private static final ClientboundPackets1_4_2[] REGISTRY = new ClientboundPackets1_4_2[256];

    static {
        for (ClientboundPackets1_4_2 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPackets1_4_2 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPackets1_4_2(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
