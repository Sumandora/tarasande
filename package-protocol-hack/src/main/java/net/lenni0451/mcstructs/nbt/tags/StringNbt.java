package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class StringNbt implements INbtTag {

    private String value;

    public StringNbt() {
        this("");
    }

    public StringNbt(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public NbtType getNbtType() {
        return NbtType.STRING;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(288);
        this.value = in.readUTF();
        readTracker.read(16 * this.value.length());
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(this.value);
    }

    @Override
    public INbtTag copy() {
        return new StringNbt(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringNbt stringNbt = (StringNbt) o;
        return Objects.equals(value, stringNbt.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return SNbtSerializer.V1_14.trySerialize(this);
    }

}
