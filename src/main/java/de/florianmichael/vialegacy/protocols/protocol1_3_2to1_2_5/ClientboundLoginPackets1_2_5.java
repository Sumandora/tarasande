package de.florianmichael.vialegacy.protocols.protocol1_3_2to1_2_5;

import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitterLogic;
import de.florianmichael.vialegacy.protocol.splitter.LegacyClientboundPacketType;

import java.util.Arrays;

public enum ClientboundLoginPackets1_2_5 implements LegacyClientboundPacketType {

	HANDSHAKE(0x02, (buffer, transformer) -> {
		transformer.readString(buffer);
	});

	private final int id;
	private final IPacketSplitterLogic splitter;

	ClientboundLoginPackets1_2_5(final int id, final IPacketSplitterLogic splitter) {
		this.id = id;
		this.splitter = splitter;

		for (LegacyProtocolVersion legacyProtocolVersion : Arrays.asList(LegacyProtocolVersion.R1_2_5, LegacyProtocolVersion.R1_2_3, LegacyProtocolVersion.R1_1)) {
			this.registerSplitter(legacyProtocolVersion);
		}
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
	public IPacketSplitterLogic getSplitter() {
		return splitter;
	}
}
