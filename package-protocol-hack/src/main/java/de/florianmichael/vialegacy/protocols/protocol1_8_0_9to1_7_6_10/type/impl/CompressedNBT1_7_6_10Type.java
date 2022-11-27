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

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class CompressedNBT1_7_6_10Type extends Type<CompoundTag> {
	public CompressedNBT1_7_6_10Type() {
		super(CompoundTag.class);
	}

	@Override
	public CompoundTag read(ByteBuf buffer) throws IOException {
		short length = buffer.readShort();
		if (length < 0) {
			return null;
		}

		ByteBuf compressed = buffer.readSlice(length);

		try (GZIPInputStream gzipStream = new GZIPInputStream(new ByteBufInputStream(compressed))) {
			return NBTIO.readTag(gzipStream);
		}
	}

	@Override
	public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
		if (nbt == null) {
			buffer.writeShort(-1);
			return;
		}

		ByteBuf compressedBuf = buffer.alloc().buffer();
		try {
			try (GZIPOutputStream gzipStream = new GZIPOutputStream(new ByteBufOutputStream(compressedBuf))) {
				NBTIO.writeTag(gzipStream, nbt);
			}

			buffer.writeShort(compressedBuf.readableBytes());
			buffer.writeBytes(compressedBuf);
		} finally {
			compressedBuf.release();
		}
	}
}