package net.lenni0451.mcstructs.nbt.tags;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.NbtType;
import net.lenni0451.mcstructs.nbt.exceptions.NbtReadException;
import net.lenni0451.mcstructs.nbt.snbt.SNbtSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListNbt<T extends INbtTag> implements INbtTag {

    private NbtType type;
    private List<T> value;

    public ListNbt() {
        this(null, new ArrayList<>());
    }

    public ListNbt(final NbtType type) {
        this(type, new ArrayList<>());
    }

    public ListNbt(final List<T> list) {
        if (!list.isEmpty()) {
            this.type = NbtType.byClass(list.get(0).getClass());
            if (list.stream().anyMatch(tag -> !tag.getNbtType().equals(this.type))) throw new IllegalArgumentException("Tried to create list with multiple nbt types");
        }
        this.value = list;
    }

    public ListNbt(final NbtType type, final List<T> value) {
        this.type = type;
        this.value = value;
    }

    public NbtType getType() {
        return this.type;
    }

    public void setType(final NbtType type) {
        this.type = type;
    }

    public List<T> getValue() {
        return this.value;
    }

    public void setValue(final List<T> value) {
        this.value = value;
    }

    public T get(final int index) {
        return this.value.get(index);
    }

    public void add(final T tag) {
        this.check(tag);
        this.value.add(tag);
    }

    public void set(final int index, final T tag) {
        this.check(tag);
        this.value.set(index, tag);
    }

    public void remove(final T tag) {
        this.check(tag);
        this.value.remove(tag);
    }

    public boolean canAdd(final INbtTag tag) {
        if (this.type == null || this.value.isEmpty()) return true;
        return this.type.equals(tag.getNbtType());
    }

    public boolean canAdd(final NbtType type) {
        if (this.type == null || this.value.isEmpty()) return true;
        return this.type.equals(type);
    }

    public boolean trim() {
        if (this.value.isEmpty()) return true;
        if (NbtType.COMPOUND.equals(this.type)) this.value.forEach(tag -> ((CompoundNbt) tag).trim());
        else if (NbtType.LIST.equals(this.type)) this.value.forEach(tag -> ((ListNbt<?>) tag).trim());
        return false;
    }

    public int size() {
        return this.value.size();
    }

    public boolean isEmpty() {
        return this.value.isEmpty();
    }

    private void check(final T tag) {
        if (this.type == null || this.value.isEmpty()) {
            this.type = tag.getNbtType();
            this.value.clear();
        } else if (!this.type.equals(tag.getNbtType())) {
            throw new IllegalArgumentException("Can't add " + tag.getClass().getSimpleName() + " to a " + this.type.name() + " list");
        }
    }

    @Override
    public NbtType getNbtType() {
        return NbtType.LIST;
    }

    @Override
    public void read(DataInput in, NbtReadTracker readTracker) throws IOException {
        readTracker.read(296);
        int typeId = in.readByte();
        int count = in.readInt();
        if (typeId == NbtType.END.getId() && count > 0) throw new NbtReadException("ListNbt with type END and count > 0");
        readTracker.read(32 * count);
        this.type = NbtType.byId(typeId);
        this.value = new ArrayList<>(Math.min(count, 512));
        for (int i = 0; i < count; i++) {
            T tag = (T) this.type.newInstance();
            readTracker.pushDepth();
            tag.read(in, readTracker);
            readTracker.popDepth();
            this.value.add(tag);
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(this.type.getId());
        out.writeInt(this.value.size());
        for (T tag : this.value) tag.write(out);
    }

    @Override
    public INbtTag copy() {
        List<INbtTag> value = new ArrayList<>();
        for (T val : this.value) value.add(val.copy());
        return new ListNbt<>(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListNbt<?> listNbt = (ListNbt<?>) o;
        return Objects.equals(type, listNbt.type) && Objects.equals(value, listNbt.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return SNbtSerializer.V1_14.trySerialize(this);
    }

}
