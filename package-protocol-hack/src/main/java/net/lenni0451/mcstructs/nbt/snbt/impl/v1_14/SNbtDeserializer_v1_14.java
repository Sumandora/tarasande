package net.lenni0451.mcstructs.nbt.snbt.impl.v1_14;

import net.lenni0451.mcstructs.nbt.snbt.impl.v1_13.SNbtDeserializer_v1_13;

public class SNbtDeserializer_v1_14 extends SNbtDeserializer_v1_13 {

    @Override
    protected boolean isQuote(char c) {
        return c == '"' || c == '\'';
    }

}
