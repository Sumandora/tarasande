package net.lenni0451.mcstructs.nbt.snbt.impl.v1_8;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtSerializeException;
import net.lenni0451.mcstructs.nbt.snbt.ISNbtSerializer;
import net.lenni0451.mcstructs.nbt.tags.*;

import java.util.Map;

public class SNbtSerializer_v1_8 implements ISNbtSerializer {

    @Override
    public String serialize(INbtTag tag) throws SNbtSerializeException {
        if (tag instanceof ByteNbt) {
            ByteNbt byteNbt = (ByteNbt) tag;
            return byteNbt.getValue() + "b";
        } else if (tag instanceof ShortNbt) {
            ShortNbt shortNbt = (ShortNbt) tag;
            return shortNbt.getValue() + "s";
        } else if (tag instanceof IntNbt) {
            IntNbt intNbt = (IntNbt) tag;
            return String.valueOf(intNbt.getValue());
        } else if (tag instanceof LongNbt) {
            LongNbt longNbt = (LongNbt) tag;
            return longNbt.getValue() + "L";
        } else if (tag instanceof FloatNbt) {
            FloatNbt floatNbt = (FloatNbt) tag;
            return floatNbt.getValue() + "f";
        } else if (tag instanceof DoubleNbt) {
            DoubleNbt doubleNbt = (DoubleNbt) tag;
            return doubleNbt.getValue() + "d";
        } else if (tag instanceof ByteArrayNbt) {
            ByteArrayNbt byteArrayNbt = (ByteArrayNbt) tag;
            return "[" + byteArrayNbt.getValue().length + " bytes]";
        } else if (tag instanceof StringNbt) {
            StringNbt stringNbt = (StringNbt) tag;
            return "\"" + stringNbt.getValue().replace("\"", "\\\"") + "\"";
        } else if (tag instanceof ListNbt) {
            ListNbt<?> listNbt = (ListNbt<?>) tag;
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < listNbt.size(); i++) {
                if (i != 0) out.append(",");
                out.append(i).append(":").append(this.serialize(listNbt.get(i)));
            }
            return out.append("]").toString();
        } else if (tag instanceof CompoundNbt) {
            CompoundNbt compoundNbt = (CompoundNbt) tag;
            StringBuilder out = new StringBuilder("{");
            for (Map.Entry<String, INbtTag> entry : compoundNbt.getValue().entrySet()) {
                if (out.length() != 1) out.append(",");
                out.append(entry.getKey()).append(":").append(this.serialize(entry.getValue()));
            }
            return out.append("}").toString();
        } else if (tag instanceof IntArrayNbt) {
            IntArrayNbt intArrayNbt = (IntArrayNbt) tag;
            StringBuilder out = new StringBuilder("[");
            for (int i : intArrayNbt.getValue()) out.append(i).append(",");
            return out.append("]").toString();
        }
        throw new SNbtSerializeException(tag.getNbtType());
    }

}
