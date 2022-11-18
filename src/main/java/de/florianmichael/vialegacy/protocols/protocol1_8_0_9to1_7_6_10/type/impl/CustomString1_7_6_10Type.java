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

import com.viaversion.viaversion.api.type.PartialType;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class CustomString1_7_6_10Type extends PartialType<String[], Integer> {

	public CustomString1_7_6_10Type(Integer param) {
		super(param, String[].class);
	}

	public String[] read(ByteBuf buffer, Integer size) throws Exception {
		if (buffer.readableBytes() < size/4) {
			throw new RuntimeException("Readable bytes does not match expected!");
		} else {
			String[] array = new String[size];
			for (int i = 0; i<size; i++) {
				array[i] = Type.STRING.read(buffer);
			}
			return array;
		}
	}

	public void write(ByteBuf buffer, Integer size, String[] strings) throws Exception {
		for (String s : strings) Type.STRING.write(buffer, s);
	}
}