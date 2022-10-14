package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4;

import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;

public enum ServerboundLoginPackets1_6_4 implements ServerboundPacketType {

	CLIENT_PROTOCOL(0x02);

	private final int id;

	ServerboundLoginPackets1_6_4(final int id) {
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
