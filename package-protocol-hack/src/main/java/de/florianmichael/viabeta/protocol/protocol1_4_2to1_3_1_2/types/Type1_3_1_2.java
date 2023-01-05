package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl.Metadata_1_3_1_2Type;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl.NbtLessItem_1_3_1_2Type;

import java.util.List;

public class Type1_3_1_2 {

    public static final Type<Item> NBTLESS_ITEM = new NbtLessItem_1_3_1_2Type();

    public static final Type<Metadata> METADATA = new Metadata_1_3_1_2Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);
}
