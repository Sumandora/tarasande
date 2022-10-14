/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 5:37 PM
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.vialegacy.api.type._1_6_4;

import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ByteArrayType_1_6_4 extends Type<byte[]> {
	
	public ByteArrayType_1_6_4() {
		super(byte[].class);
	}

	@Override
	public byte[] read(ByteBuf buf) throws Exception {
		byte[] data = new byte[buf.readUnsignedShort()];
		buf.readBytes(data, 0, data.length);
		return data;
	}

	@Override
	public void write(ByteBuf buf, byte[] data) throws Exception {
		buf.writeShort(data.length);
		buf.writeBytes(data);
	}
}
