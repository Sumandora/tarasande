package de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.impl.Metadata_1_4_2Type;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.impl.NbtLessItem_1_4_2Type;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.impl.UnsignedByteByteArray_1_4_2Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ItemArray_1_7_6_10Type;

import java.util.List;

public class Type1_4_2 {

    public static final Type<byte[]> UNSIGNED_BYTE_BYTE_ARRAY = new UnsignedByteByteArray_1_4_2Type();

    public static final Type<Item> NBTLESS_ITEM = new NbtLessItem_1_4_2Type();
    public static final Type<Item[]> NBTLESS_ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(NBTLESS_ITEM);

    public static final Type<Metadata> METADATA = new Metadata_1_4_2Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);

}
