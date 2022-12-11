package net.lenni0451.mcstructs.nbt;

import java.util.Objects;

public class NbtHeader {

    public static NbtHeader END = new NbtHeader(NbtType.END, null);


    private final NbtType type;
    private final String name;

    public NbtHeader(final NbtType type, final String name) {
        Objects.requireNonNull(type);

        this.type = type;
        this.name = name;
    }

    public NbtType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnd() {
        return NbtType.END.equals(this.type);
    }

}
