package net.lenni0451.mcstructs.nbt.exceptions;

public class UnknownTagTypeException extends IllegalStateException {

    public UnknownTagTypeException(final int typeId) {
        super("Unknown tag type id " + typeId);
    }

    public UnknownTagTypeException(final Class<?> typeClass) {
        super("Unknown tag type class " + typeClass.getName());
    }

}
