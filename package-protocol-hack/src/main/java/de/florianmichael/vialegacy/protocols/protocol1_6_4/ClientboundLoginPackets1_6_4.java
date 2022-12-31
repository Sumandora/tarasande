/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_6_4;

import de.florianmichael.vialegacy.api.viaversion.LegacyClientboundPacketType;
import de.florianmichael.vialegacy.protocol.splitter.IPacketSplitter;

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