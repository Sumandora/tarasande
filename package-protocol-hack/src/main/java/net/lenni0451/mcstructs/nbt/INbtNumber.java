package net.lenni0451.mcstructs.nbt;

public interface INbtNumber extends INbtTag {

    byte byteValue();

    short shortValue();

    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

    Number numberValue();

}
