package net.lenni0451.mcstructs.nbt.snbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtSerializeException;

public interface ISNbtSerializer {

    String serialize(final INbtTag tag) throws SNbtSerializeException;

}
