package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.type;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ItemArray_1_7_6_10Type;

public class Typeb1_8_0_1 {

    public static final Type<Item> CREATIVE_ITEM = new NbtLessItem_b1_8_0_1Type();
    public static final Type<Item[]> CREATIVE_ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(CREATIVE_ITEM);

}
