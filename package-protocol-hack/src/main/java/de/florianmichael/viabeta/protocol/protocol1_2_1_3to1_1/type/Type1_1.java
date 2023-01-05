package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type;

import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl.BlockChangeRecordArray_1_1Type;

public class Type1_1 {

    public static Type<BlockChangeRecord[]> BLOCK_CHANGE_RECORD_ARRAY = new BlockChangeRecordArray_1_1Type();

}
