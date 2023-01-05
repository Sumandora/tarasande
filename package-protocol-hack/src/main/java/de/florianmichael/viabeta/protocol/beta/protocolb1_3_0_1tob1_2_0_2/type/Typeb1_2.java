package de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type.impl.Metadata_b1_2_0_2Type;

import java.util.List;

public class Typeb1_2 {

    public static final Type<Metadata> METADATA = new Metadata_b1_2_0_2Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);

}
