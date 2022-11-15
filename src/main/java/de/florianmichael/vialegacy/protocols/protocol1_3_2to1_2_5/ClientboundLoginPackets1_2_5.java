package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5;

import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;
import de.florianmichael.vialegacy.api.LegacyClientboundPacketType;

import java.util.Arrays;

public enum ClientboundLoginPackets1_2_5 implements LegacyClientboundPacketType {

	HANDSHAKE(0x02, (buffer, transformer) -> {
		transformer.readString(buffer);
	});

	private final int id;
	private final IPacketSplitter splitter;

	ClientboundLoginPackets1_2_5(final int id, final IPacketSplitter splitter) {
		this.id = id;
		this.splitter = splitter;
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
	public IPacketSplitter getSplitter() {
		return splitter;
	}
}
