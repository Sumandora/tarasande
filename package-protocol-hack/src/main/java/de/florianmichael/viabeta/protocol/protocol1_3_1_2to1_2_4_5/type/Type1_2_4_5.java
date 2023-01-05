package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.impl.Item_1_2_4_5Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ItemArray_1_7_6_10Type;

public class Type1_2_4_5 {

    public static final Type<Item> ITEM = new Item_1_2_4_5Type(false);
    public static final Type<Item> COMPRESSED_NBT_ITEM = new Item_1_2_4_5Type(true);

    public static final Type<Item[]> ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(ITEM);
    public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(COMPRESSED_NBT_ITEM);

}
