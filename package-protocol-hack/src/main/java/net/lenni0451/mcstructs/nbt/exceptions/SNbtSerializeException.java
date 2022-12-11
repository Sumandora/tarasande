package net.lenni0451.mcstructs.nbt.exceptions;

import net.lenni0451.mcstructs.nbt.NbtType;

public class SNbtSerializeException extends Exception {

    public SNbtSerializeException(final NbtType type) {
        super("Unable to serialize nbt type " + type.name());
    }

}
