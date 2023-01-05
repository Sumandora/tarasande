package de.florianmichael.viabeta.api.rewriter.legacy;


import com.viaversion.viaversion.libs.opennbt.tag.TagRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Deprecated // Replace with mcstructs
public class ViaStringTagReader1_12_2 {

    private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private final String JSON_TAG;
    private int currentPosition;

    private ViaStringTagReader1_12_2(String jsonTag) {
        this.JSON_TAG = jsonTag;
    }

    public static CompoundTag getTagFromJson(String jsonString) throws NBTException {
        return (new ViaStringTagReader1_12_2(jsonString)).readCompound();
    }

    private CompoundTag readCompound() throws NBTException {
        CompoundTag nbttagcompound = this.parseCompound();
        this.skipWhitespace();

        if (this.canRead()) {
            ++this.currentPosition;
            throw this.makeError("Trailing data found");
        } else {
            return nbttagcompound;
        }
    }

    private String readString() throws NBTException {
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.makeError("Expected key");
        } else {
            return this.peek() == '"' ? this.readQuotedString() : this.readUnquotedString();
        }
    }

    private NBTException makeError(String message) {
        return new NBTException(message, this.JSON_TAG, this.currentPosition);
    }

    private Tag parseTagPrimitive() throws NBTException {
        this.skipWhitespace();

        if (this.peek() == '"') {
            return new StringTag(this.readQuotedString());
        } else {
            String s = this.readUnquotedString();

            if (s.isEmpty()) {
                throw this.makeError("Expected value");
            } else {
                return this.parsePrimitive(s);
            }
        }
    }

    private Tag parsePrimitive(String input) {
        try {
            if (FLOAT_PATTERN.matcher(input).matches()) {
                return new FloatTag(Float.parseFloat(input.substring(0, input.length() - 1)));
            }

            if (BYTE_PATTERN.matcher(input).matches()) {
                return new ByteTag(Byte.parseByte(input.substring(0, input.length() - 1)));
            }

            if (LONG_PATTERN.matcher(input).matches()) {
                return new LongTag(Long.parseLong(input.substring(0, input.length() - 1)));
            }

            if (SHORT_PATTERN.matcher(input).matches()) {
                return new ShortTag(Short.parseShort(input.substring(0, input.length() - 1)));
            }

            if (INT_PATTERN.matcher(input).matches()) {
                return new IntTag(Integer.parseInt(input));
            }

            if (DOUBLE_PATTERN.matcher(input).matches()) {
                return new DoubleTag(Double.parseDouble(input.substring(0, input.length() - 1)));
            }

            if (DOUBLE_PATTERN_IMPLICIT.matcher(input).matches()) {
                return new DoubleTag(Double.parseDouble(input));
            }

            if ("true".equalsIgnoreCase(input)) {
                return new ByteTag((byte) 1);
            }

            if ("false".equalsIgnoreCase(input)) {
                return new ByteTag((byte) 0);
            }
        } catch (NumberFormatException ignored) {
        }

        return new StringTag(input);
    }

    private String readQuotedString() throws NBTException {
        int i = ++this.currentPosition;
        StringBuilder stringbuilder = null;
        boolean flag = false;

        while (this.canRead()) {
            char c0 = this.read();

            if (flag) {
                if (c0 != '\\' && c0 != '"') {
                    throw this.makeError("Invalid escape of '" + c0 + "'");
                }

                flag = false;
            } else {
                if (c0 == '\\') {
                    flag = true;

                    if (stringbuilder == null) {
                        stringbuilder = new StringBuilder(this.JSON_TAG.substring(i, this.currentPosition - 1));
                    }

                    continue;
                }

                if (c0 == '"') {
                    return stringbuilder == null ? this.JSON_TAG.substring(i, this.currentPosition - 1) : stringbuilder.toString();
                }
            }

            if (stringbuilder != null) {
                stringbuilder.append(c0);
            }
        }

        throw this.makeError("Missing termination quote");
    }

    private String readUnquotedString() {
        int i = this.currentPosition;

        while (this.canRead() && this.isAllowedInUnquotedString(this.peek())) {
            this.currentPosition++;
        }

        return this.JSON_TAG.substring(i, this.currentPosition);
    }

    private Tag parseTag() throws NBTException {
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.makeError("Expected value");
        } else {
            char c0 = this.peek();

            if (c0 == '{') {
                return this.parseCompound();
            } else {
                return c0 == '[' ? this.parseTagArray() : this.parseTagPrimitive();
            }
        }
    }

    private Tag parseTagArray() throws NBTException {
        return this.canRead(2) && this.peek(1) != '"' && this.peek(2) == ';' ? this.parseTagPrimitiveArray() : this.parseListTag();
    }

    private CompoundTag parseCompound() throws NBTException {
        this.expect('{');
        CompoundTag nbttagcompound = new CompoundTag();
        this.skipWhitespace();

        while (this.canRead() && this.peek() != '}') {
            String key = this.readString();

            if (key.isEmpty()) {
                throw this.makeError("Expected non-empty key");
            }

            this.expect(':');
            Tag tag = this.parseTag();
            nbttagcompound.put(key, tag);

            if (!this.readComma()) {
                break;
            }

            if (!this.canRead()) {
                throw this.makeError("Expected key");
            }
        }

        this.expect('}');
        return nbttagcompound;
    }

    private Tag parseListTag() throws NBTException {
        this.expect('[');
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.makeError("Expected value");
        } else {
            ListTag nbttaglist = new ListTag();
            int i = -1;

            while (this.peek() != ']') {
                Tag nbtbase = this.parseTag();
                int j = TagRegistry.getIdFor(nbtbase.getClass());

                if (i < 0) {
                    i = j;
                } else if (j != i) {
                    throw this.makeError("Unable to insert " + TagRegistry.getClassFor(j).getSimpleName() + " into ListTag of type " + TagRegistry.getClassFor(i).getSimpleName());
                }

                nbttaglist.add(nbtbase);

                if (!this.readComma()) {
                    break;
                }

                if (!this.canRead()) {
                    throw this.makeError("Expected value");
                }
            }

            this.expect(']');
            return nbttaglist;
        }
    }

    private Tag parseTagPrimitiveArray() throws NBTException {
        this.expect('[');
        char c0 = this.read();
        this.read();
        this.skipWhitespace();

        if (!this.canRead()) {
            throw this.makeError("Expected value");
        } else if (c0 == 'B') {
            return new ByteArrayTag(toByteArray(this.readArray((byte) 7, (byte) 1)));
        } else if (c0 == 'L') {
            return new LongArrayTag(toLongArray(this.readArray((byte) 12, (byte) 4)));
        } else if (c0 == 'I') {
            return new IntArrayTag(toIntArray(this.readArray((byte) 11, (byte) 3)));
        } else {
            throw this.makeError("Invalid array type '" + c0 + "' found");
        }
    }

    private static byte[] toByteArray(List<Byte> list) {
        byte[] bs = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Byte byte_ = list.get(i);
            bs[i] = byte_ == null ? 0 : byte_;
        }

        return bs;
    }

    private static long[] toLongArray(List<Long> list) {
        long[] ls = new long[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Long long_ = list.get(i);
            ls[i] = long_ == null ? 0L : long_;
        }

        return ls;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] is = new int[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Integer integer = list.get(i);
            is[i] = integer == null ? 0 : integer;
        }

        return is;
    }

    private <T extends Number> List<T> readArray(byte b, byte c) throws NBTException {
        List<T> list = new ArrayList<>();

        while (true) {
            if (this.peek() != ']') {
                Tag nbtbase = this.parseTag();
                int i = TagRegistry.getIdFor(nbtbase.getClass());

                if (i != c) {
                    throw this.makeError("Unable to insert " + TagRegistry.getClassFor(i).getSimpleName() + " into " + TagRegistry.getClassFor(b).getSimpleName());
                }

                if (c == 1) {
                    list.add((T) ((ByteTag) nbtbase).getValue());
                } else if (c == 4) {
                    list.add((T) ((LongTag) nbtbase).getValue());
                } else {
                    list.add((T) ((IntTag) nbtbase).getValue());
                }

                if (this.readComma()) {
                    if (!this.canRead()) {
                        throw this.makeError("Expected value");
                    }

                    continue;
                }
            }

            this.expect(']');
            return list;
        }
    }

    private void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) {
            ++this.currentPosition;
        }
    }

    private boolean readComma() {
        this.skipWhitespace();

        if (this.canRead() && this.peek() == ',') {
            ++this.currentPosition;
            this.skipWhitespace();
            return true;
        } else {
            return false;
        }
    }

    private void expect(char expectedChar) throws NBTException {
        this.skipWhitespace();
        boolean flag = this.canRead();

        if (flag && this.peek() == expectedChar) {
            ++this.currentPosition;
        } else {
            throw new NBTException("Expected '" + expectedChar + "' but got '" + (flag ? this.peek() : "<EOF>") + "'", this.JSON_TAG, this.currentPosition + 1);
        }
    }

    private boolean isAllowedInUnquotedString(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_' || c == '-' || c == '.' || c == '+';
    }

    private boolean canRead(int offset) {
        return this.currentPosition + offset < this.JSON_TAG.length();
    }

    private boolean canRead() {
        return this.canRead(0);
    }

    private char peek(int offset) {
        return this.JSON_TAG.charAt(this.currentPosition + offset);
    }

    private char peek() {
        return this.peek(0);
    }

    private char read() {
        return this.JSON_TAG.charAt(this.currentPosition++);
    }

    public static final class NBTException extends Exception {
        public NBTException(String message, String jsonTag, int position) {
            super(message + " at: " + generate(jsonTag, position));
        }

        private static String generate(String jsonTag, int position) {
            StringBuilder stringbuilder = new StringBuilder();
            int i = Math.min(jsonTag.length(), position);

            if (i > 35) {
                stringbuilder.append("...");
            }

            stringbuilder.append(jsonTag, Math.max(0, i - 35), i);
            stringbuilder.append("<--[HERE]");
            return stringbuilder.toString();
        }
    }

}
