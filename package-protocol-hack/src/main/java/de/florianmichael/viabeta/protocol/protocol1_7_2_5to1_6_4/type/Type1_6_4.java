package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type;

import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.minecraft.MetaListType;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl.Metadata_1_6_4Type;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl.String_1_6_4Type;

import java.util.List;

public class Type1_6_4 {

    public static final Type<String> STRING = new String_1_6_4Type();

    public static final Type<Metadata> METADATA = new Metadata_1_6_4Type();
    public static final Type<List<Metadata>> METADATA_LIST = new MetaListType(METADATA);

}
