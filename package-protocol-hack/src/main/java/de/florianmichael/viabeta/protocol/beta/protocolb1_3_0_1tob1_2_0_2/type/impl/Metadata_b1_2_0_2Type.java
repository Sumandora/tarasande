package de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.types.minecraft.OldMetaType;

public class Metadata_b1_2_0_2Type extends OldMetaType {

    @Override
    protected MetaType getType(int index) {
        return MetaTypeb1_2.byId(index);
    }

}
