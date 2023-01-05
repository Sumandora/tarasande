package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;

public class NbtLessItem_1_3_1_2Type extends Type<Item> {

    public NbtLessItem_1_3_1_2Type() {
        super(Item.class);
    }

    public Item read(ByteBuf buffer) throws Exception {
        final short id = buffer.readShort();
        final byte amount = buffer.readByte();
        final short data = buffer.readShort();
        if (id < 0) {
            return null;
        }

        final Item item = new DataItem();
        item.setIdentifier(id);
        item.setAmount(amount);
        item.setData(data);
        return item;
    }

    public void write(ByteBuf buffer, Item item) throws Exception {
        if (item == null) {
            buffer.writeShort(-1);
            buffer.writeByte(0);
            buffer.writeShort(0);
        } else {
            buffer.writeShort(item.identifier());
            buffer.writeByte(item.amount());
            buffer.writeShort(item.data());
        }
    }

}
