package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayNbt implements INbtTag {

    private byte[] value;

    public ByteArrayNbt() {
        this(new byte[0]);
    }

    public ByteArrayNbt(final ListNbt<ByteNbt> list) {
        this.value = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) this.value[i] = list.get(i).getValue();
    }

    public ByteArrayNbt(final byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(final byte[] value) {
        this.value = value;
    }

    public byte get(final int index) {
        return this.value[index];
    }

    public void set(final int index, final byte b) {
        this.value[index] = b;
    }

    public void add(final byte b) {
        byte[] newValue = new byte[this.value.length + 1];
        System.arraycopy(this.value, 0, newValue, 0, this.value.length);
        newValue[this.value.length] = b;
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
        return NbtType.BYTE_ARRAY;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(192);
        int length = in.readInt();
        readTracker.read(length * 8);
        byte[] value = new byte[length];
        in.readFully(value);
        this.value = value;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(this.value.length);
        out.write(this.value);
    }

    @Override
    public INbtTag copy() {
        return new ByteArrayNbt(this.value.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ByteArrayNbt that = (ByteArrayNbt) o;
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
