package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type;

import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.impl.ByteArray_c0_30Type;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.impl.Position_c0_30Type;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.impl.String_c0_30Type;

public class Typec0_30 {

    public static final Type<String> STRING = new String_c0_30Type();

    public static final Type<byte[]> BYTE_ARRAY = new ByteArray_c0_30Type();

    public static final Type<Position> POSITION = new Position_c0_30Type();

}
