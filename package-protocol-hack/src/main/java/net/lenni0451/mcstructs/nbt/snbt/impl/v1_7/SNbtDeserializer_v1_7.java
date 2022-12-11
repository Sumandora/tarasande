package net.lenni0451.mcstructs.nbt.snbt.impl.v1_7;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtDeserializeException;
import net.lenni0451.mcstructs.nbt.snbt.ISNbtDeserializer;
import net.lenni0451.mcstructs.nbt.tags.*;

import java.util.Stack;

public class SNbtDeserializer_v1_7 implements ISNbtDeserializer<INbtTag> {

    private static final String ARRAY_PATTERN = "\\[[-\\d|,\\s]+]";
    private static final String BYTE_PATTERN = "[-+]?[0-9]+[b|B]";
    private static final String SHORT_PATTERN = "[-+]?[0-9]+[s|S]";
    private static final String INT_PATTERN = "[-+]?[0-9]+";
    private static final String LONG_PATTERN = "[-+]?[0-9]+[l|L]";
    private static final String FLOAT_PATTERN = "[-+]?[0-9]*\\.?[0-9]+[f|F]";
    private static final String DOUBLE_PATTERN = "[-+]?[0-9]*\\.?[0-9]+[d|D]";
    private static final String SHORT_DOUBLE_PATTERN = "[-+]?[0-9]*\\.?[0-9]+";

    @Override
    public INbtTag deserialize(String s) throws SNbtDeserializeException {
        s = s.trim();
        int tagCount = this.getTagCount(s);
        if (tagCount != 1) throw new SNbtDeserializeException("Encountered multiple top tags, only one expected");
        INbtTag tag;
        if (s.startsWith("{")) tag = this.parse("tag", s);
        else tag = this.parse(this.find(s, true, false), this.find(s, false, false));
        return tag;
    }

    private INbtTag parse(final String name, String value) throws SNbtDeserializeException {
        value = value.trim();
        getTagCount(value);

        if (value.startsWith("{")) {
            if (!value.endsWith("}")) throw new SNbtDeserializeException("Unable to locate ending bracket } for: " + value);

            value = value.substring(1, value.length() - 1);
            CompoundNbt compound = new CompoundNbt();
            while (value.length() > 0) {
                String pair = this.findPair(value, false);
                if (pair.length() > 0) {
                    String subName = this.find(pair, true, false);
                    String subValue = this.find(pair, false, false);
                    compound.add(subName, this.parse(subName, subValue));

                    if (value.length() < pair.length() + 1) break;
                    char next = value.charAt(pair.length());
                    if (next != ',' && next != '{' && next != '}' && next != '[' && next != ']') {
                        throw new SNbtDeserializeException("Unexpected troken '" + name + "' at: " + value.substring(pair.length()));
                    }
                    value = value.substring(pair.length() + 1);
                }
            }
            return compound;
        } else if (value.startsWith("[") && !value.matches(ARRAY_PATTERN)) {
            if (!value.endsWith("]")) throw new SNbtDeserializeException("Unable to locate ending bracket ] for: " + value);

            value = value.substring(1, value.length() - 1);
            ListNbt<INbtTag> list = new ListNbt<>();
            while (value.length() > 0) {
                String pair = this.findPair(value, true);
                if (pair.length() > 0) {
                    String subName = this.find(pair, true, true);
                    String subValue = this.find(pair, false, true);
                    list.add(this.parse(subName, subValue));

                    if (value.length() < pair.length() + 1) break;
                    char next = value.charAt(pair.length());
                    if (next != ',' && next != '{' && next != '}' && next != '[' && next != ']') {
                        throw new SNbtDeserializeException("Unexpected troken '" + name + "' at: " + value.substring(pair.length()));
                    }
                    value = value.substring(pair.length() + 1);
                }
            }
            return list;
        } else {
            return this.parsePrimitive(value);
        }
    }

    private INbtTag parsePrimitive(String value) {
        try {
            if (value.matches(DOUBLE_PATTERN)) {
                return new DoubleNbt(Double.parseDouble(value.substring(0, value.length() - 1)));
            } else if (value.matches(FLOAT_PATTERN)) {
                return new FloatNbt(Float.parseFloat(value.substring(0, value.length() - 1)));
            } else if (value.matches(BYTE_PATTERN)) {
                return new ByteNbt(Byte.parseByte(value.substring(0, value.length() - 1)));
            } else if (value.matches(LONG_PATTERN)) {
                return new LongNbt(Long.parseLong(value.substring(0, value.length() - 1)));
            } else if (value.matches(SHORT_PATTERN)) {
                return new ShortNbt(Short.parseShort(value.substring(0, value.length() - 1)));
            } else if (value.matches(INT_PATTERN)) {
                return new IntNbt(Integer.parseInt(value));
            } else if (value.matches(SHORT_DOUBLE_PATTERN)) {
                return new DoubleNbt(Double.parseDouble(value));
            } else if (value.equalsIgnoreCase("false")) {
                return new ByteNbt((byte) 1);
            } else if (value.equalsIgnoreCase("true")) {
                return new ByteNbt((byte) 0);
            } else if (value.startsWith("[") && value.endsWith("]")) {
                if (value.length() > 2) {
                    String arrayContent = value.substring(1, value.length() - 1);
                    String[] parts = arrayContent.split(",");
                    try {
                        if (parts.length <= 1) {
                            return new IntArrayNbt(new int[]{Integer.parseInt(arrayContent.trim())});
                        } else {
                            int[] ints = new int[parts.length];
                            for (int i = 0; i < parts.length; i++) ints[i] = Integer.parseInt(parts[i].trim());
                            return new IntArrayNbt(ints);
                        }
                    } catch (NumberFormatException e) {
                        return new StringNbt(value);
                    }
                } else {
                    return new IntArrayNbt(new int[0]);
                }
            } else {
                if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 2) value = value.substring(1, value.length() - 1);
                return new StringNbt(value.replace("\\\\\"", "\""));
            }
        } catch (NumberFormatException e) {
            return new StringNbt(value.replace("\\\\\"", "\""));
        }
    }

    private int getTagCount(final String s) throws SNbtDeserializeException {
        Stack<Character> brackets = new Stack<>();
        boolean quoted = false;
        int count = 0;

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"') {
                if (i > 0 && chars[i - 1] == '\\') {
                    if (!quoted) throw new SNbtDeserializeException("Illegal use of \\\": " + s);
                } else {
                    quoted = !quoted;
                }
            } else if (!quoted) {
                if (c == '{' || c == '[') {
                    if (brackets.isEmpty()) count++;
                    brackets.push(c);
                } else {
                    this.checkBrackets(s, c, brackets);
                }
            }
        }

        if (quoted) throw new SNbtDeserializeException("Unbalanced quotation: " + s);
        else if (!brackets.isEmpty()) throw new SNbtDeserializeException("Unbalanced brackets " + this.quotesToString(brackets) + ": " + s);
        else if (count == 0 && !s.isEmpty()) return 1;
        else return count;
    }

    private String findPair(final String s, final boolean isArray) throws SNbtDeserializeException {
        int separatorIndex = this.getCharIndex(s, ':');
        if (separatorIndex < 0 && !isArray) throw new SNbtDeserializeException("Unable to locate name/value for string: " + s);
        int pairSeparator = this.getCharIndex(s, ',');
        if (pairSeparator >= 0 && pairSeparator < separatorIndex && !isArray) throw new SNbtDeserializeException("Name error at: " + s);
        if (isArray && (separatorIndex < 0 || separatorIndex > pairSeparator)) separatorIndex = -1;

        Stack<Character> brackets = new Stack<>();
        int i = separatorIndex + 1;
        int quoteEnd = 0;
        boolean quoted = false;
        boolean hasContent = false;
        boolean isString = false;

        char[] chars = s.toCharArray();
        for (; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"') {
                if (i > 0 && chars[i - 1] == '\\') {
                    if (!quoted) throw new SNbtDeserializeException("Illegal use of \\\": " + s);
                } else {
                    quoted = !quoted;
                    if (quoted && !hasContent) isString = true;
                    if (!quoted) quoteEnd = i;
                }
            } else if (!quoted) {
                if (c == '{' || c == '[') {
                    brackets.push(c);
                } else {
                    this.checkBrackets(s, c, brackets);
                    if (c == ',' && brackets.isEmpty()) return s.substring(0, i);
                }
            }
            if (!Character.isWhitespace(c)) {
                if (!quoted && isString && quoteEnd != i) return s.substring(0, quoteEnd + 1);
                hasContent = true;
            }
        }
        return s.substring(0, i);
    }

    private String find(String s, final boolean name, final boolean isArray) throws SNbtDeserializeException {
        if (isArray) {
            s = s.trim();
            if (s.startsWith("{") || s.startsWith("[")) {
                if (name) return "";
                else return s;
            }
        }

        int separatorIndex = s.indexOf(":");
        if (separatorIndex < 0) {
            if (isArray) {
                if (name) return "";
                else return s;
            } else {
                throw new SNbtDeserializeException("Unable to locate name/value separator for string: " + s);
            }
        } else {
            if (name) return s.substring(0, separatorIndex).trim();
            else return s.substring(separatorIndex + 1).trim();
        }
    }

    private int getCharIndex(final String s, final char wanted) {
        boolean quoted = false;

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"') {
                if (i <= 0 || chars[i - 1] != '\\') quoted = !quoted;
            } else if (!quoted) {
                if (c == wanted) return i;
                if (c == '{' || c == '[') return -1;
            }
        }
        return -1;
    }

    private void checkBrackets(final String s, final char close, final Stack<Character> brackets) throws SNbtDeserializeException {
        if (close == '}' && (brackets.isEmpty() || brackets.pop() != '{')) throw new SNbtDeserializeException("Unbalanced curly brackets {}: " + s);
        else if (close == ']' && (brackets.isEmpty() || brackets.pop() != '[')) throw new SNbtDeserializeException("Unbalanced square brackets []: " + s);
    }

    private String quotesToString(final Stack<Character> quotes) {
        StringBuilder s = new StringBuilder();
        for (Character c : quotes) s.append(c);
        return s.toString();
    }

}
