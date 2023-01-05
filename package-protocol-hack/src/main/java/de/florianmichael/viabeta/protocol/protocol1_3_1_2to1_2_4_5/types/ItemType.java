package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.types;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import io.netty.buffer.ByteBuf;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.data.NbtItemList_1_2_5;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemType extends Type<Item> {

    private final boolean compressed;

    public ItemType(boolean compressed) {
        super(Item.class);
        this.compressed = compressed;
    }

    public Item read(ByteBuf buffer) throws Exception {
        final short id = buffer.readShort();
        if (id < 0) {
            return null;
        } else {
            final Item item = new DataItem();
            item.setIdentifier(id);
            item.setAmount(buffer.readByte());
            item.setData(buffer.readShort());
            if (NbtItemList_1_2_5.hasNbt(id)) {
                item.setTag((this.compressed ? Type1_7_6_10.COMPRESSED_NBT : Type1_7_6_10.NBT).read(buffer));
            }
            return item;
        }
    }

    public void write(ByteBuf buffer, Item item) throws Exception {
        if (item == null) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(item.identifier());
            buffer.writeByte(item.amount());
            buffer.writeShort(item.data());
            if (NbtItemList_1_2_5.hasNbt(item.identifier())) {
                (this.compressed ? Type1_7_6_10.COMPRESSED_NBT : Type1_7_6_10.NBT).write(buffer, item.tag());
            }
        }
    }

}
