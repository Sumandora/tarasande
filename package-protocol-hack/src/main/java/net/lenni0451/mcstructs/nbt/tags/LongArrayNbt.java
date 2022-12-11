package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayNbt implements INbtTag {

    private long[] value;

    public LongArrayNbt() {
        this(new long[0]);
    }

    public LongArrayNbt(final ListNbt<LongNbt> list) {
        this.value = new long[list.size()];
        for (int i = 0; i < list.size(); i++) this.value[i] = list.get(i).getValue();
    }

    public LongArrayNbt(final long[] value) {
        this.value = value;
    }

    public long[] getValue() {
        return this.value;
    }

    public void setValue(long[] value) {
        this.value = value;
    }

    public long get(final int index) {
        return this.value[index];
    }

    public void set(final int index, final long l) {
        this.value[index] = l;
    }

    public void add(final long l) {
        long[] newValue = new long[this.value.length + 1];
        System.arraycopy(this.value, 0, newValue, 0, this.value.length);
        newValue[this.value.length] = l;
        this.value = newValue;
    }

    public int getLength() {
        return this.value.length;
    }

    public boolean isEmpty() {
        return this.value.length == 0;
    }

    @Override
    public NbtType getNbtType() {
        return NbtType.LONG_ARRAY;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(192);
        int length = in.readInt();
        readTracker.read(64 * length);
        this.value = new long[length];
        for (int i = 0; i < this.value.length; i++) this.value[i] = in.readLong();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        for (long l : this.value) out.writeLong(l);
    }

    @Override
    public INbtTag copy() {
        return new LongArrayNbt(this.value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongArrayNbt that = (LongArrayNbt) o;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return SNbtSerializer.V1_14.trySerialize(this);
    }

}
