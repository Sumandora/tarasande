package de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata_1_4_2Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaType1_4_2.byId(index);
    }

}
