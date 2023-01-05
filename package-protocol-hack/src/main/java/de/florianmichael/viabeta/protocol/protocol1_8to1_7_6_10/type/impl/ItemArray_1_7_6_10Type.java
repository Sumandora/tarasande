package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class ItemArray_1_7_6_10Type<T extends Item> extends Type<Item[]> {

    private final Type<T> itemType;

    public ItemArray_1_7_6_10Type(final Type<T> itemType) {
        super(Item[].class);
        this.itemType = itemType;
    }

    @Override
    public Item[] read(ByteBuf buffer) throws Exception {
        final int amount = buffer.readShort();
        final Item[] items = new Item[amount];

        for (int i = 0; i < amount; i++) {
            items[i] = this.itemType.read(buffer);
        }
        return items;
    }

    @Override
    public void write(ByteBuf buffer, Item[] items) throws Exception {
        buffer.writeShort(items.length);
        for (Item item : items) {
            this.itemType.write(buffer, (T) item);
        }
    }

}
