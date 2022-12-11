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

public class ByteNbt implements INbtNumber {

    private byte value;

    public ByteNbt() {
        this((byte) 0);
    }

    public ByteNbt(final byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    public void setValue(final byte value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return this.value;
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
        return NbtType.BYTE;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(72);
        this.value = in.readByte();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.value);
    }

    @Override
    public INbtTag copy() {
        return new ByteNbt(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteNbt byteNbt = (ByteNbt) o;
        return value == byteNbt.value;
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
