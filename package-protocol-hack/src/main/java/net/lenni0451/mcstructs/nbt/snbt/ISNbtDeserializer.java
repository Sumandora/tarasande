package net.lenni0451.mcstructs.nbt.snbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtDeserializeException;

public interface ISNbtDeserializer<T extends INbtTag> {

    T deserialize(final String s) throws SNbtDeserializeException;

}
