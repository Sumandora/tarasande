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

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.types.minecraft.MetaTypeTemplate;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Meta1_7_6_10Type;
import io.netty.buffer.ByteBuf;

public class Metadata1_7_6_10Type extends MetaTypeTemplate {
	@Override
	public Metadata read(ByteBuf buffer) throws Exception {
		byte item = buffer.readByte();
		if (item == 127) {
			return null;
		} else {
			int typeID = (item & 224) >> 5;
			Meta1_7_6_10Type type = Meta1_7_6_10Type.byId(typeID);
			int id = item & 31;
			return new Metadata(id, type, type.type().read(buffer));
		}
	}

	@Override
	public void write(ByteBuf buffer, Metadata meta) throws Exception {
		int item = (meta.metaType().typeId() << 5 | meta.id() & 31) & 255;
		buffer.writeByte(item);
		meta.metaType().type().write(buffer, meta.getValue());
	}
}