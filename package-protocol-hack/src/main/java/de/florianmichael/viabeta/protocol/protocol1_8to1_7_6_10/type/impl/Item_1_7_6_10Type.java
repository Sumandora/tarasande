package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import io.netty.buffer.ByteBuf;

public class Item_1_7_6_10Type extends Type<Item> {

    private final boolean compressed;

    public Item_1_7_6_10Type(boolean compressed) {
        super(Item.class);
        this.compressed = compressed;
    }

    @Override
    public Item read(ByteBuf buffer) throws Exception {
        final short id = buffer.readShort();
        if (id < 0) {
            return null;
        }

        final Item item = new DataItem();
        item.setIdentifier(id);
        item.setAmount(buffer.readByte());
        item.setData(buffer.readShort());
        item.setTag((this.compressed ? Type1_7_6_10.COMPRESSED_NBT : Type1_7_6_10.NBT).read(buffer));
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
            (this.compressed ? Type1_7_6_10.COMPRESSED_NBT : Type1_7_6_10.NBT).write(buffer, item.tag());
        }
    }

}
