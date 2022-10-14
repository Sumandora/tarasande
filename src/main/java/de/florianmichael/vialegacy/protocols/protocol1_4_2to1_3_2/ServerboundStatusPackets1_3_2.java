package de.florianmichael.vialegacy.protocols.protocol1_4_2to1_3_2;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundStatusPackets1_3_2 implements ServerboundPacketType {
    SERVER_LIST_PING(0xFE);

    private final int id;

    ServerboundStatusPackets1_3_2(final int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return name();
    }
}
