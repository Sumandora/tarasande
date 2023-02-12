package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ServerboundPackets1_3_1 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.skipBytes(4);
    }),
    LOGIN(1, (user, buf) -> {
        buf.skipBytes(4);
        readString(buf);
        buf.skipBytes(5);
    }),
    CLIENT_PROTOCOL(2, (user, buf) -> {
        buf.skipBytes(1);
        readString(buf);
        readString(buf);
        buf.skipBytes(4);
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readString(buf);
    }),
    INTERACT_ENTITY(7, (user, buf) -> {
        buf.skipBytes(9);
    }),
    RESPAWN(9, (user, buf) -> {
        buf.skipBytes(8);
        readString(buf);
    }),
    PLAYER_MOVEMENT(10, (user, buf) -> {
        buf.skipBytes(1);
    }),
    PLAYER_POSITION(11, (user, buf) -> {
        buf.skipBytes(33);
    }),
    PLAYER_ROTATION(12, (user, buf) -> {
        buf.skipBytes(9);
    }),
    PLAYER_POSITION_AND_ROTATION(13, (user, buf) -> {
        buf.skipBytes(41);
    }),
    PLAYER_DIGGING(14, (user, buf) -> {
        buf.skipBytes(11);
    }),
    PLAYER_BLOCK_PLACEMENT(15, (user, buf) -> {
        buf.skipBytes(10);
        readItemStack1_3_1(buf);
        buf.skipBytes(3);
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.skipBytes(2);
    }),
    ANIMATION(18, (user, buf) -> {
        buf.skipBytes(5);
    }),
    ENTITY_ACTION(19, (user, buf) -> {
        buf.skipBytes(5);
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.skipBytes(1);
    }),
    CLICK_WINDOW(102, (user, buf) -> {
        buf.skipBytes(6);
        buf.readBoolean();
        readItemStack1_3_1(buf);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.skipBytes(4);
    }),
    CREATIVE_INVENTORY_ACTION(107, (user, buf) -> {
        buf.skipBytes(2);
        readItemStack1_3_1(buf);
    }),
    CLICK_WINDOW_BUTTON(108, (user, buf) -> {
        buf.skipBytes(2);
    }),
    UPDATE_SIGN(130, (user, buf) -> {
        buf.skipBytes(10);
        readString(buf);
        readString(buf);
        readString(buf);
        readString(buf);
    }),
    PLAYER_ABILITIES(202, (user, buf) -> {
        buf.skipBytes(3);
    }),
    TAB_COMPLETE(203, (user, buf) -> {
        readString(buf);
    }),
    CLIENT_SETTINGS(204, (user, buf) -> {
        readString(buf);
        buf.skipBytes(3);
    }),
    CLIENT_STATUS(205, (user, buf) -> {
        buf.skipBytes(1);
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
    SERVER_PING(254, (user, buf) -> {
    }),
    DISCONNECT(255, (user, buf) -> {
        readString(buf);
    });

    private static final ServerboundPackets1_3_1[] REGISTRY = new ServerboundPackets1_3_1[256];

    static {
        for (ServerboundPackets1_3_1 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPackets1_3_1 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPackets1_3_1(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
