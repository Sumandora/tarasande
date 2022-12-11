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

public class DoubleNbt implements INbtNumber {

    private double value;

    public DoubleNbt() {
        this(0);
    }

    public DoubleNbt(final double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    @Override
    public byte byteValue() {
        return (byte) (((int) Math.floor(this.value)) & 0xFF);
    }

    @Override
    public short shortValue() {
        return (byte) (((int) Math.floor(this.value)) & 0xFFFF);
    }

    @Override
    public int intValue() {
        return (int) Math.floor(this.value);
    }

    @Override
    public long longValue() {
        return (long) Math.floor(this.value);
    }

    @Override
    public float floatValue() {
        return (float) this.value;
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
        return NbtType.DOUBLE;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(128);
        this.value = in.readDouble();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(this.value);
    }

    @Override
    public INbtTag copy() {
        return new DoubleNbt(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DoubleNbt doubleNbt = (DoubleNbt) o;
        return Double.compare(doubleNbt.value, value) == 0;
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
