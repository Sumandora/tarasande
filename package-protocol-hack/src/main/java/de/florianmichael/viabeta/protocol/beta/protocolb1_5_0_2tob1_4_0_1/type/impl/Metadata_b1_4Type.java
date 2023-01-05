package de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata_b1_4Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaTypeb1_4.byId(index);
    }

}
