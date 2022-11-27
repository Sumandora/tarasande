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

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import io.netty.buffer.ByteBuf;

public class Item1_7_6_10Type extends Type<Item> {
	private final boolean compressed;

	public Item1_7_6_10Type(boolean compressed) {
		super(Item.class);
		this.compressed = compressed;
	}

	@Override
	public Item read(ByteBuf buffer) throws Exception {
		short id = buffer.readShort();
		if (id < 0) {
			return null;
		}
		Item item = new DataItem();
		item.setIdentifier(id);
		item.setAmount(buffer.readByte());
		item.setData(buffer.readShort());
		item.setTag((compressed ? Types1_7_6_10.COMPRESSED_NBT : Types1_7_6_10.NBT).read(buffer));
		return item;
	}

	@Override
	public void write(ByteBuf buffer, Item item) throws Exception {
		if (item == null) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(item.identifier());
			buffer.writeByte(item.amount());
			buffer.writeShort(item.data());
			(compressed ? Types1_7_6_10.COMPRESSED_NBT : Types1_7_6_10.NBT).write(buffer, item.tag());
		}
	}
}