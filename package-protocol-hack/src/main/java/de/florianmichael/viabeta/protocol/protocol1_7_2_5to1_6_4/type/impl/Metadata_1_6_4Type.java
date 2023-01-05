package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata_1_6_4Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaType1_6_4.byId(index);
    }
}
