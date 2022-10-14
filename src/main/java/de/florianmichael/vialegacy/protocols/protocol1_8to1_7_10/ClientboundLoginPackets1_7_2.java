package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10;

import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;

public enum ClientboundLoginPackets1_7_2 implements ClientboundPacketType {

	ENCRYPTION_REQUEST(0x01),
	LOGIN_SUCCESS(0x02);

	private final int id;

	ClientboundLoginPackets1_7_2(final int id) {
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
