package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.type.impl.*;

import java.util.List;

public class TypeRegistry1_7_6_10 {

    public static final Type<CompoundTag> COMPRESSED_NBT = new CompressedNBTType_1_7_6_10();
    public static final Type<Item[]> COMPRESSED_NBT_ITEM_ARRAY = new ItemArrayType_1_7_6_10(true);
    public static final Type<Item> ITEM = new ItemType_1_7_6_10(false);
    public static final Type<Item> COMPRESSED_NBT_ITEM = new ItemType_1_7_6_10(true);
    public static final Type<List<Metadata>> METADATA_LIST = new MetadataListType_1_7_6_10();
    public static final Type<Metadata> METADATA = new MetadataType_1_7_6_10();
    public static final Type<CompoundTag> NBT = new NBTType_1_7_6_10();
    public static final Type<byte[]> BYTEARRAY = new ByteArrayType_1_7_6_10();

}
