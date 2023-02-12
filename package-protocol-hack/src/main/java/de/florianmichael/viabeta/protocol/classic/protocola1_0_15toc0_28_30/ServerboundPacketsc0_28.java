package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ServerboundPacketsc0_28 implements ServerboundPacketType, PreNettyPacketType {

    LOGIN(0, (user, buf) -> {
        buf.skipBytes(130);
    }),
    PLAYER_BLOCK_PLACEMENT(5, (user, buf) -> {
        buf.skipBytes(8);
    }),
    PLAYER_POSITION_AND_ROTATION(8, (user, buf) -> {
        buf.skipBytes(9);
    }),
    CHAT_MESSAGE(13, (user, buf) -> {
        buf.skipBytes(65);
    });

    private static final ServerboundPacketsc0_28[] REGISTRY = new ServerboundPacketsc0_28[256];

    static {
        for (ServerboundPacketsc0_28 packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ServerboundPacketsc0_28 getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ServerboundPacketsc0_28(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
