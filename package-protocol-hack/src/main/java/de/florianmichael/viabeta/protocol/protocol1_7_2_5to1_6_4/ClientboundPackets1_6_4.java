package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ClientboundPackets1_6_4 implements ClientboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.skipBytes(4);
    }),
    JOIN_GAME(1, (user, buf) -> {
        buf.skipBytes(4);
        readString(buf);
        buf.skipBytes(5);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readString(buf);
    }),
    TIME_UPDATE(4, (user, buf) -> {
        buf.skipBytes(16);
    }),
    ENTITY_EQUIPMENT(5, (user, buf) -> {
        buf.skipBytes(6);
        readItemStack1_3_1(buf);
    }),
    SPAWN_POSITION(6, (user, buf) -> {
        buf.skipBytes(12);
    }),
    UPDATE_HEALTH(8, (user, buf) -> {
        buf.skipBytes(10);
    }),
    RESPAWN(9, (user, buf) -> {
        buf.skipBytes(8);
        readString(buf);
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
        buf.skipBytes(2);
    }),
    USE_BED(17, (user, buf) -> {
        buf.skipBytes(14);
    }),
    ENTITY_ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    SPAWN_PLAYER(20, (user, buf) -> {
        buf.skipBytes(4);
        readString(buf);
        buf.skipBytes(16);
        readEntityMetadata1_4_4(buf);
    }),
    COLLECT_ITEM(22, (user, buf) -> {
        buf.skipBytes(8);
    }),
    SPAWN_ENTITY(23, (user, buf) -> {
        buf.skipBytes(19);
        int i = buf.readInt();
        if (i > 0) {
            buf.skipBytes(6);
        }
    }),
    SPAWN_MOB(24, (user, buf) -> {
        buf.skipBytes(26);
        readEntityMetadata1_4_4(buf);
    }),
    SPAWN_PAINTING(25, (user, buf) -> {
        buf.skipBytes(4);
        readString(buf);
        buf.skipBytes(16);
    }),
    SPAWN_EXPERIENCE_ORB(26, (user, buf) -> {
        buf.skipBytes(18);
    }),
    ENTITY_VELOCITY(28, (user, buf) -> {
        buf.skipBytes(10);
    }),
    DESTROY_ENTITIES(29, (user, buf) -> {
        int x = buf.readUnsignedByte();
        for (int i = 0; i < x; i++) buf.readInt();
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
    ENTITY_HEAD_LOOK(35, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ENTITY_STATUS(38, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ATTACH_ENTITY(39, (user, buf) -> {
        buf.skipBytes(9);
    }),
    ENTITY_METADATA(40, (user, buf) -> {
        buf.skipBytes(4);
        readEntityMetadata1_4_4(buf);
    }),
    ENTITY_EFFECT(41, (user, buf) -> {
        buf.skipBytes(8);
    }),
    REMOVE_ENTITY_EFFECT(42, (user, buf) -> {
        buf.skipBytes(5);
    }),
    SET_EXPERIENCE(43, (user, buf) -> {
        buf.skipBytes(8);
    }),
    ENTITY_PROPERTIES(44, (user, buf) -> {
        buf.skipBytes(4);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) {
            readString(buf);
            buf.skipBytes(8);
            int s = buf.readUnsignedShort();
            for (int k = 0; k < s; k++) {
                buf.skipBytes(25);
            }
        }
    }),
    CHUNK_DATA(51, (user, buf) -> {
        buf.skipBytes(8);
        buf.readBoolean();
        buf.skipBytes(4);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    MULTI_BLOCK_CHANGE(52, (user, buf) -> {
        buf.skipBytes(10);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    BLOCK_CHANGE(53, (user, buf) -> {
        buf.skipBytes(12);
    }),
    BLOCK_ACTION(54, (user, buf) -> {
        buf.skipBytes(14);
    }),
    BLOCK_BREAK_ANIMATION(55, (user, buf) -> {
        buf.skipBytes(17);
    }),
    MAP_BULK_CHUNK(56, (user, buf) -> {
        int x = buf.readShort();
        int y = buf.readInt();
        buf.readBoolean();
        for (int i = 0; i < y; i++) buf.readByte();
        for (int i = 0; i < x; i++) {
            buf.skipBytes(12);
        }
    }),
    EXPLOSION(60, (user, buf) -> {
        buf.skipBytes(28);
        int x = buf.readInt();
        for (int i = 0; i < x; i++) {
            buf.skipBytes(3);
        }
        buf.skipBytes(12);
    }),
    EFFECT(61, (user, buf) -> {
        buf.skipBytes(17);
        buf.readBoolean();
    }),
    NAMED_SOUND(62, (user, buf) -> {
        readString(buf);
        buf.skipBytes(17);
    }),
    SPAWN_PARTICLE(63, (user, buf) -> {
        readString(buf);
        buf.skipBytes(32);
    }),
    GAME_EVENT(70, (user, buf) -> {
        buf.skipBytes(2);
    }),
    SPAWN_GLOBAL_ENTITY(71, (user, buf) -> {
        buf.skipBytes(17);
    }),
    OPEN_WINDOW(100, (user, buf) -> {
        buf.skipBytes(1);
        int x = buf.readByte();
        readString(buf);
        buf.skipBytes(1);
        buf.readBoolean();
        if (x == 11) buf.readInt();
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.skipBytes(1);
    }),
    SET_SLOT(103, (user, buf) -> {
        buf.skipBytes(3);
        readItemStack1_3_1(buf);
    }),
    WINDOW_ITEMS(104, (user, buf) -> {
        buf.skipBytes(1);
        int x = buf.readShort();
        for (int i = 0; i < x; i++) readItemStack1_3_1(buf);
    }),
    WINDOW_PROPERTY(105, (user, buf) -> {
        buf.skipBytes(5);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.skipBytes(4);
    }),
    CREATIVE_INVENTORY_ACTION(107, (user, buf) -> {
        buf.skipBytes(2);
        readItemStack1_3_1(buf);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.skipBytes(10);
        readString(buf);
        readString(buf);
        readString(buf);
        readString(buf);
    }),
    MAP_DATA(131, (user, buf) -> {
        buf.skipBytes(4);
        int x = buf.readUnsignedShort();
        for (int i = 0; i < x; i++) buf.readByte();
    }),
    BLOCK_ENTITY_DATA(132, (user, buf) -> {
        buf.skipBytes(11);
        readTag(buf);
    }),
    OPEN_SIGN_EDITOR(133, (user, buf) -> {
        buf.skipBytes(13);
    }),
    STATISTICS(200, (user, buf) -> {
        buf.skipBytes(8);
    }),
    PLAYER_INFO(201, (user, buf) -> {
        readString(buf);
        buf.skipBytes(3);
    }),
    PLAYER_ABILITIES(202, (user, buf) -> {
        buf.skipBytes(9);
    }),
    TAB_COMPLETE(203, (user, buf) -> {
        readString(buf);
    }),
    SCOREBOARD_OBJECTIVE(206, (user, buf) -> {
        readString(buf);
        readString(buf);
        buf.skipBytes(1);
    }),
    UPDATE_SCORE(207, (user, buf) -> {
        readString(buf);
        byte b = buf.readByte();
        if (b != 1) {
            readString(buf);
            buf.skipBytes(4);
        }
    }),
    DISPLAY_SCOREBOARD(208, (user, buf) -> {
        buf.skipBytes(1);
        readString(buf);
    }),
    TEAMS(209, (user, buf) -> {
        readString(buf);
        int x = buf.readByte();
        if (x == 0 || x == 2) {
            readString(buf);
            readString(buf);
            readString(buf);
            buf.skipBytes(1);
        }
        if (x == 0 || x == 3 || x == 4) {
            x = buf.readShort();
            for (int i = 0; i < x; i++) readString(buf);
        }
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

    private static final ClientboundPackets1_6_4[] REGISTRY = new ClientboundPackets1_6_4[256];

    static {
        for (ClientboundPackets1_6_4 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPackets1_6_4 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPackets1_6_4(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
