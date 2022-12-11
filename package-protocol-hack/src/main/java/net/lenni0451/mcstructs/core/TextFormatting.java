package net.lenni0451.mcstructs.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextFormatting {

    public static final List<TextFormatting> ALL = new ArrayList<>();
    public static final List<TextFormatting> COLORS = new ArrayList<>();
    public static final List<TextFormatting> FORMATTINGS = new ArrayList<>();
    public static final TextFormatting BLACK = new TextFormatting("BLACK", '0', 0x00_00_00);
    public static final TextFormatting DARK_BLUE = new TextFormatting("DARK_BLUE", '1', 0x00_00_AA);
    public static final TextFormatting DARK_GREEN = new TextFormatting("DARK_GREEN", '2', 0x00_AA_00);
    public static final TextFormatting DARK_AQUA = new TextFormatting("DARK_AQUA", '3', 0x00_AA_AA);
    public static final TextFormatting DARK_RED = new TextFormatting("DARK_RED", '4', 0xAA_00_00);
    public static final TextFormatting DARK_PURPLE = new TextFormatting("DARK_PURPLE", '5', 0xAA_00_AA);
    public static final TextFormatting GOLD = new TextFormatting("GOLD", '6', 0xFF_AA_00);
    public static final TextFormatting GRAY = new TextFormatting("GRAY", '7', 0xAA_AA_AA);
    public static final TextFormatting DARK_GRAY = new TextFormatting("DARK_GRAY", '8', 0x55_55_55);
    public static final TextFormatting BLUE = new TextFormatting("BLUE", '9', 0x55_55_FF);
    public static final TextFormatting GREEN = new TextFormatting("GREEN", 'a', 0x55_FF_55);
    public static final TextFormatting AQUA = new TextFormatting("AQUA", 'b', 0x55_FF_FF);
    public static final TextFormatting RED = new TextFormatting("RED", 'c', 0xFF_55_55);
    public static final TextFormatting LIGHT_PURPLE = new TextFormatting("LIGHT_PURPLE", 'd', 0xFF_55_FF);
    public static final TextFormatting YELLOW = new TextFormatting("YELLOW", 'e', 0xFF_FF_55);
    public static final TextFormatting WHITE = new TextFormatting("WHITE", 'f', 0xFF_FF_FF);
    public static final TextFormatting OBFUSCATED = new TextFormatting("OBFUSCATED", 'k');
    public static final TextFormatting BOLD = new TextFormatting("BOLD", 'l');
    public static final TextFormatting STRIKETHROUGH = new TextFormatting("STRIKETHROUGH", 'm');
    public static final TextFormatting UNDERLINE = new TextFormatting("UNDERLINE", 'n');
    public static final TextFormatting ITALIC = new TextFormatting("ITALIC", 'o');
    public static final TextFormatting RESET = new TextFormatting("RESET", 'r', -1);

    public static TextFormatting getByName(final String name) {
        for (TextFormatting formatting : ALL) {
            if (formatting.getName().equalsIgnoreCase(name)) return formatting;
        }
        return null;
    }

    public static TextFormatting parse(final String s) {
        if (s.startsWith("#")) return new TextFormatting(Integer.parseInt(s.substring(1), 16));
        else return getByName(s);
    }


    private final Type type;
    private final String name;
    private final char code;
    private final int rgbValue;

    private TextFormatting(final String name, final char code, final int rgbValue) {
        this.type = Type.COLOR;
        this.name = name;
        this.code = code;
        this.rgbValue = rgbValue;

        ALL.add(this);
        COLORS.add(this);
    }

    private TextFormatting(final String name, final char code) {
        this.type = Type.FORMATTING;
        this.name = name;
        this.code = code;
        this.rgbValue = -1;

        ALL.add(this);
        FORMATTINGS.add(this);
    }

    public TextFormatting(final int rgbValue) {
        this.type = Type.RGB;
        this.name = "RGB_COLOR";
        this.code = '\0';
        this.rgbValue = rgbValue & 0xFF_FF_FF;
    }

    public boolean isColor() {
        return Type.COLOR.equals(this.type) || Type.RGB.equals(this.type);
    }

    public boolean isRGBColor() {
        return Type.RGB.equals(this.type);
    }

    public boolean isFormatting() {
        return Type.FORMATTING.equals(this.type);
    }

    public String getName() {
        return this.name;
    }

    public char getCode() {
        return this.code;
    }

    public int getRgbValue() {
        return this.rgbValue;
    }

    public String serialize() {
        if (Type.RGB.equals(this.type)) return "#" + String.format("%06X", this.rgbValue);
        else return this.name.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextFormatting that = (TextFormatting) o;
        return code == that.code && rgbValue == that.rgbValue && type == that.type && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, code, rgbValue);
    }

    @Override
    public String toString() {
        return "TextFormatting{type=" + type + ", name='" + name + '\'' + ", code=" + code + ", rgbValue=" + rgbValue + "}";
    }


    private enum Type {
        COLOR, FORMATTING, RGB
    }

}
