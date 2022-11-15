package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4;

import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;
import de.florianmichael.vialegacy.api.LegacyClientboundPacketType;

public enum ClientboundLoginPackets1_6_4 implements LegacyClientboundPacketType {

	SERVER_AUTH_DATA(0xFD, ((buffer, transformer) -> {
		transformer.readString(buffer);

		final int s = buffer.readShort();

		for (int i = 0; i < s; i++)
			buffer.readByte();

		final int s2 = buffer.readShort();

		for (int i = 0; i < s2; i++)
			buffer.readByte();
	})),
	SHARED_KEY(0xFC, ((buffer, transformer) -> {
		final int s = buffer.readShort();

		for (int i = 0; i < s; i++)
			buffer.readByte();

		final int s2 = buffer.readShort();

		for (int i = 0; i < s2; i++)
			buffer.readByte();
	}))
	;

	private final int id;
	private final IPacketSplitter splitter;

	ClientboundLoginPackets1_6_4(final int id, final IPacketSplitter splitter) {
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
