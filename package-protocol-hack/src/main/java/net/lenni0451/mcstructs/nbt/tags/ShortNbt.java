package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtNumber;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class ShortNbt implements INbtNumber {

    private short value;

    public ShortNbt() {
        this((short) 0);
    }

    public ShortNbt(final short value) {
        this.value = value;
    }

    public short getValue() {
        return this.value;
    }

    public void setValue(final short value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFF);
    }

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public NbtType getNbtType() {
        return NbtType.SHORT;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(80);
        this.value = in.readShort();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeShort(this.value);
    }

    @Override
    public INbtTag copy() {
        return new ShortNbt(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShortNbt shortNbt = (ShortNbt) o;
        return value == shortNbt.value;
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
