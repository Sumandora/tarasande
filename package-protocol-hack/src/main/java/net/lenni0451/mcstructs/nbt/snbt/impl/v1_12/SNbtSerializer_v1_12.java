package net.lenni0451.mcstructs.nbt.snbt.impl.v1_12;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtSerializeException;
import net.lenni0451.mcstructs.nbt.snbt.ISNbtSerializer;
import net.lenni0451.mcstructs.nbt.tags.*;

import java.util.Map;
import java.util.regex.Pattern;

public class SNbtSerializer_v1_12 implements ISNbtSerializer {

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("[A-Za-z0-9._+-]+");

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
            StringBuilder out = new StringBuilder("[B;");
            for (int i = 0; i < byteArrayNbt.getLength(); i++) {
                if (i != 0) out.append(",");
                out.append(byteArrayNbt.get(i)).append("B");
            }
            return out.append("]").toString();
        } else if (tag instanceof StringNbt) {
            StringNbt stringNbt = (StringNbt) tag;
            return this.escape(stringNbt.getValue());
        } else if (tag instanceof ListNbt) {
            ListNbt<?> listNbt = (ListNbt<?>) tag;
            StringBuilder out = new StringBuilder("[");
            for (int i = 0; i < listNbt.size(); i++) {
                if (i != 0) out.append(",");
                out.append(this.serialize(listNbt.get(i)));
            }
            return out.append("]").toString();
        } else if (tag instanceof CompoundNbt) {
            CompoundNbt compoundNbt = (CompoundNbt) tag;
            StringBuilder out = new StringBuilder("{");
            for (Map.Entry<String, INbtTag> entry : compoundNbt.getValue().entrySet()) {
                if (out.length() != 1) out.append(",");
                out.append(this.checkEscape(entry.getKey())).append(":").append(this.serialize(entry.getValue()));
            }
            return out.append("}").toString();
        } else if (tag instanceof IntArrayNbt) {
            IntArrayNbt intArrayNbt = (IntArrayNbt) tag;
            StringBuilder out = new StringBuilder("[I;");
            for (int i = 0; i < intArrayNbt.getLength(); i++) {
                if (i != 0) out.append(",");
                out.append(intArrayNbt.get(i));
            }
            return out.append("]").toString();
        } else if (tag instanceof LongArrayNbt) {
            LongArrayNbt longArrayNbt = (LongArrayNbt) tag;
            StringBuilder out = new StringBuilder("[L;");
            for (int i = 0; i < longArrayNbt.getLength(); i++) {
                if (i != 0) out.append(",");
                out.append(longArrayNbt.get(i)).append("L");
            }
            return out.append("]").toString();
        }
        throw new SNbtSerializeException(tag.getNbtType());
    }

    protected String checkEscape(final String s) {
        if (ESCAPE_PATTERN.matcher(s).matches()) return s;
        return this.escape(s);
    }

    protected String escape(final String s) {
        StringBuilder out = new StringBuilder("\"");
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == '\\' || c == '"') out.append("\\");
            out.append(c);
        }
        return out.append("\"").toString();
    }

}
