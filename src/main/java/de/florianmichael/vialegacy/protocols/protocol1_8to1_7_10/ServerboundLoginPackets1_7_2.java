package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundLoginPackets1_7_2 implements ServerboundPacketType {

	HANDSHAKE(0x00),
	ENCRYPTION_RESPONSE(0x01);

	private final int id;

	ServerboundLoginPackets1_7_2(final int id) {
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
