package de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type.impl;

import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;

public enum MetaTypeb1_2 implements MetaType {

    Byte(0, Type.BYTE),
    Short(1, Type.SHORT),
    Int(2, Type.INT),
    Float(3, Type.FLOAT),
    String(4, Typeb1_7_0_3.STRING),
    Slot(5, Type1_3_1_2.NBTLESS_ITEM);

    private final int typeID;
    private final Type<?> type;

    MetaTypeb1_2(int typeID, Type<?> type) {
        this.typeID = typeID;
        this.type = type;
    }

    public static MetaTypeb1_2 byId(int id) {
        return values()[id];
    }

    public int typeId() {
        return this.typeID;
    }

    public Type<?> type() {
        return this.type;
    }

}
