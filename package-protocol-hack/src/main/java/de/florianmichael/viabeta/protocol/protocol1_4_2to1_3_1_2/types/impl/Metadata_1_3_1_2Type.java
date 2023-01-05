package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata_1_3_1_2Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaType1_3_1_2.byId(index);
    }

}
