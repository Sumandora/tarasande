package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.*;

public enum ServerboundPackets1_6_4 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.readInt();
    }),
    LOGIN(1, (user, buf) -> {
        buf.readInt();
        readString(buf);
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    CLIENT_PROTOCOL(2, (user, buf) -> {
        buf.readByte();
        readString(buf);
        readString(buf);
        buf.readInt();
    }),
    CHAT_MESSAGE(3, (user, buf) -> {
        readString(buf);
    }),
    INTERACT_ENTITY(7, (user, buf) -> {
        buf.readInt();
        buf.readInt();
        buf.readByte();
    }),
    RESPAWN(9, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readShort();
        readString(buf);
    }),
    PLAYER_MOVEMENT(10, (user, buf) -> {
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION(11, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readUnsignedByte();
    }),
    PLAYER_ROTATION(12, (user, buf) -> {
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    PLAYER_POSITION_AND_ROTATION(13, (user, buf) -> {
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readDouble();
        buf.readFloat();
        buf.readFloat();
        buf.readUnsignedByte();
    }),
    PLAYER_DIGGING(14, (user, buf) -> {
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
    }),
    PLAYER_BLOCK_PLACEMENT(15, (user, buf) -> {
        buf.readInt();
        buf.readUnsignedByte();
        buf.readInt();
        buf.readUnsignedByte();
        readItemStack1_3_1(buf);
        buf.readUnsignedByte();
        buf.readUnsignedByte();
        buf.readUnsignedByte();
    }),
    HELD_ITEM_CHANGE(16, (user, buf) -> {
        buf.readShort();
    }),
    ANIMATION(18, (user, buf) -> {
        buf.readInt();
        buf.readByte();
    }),
    ENTITY_ACTION(19, (user, buf) -> {
        buf.readInt();
        buf.readByte();
        buf.readInt();
    }),
    STEER_VEHICLE(27, (user, buf) -> {
        buf.readFloat();
        buf.readFloat();
        buf.readBoolean();
        buf.readBoolean();
    }),
    CLOSE_WINDOW(101, (user, buf) -> {
        buf.readByte();
    }),
    CLICK_WINDOW(102, (user, buf) -> {
        buf.readByte();
        buf.readShort();
        buf.readByte();
        buf.readShort();
        buf.readByte();
        readItemStack1_3_1(buf);
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
    CLICK_WINDOW_BUTTON(108, (user, buf) -> {
        buf.readByte();
        buf.readByte();
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
    PLAYER_ABILITIES(202, (user, buf) -> {
        buf.readByte();
        buf.readFloat();
        buf.readFloat();
    }),
    TAB_COMPLETE(203, (user, buf) -> {
        readString(buf);
    }),
    CLIENT_SETTINGS(204, (user, buf) -> {
        readString(buf);
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readBoolean();
    }),
    CLIENT_STATUS(205, (user, buf) -> {
        buf.readByte();
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
        buf.readByte();
        buf.readByte();
        readString(buf);
        buf.readShort();
        byte x = buf.readByte();
        if (x >= 73) {
            readString(buf);
            buf.readInt();
        }
    }),
    DISCONNECT(255, (user, buf) -> {
        readString(buf);
    });

    private static final ServerboundPackets1_6_4[] REGISTRY = new ServerboundPackets1_6_4[256];

    static {
        for (ServerboundPackets1_6_4 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPackets1_6_4 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPackets1_6_4(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
