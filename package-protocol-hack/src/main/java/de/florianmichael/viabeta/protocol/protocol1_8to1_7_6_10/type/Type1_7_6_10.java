package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.*;

import java.util.List;

public class Type1_7_6_10 {

    public static final Type<int[]> INT_ARRAY = new IntArray_1_7_6_10Type();

    public static final Type<CompoundTag> NBT = new NBT_1_7_6_10Type(false);
    public static final Type<CompoundTag> COMPRESSED_NBT = new NBT_1_7_6_10Type(true);

    public static final Type<Item> ITEM = new Item_1_7_6_10Type(false);
    public static final Type<Item> COMPRESSED_ITEM = new Item_1_7_6_10Type(true);

    public static final Type<Item[]> ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(ITEM);
    public static final Type<Item[]> COMPRESSED_ITEM_ARRAY = new ItemArray_1_7_6_10Type<>(COMPRESSED_ITEM);

    public static final Type<Metadata> METADATA = new Metadata_1_7_6_10Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);

    public static final Type<BlockChangeRecord[]> BLOCK_CHANGE_RECORD_ARRAY = new BlockChangeRecordArray_1_7_6_10Type();

    public static final Type<Position> POSITION_BYTE = new PositionVarY_1_7_6_10Type<>(Type.BYTE, i -> (byte) i);
    public static final Type<Position> POSITION_UBYTE = new PositionVarY_1_7_6_10Type<>(Type.UNSIGNED_BYTE, i -> (short) i);
    public static final Type<Position> POSITION_SHORT = new PositionVarY_1_7_6_10Type<>(Type.SHORT, i -> (short) i);
    public static final Type<Position> POSITION_INT = new PositionVarY_1_7_6_10Type<>(Type.INT, i -> i);

}
