package de.florianmichael.viabeta.protocol.classic.protocolc0_0_16a_02to0_0_15a_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettyPacketType;
import io.netty.buffer.ByteBuf;

import java.util.function.BiConsumer;

public enum ClientboundPacketsc0_15a implements ClientboundPacketType, PreNettyPacketType {

    JOIN_GAME(0, (user, buf) -> {
        buf.skipBytes(64);
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
    DESTROY_ENTITIES(9, (user, buf) -> {
        buf.skipBytes(1);
    });

    private static final ClientboundPacketsc0_15a[] REGISTRY = new ClientboundPacketsc0_15a[256];

    static {
        for (ClientboundPacketsc0_15a packet : values()) {
            REGISTRY[packet.id] = packet;
        }
    }

    public static ClientboundPacketsc0_15a getPacket(final int id) {
        return REGISTRY[id];
    }

    private final int id;
    private final BiConsumer<UserConnection, ByteBuf> packetReader;

    ClientboundPacketsc0_15a(final int id, final BiConsumer<UserConnection, ByteBuf> packetReader) {
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
