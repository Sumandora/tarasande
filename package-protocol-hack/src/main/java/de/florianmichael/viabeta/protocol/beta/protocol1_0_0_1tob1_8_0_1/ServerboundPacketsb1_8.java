package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.readItemStackb1_2;
import static de.florianmichael.viabeta.pre_netty.type.PreNettyTypes.readString;

public enum ServerboundPacketsb1_8 implements ServerboundPacketType, PreNettyPacketType {

    KEEP_ALIVE(0, (user, buf) -> {
        buf.readInt();
    }),
    LOGIN(1, (user, buf) -> {
        buf.readInt();
        readString(buf);
        buf.readLong();
        buf.readInt();
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readByte();
    }),
    HANDSHAKE(2, (user, buf) -> {
        readString(buf);
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
        buf.readByte();
        buf.readByte();
        buf.readByte();
        buf.readShort();
        buf.readLong();
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
        readItemStackb1_2(buf);
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
    }),
    POSITION(27, (user, buf) -> {
        buf.readFloat();
        buf.readFloat();
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
        buf.readBoolean();
        readItemStackb1_2(buf);
    }),
    WINDOW_CONFIRMATION(106, (user, buf) -> {
        buf.readByte();
        buf.readShort();
        buf.readByte();
    }),
    CREATIVE_INVENTORY_ACTION(107, (user, buf) -> {
        buf.readShort();
        buf.readShort();
        buf.readShort();
        buf.readShort();
        buf.readShort();
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
    SERVER_PING(254, (user, buf) -> {
    }),
    DISCONNECT(255, (user, buf) -> {
        readString(buf);
    });

    private static final ServerboundPacketsb1_8[] REGISTRY = new ServerboundPacketsb1_8[256];

    static {
        for (ServerboundPacketsb1_8 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPacketsb1_8 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPacketsb1_8(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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