package de.florianmichael.vialegacy.api.type._1_7_6_10;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import io.netty.buffer.ByteBuf;

public class ItemArrayType_1_7_6_10 extends Type<Item[]> {
	private final boolean compressed;

	public ItemArrayType_1_7_6_10(boolean compressed) {
		super(Item[].class);
		this.compressed = compressed;
	}

	@Override
	public Item[] read(ByteBuf buffer) throws Exception {
		int amount = Type.SHORT.read(buffer);
		Item[] items = new Item[amount];

		for(int i = 0; i < amount; ++i) {
			items[i] = (compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM : TypeRegistry1_7_6_10.ITEM).read(buffer);
		}
		return items;
	}

	@Override
	public void write(ByteBuf buffer, Item[] items) throws Exception {
		Type.SHORT.write(buffer, (short)items.length);
		for (Item item : items) {
			(compressed ? TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM : TypeRegistry1_7_6_10.ITEM).write(buffer, item);
		}
	}
}