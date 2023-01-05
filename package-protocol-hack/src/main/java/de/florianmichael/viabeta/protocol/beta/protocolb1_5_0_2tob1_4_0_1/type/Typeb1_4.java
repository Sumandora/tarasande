package de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1.type;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import de.florianmichael.viabeta.protocol.beta.protocolb1_5_0_2tob1_4_0_1.type.impl.Metadata_b1_4Type;

import java.util.List;

public class Typeb1_4 {

    public static final Type<Metadata> METADATA = new Metadata_b1_4Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);
}
