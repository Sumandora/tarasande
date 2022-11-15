package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.impl;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.TypeRegistry1_7_6_10;
import io.netty.buffer.ByteBuf;

public class ItemType_1_7_6_10 extends Type<Item> {
	private final boolean compressed;

	public ItemType_1_7_6_10(boolean compressed) {
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
		item.setTag((compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT : TypeRegistry1_7_6_10.NBT).read(buffer));
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
			(compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT : TypeRegistry1_7_6_10.NBT).write(buffer, item.tag());
		}
	}
}