package net.lenni0451.mcstructs.core;

public class Identifier {

    public static final String VALID_KEY_CHARS = "[_\\-a-z.]";
    public static final String VALID_VALUE_CHARS = "[_\\-a-z/.]";

    public static Identifier of(final String value) {
        String[] parts = value.split(":");
        if (parts.length == 1) return of("minecraft", parts[0]);
        else if (parts.length == 2) return of(parts[0], parts[1]);
        else throw new IllegalArgumentException("The given value has to be splittable by :");
    }

    public static Identifier tryOf(final String value) {
        try {
            return of(value);
        } catch (Throwable t) {
            return null;
        }
    }

    public static Identifier of(final String key, final String value) {
        return new Identifier(key, value);
    }


    private final String key;
    private final String value;

    private Identifier(final String key, final String value) {
        if (!key.matches(VALID_KEY_CHARS)) throw new IllegalArgumentException("Key contains illegal chars");
        if (!value.matches(VALID_VALUE_CHARS)) throw new IllegalArgumentException("Value contains illegal chars");

        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

}
