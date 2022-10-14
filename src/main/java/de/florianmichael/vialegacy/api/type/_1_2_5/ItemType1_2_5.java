package de.florianmichael.vialegacy.api.type._1_2_5;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import io.netty.buffer.ByteBuf;

public class ItemType1_2_5 extends Type<Item> {
	private final boolean compressed;

	public ItemType1_2_5(boolean compressed) {
		super(Item.class);
		this.compressed = compressed;
	}

	@Override
	public Item read(ByteBuf buffer) throws Exception {
		int readerIndex = buffer.readerIndex();
		short id = buffer.readShort();
		if (id < 0) {
			return null;
		}
		Item item = new DataItem();
		item.setIdentifier(id);
		item.setAmount(buffer.readByte());
		item.setData(buffer.readShort());
		if(NBTItems.map.getOrDefault((int) id, false)) {
			item.setTag((compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT : TypeRegistry1_7_6_10.NBT).read(buffer));
		}
		return item;
	}

	@Override
	public void write(ByteBuf buffer, Item item) throws Exception {
		if (item == null || item.identifier() == 0) {
			buffer.writeShort(-1);
		} else {
			buffer.writeShort(item.identifier());
			buffer.writeByte(item.amount());
			buffer.writeShort(item.data());
			if(NBTItems.map.getOrDefault(item.identifier(), false)) {
				(compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT : TypeRegistry1_7_6_10.NBT).write(buffer, item.tag());
			}
		}
	}
}
