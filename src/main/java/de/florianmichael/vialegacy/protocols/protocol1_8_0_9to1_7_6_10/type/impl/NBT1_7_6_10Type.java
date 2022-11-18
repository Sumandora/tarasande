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

import java.io.*;

public class NBT1_7_6_10Type extends Type<CompoundTag> {
	public NBT1_7_6_10Type() {
		super(CompoundTag.class);
	}

	@Override
	public CompoundTag read(ByteBuf buffer) {
		short length = buffer.readShort();
		if (length < 0) {return null;}
		ByteBufInputStream byteBufInputStream = new ByteBufInputStream(buffer);
		DataInputStream dataInputStream = new DataInputStream(byteBufInputStream);
		try {
			return (CompoundTag) NBTIO.readTag((DataInput) dataInputStream);
		} catch (Throwable throwable) {throwable.printStackTrace();}
		finally {
			try {
				dataInputStream.close();
			} catch (IOException e) {e.printStackTrace();}
		}
		return null;
	}

	@Override
	public void write(ByteBuf buffer, CompoundTag nbt) throws Exception {
		if (nbt == null) {
			buffer.writeShort(-1);
		} else {
			ByteBuf buf = buffer.alloc().buffer();
			ByteBufOutputStream bytebufStream = new ByteBufOutputStream(buf);
			DataOutputStream dataOutputStream = new DataOutputStream(bytebufStream);
			NBTIO.writeTag((DataOutput) dataOutputStream, nbt);
			dataOutputStream.close();
			buffer.writeShort(buf.readableBytes());
			buffer.writeBytes(buf);
			buf.release();
		}
	}
}