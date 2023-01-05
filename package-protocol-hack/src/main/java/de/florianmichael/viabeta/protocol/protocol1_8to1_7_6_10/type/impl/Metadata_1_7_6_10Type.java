package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaType1_7_6;

public class Metadata_1_7_6_10Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaType1_7_6.byId(index);
    }

}
