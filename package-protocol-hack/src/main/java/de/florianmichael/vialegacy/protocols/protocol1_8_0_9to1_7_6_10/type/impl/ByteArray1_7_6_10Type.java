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
import io.netty.buffer.ByteBuf;

public class ByteArray1_7_6_10Type extends Type<byte[]> {

	public ByteArray1_7_6_10Type() {
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
