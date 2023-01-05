package de.florianmichael.viabeta.api.model;

import java.util.Objects;

public class IdAndData {

    public int id;
    public int data;

    public IdAndData(final int id, final int data) {
        if (data < 0 || data > 15) throw new IllegalArgumentException("Block data out of bounds (id:" + id + " data:" + data + ")");
        this.id = id;
        this.data = data;
    }

    public static IdAndData fromCompressedData(final int idAndData) {
        return new IdAndData(idAndData >> 4, idAndData & 15);
    }

    public static int toCompressedData(final int id, final int data) {
        return id << 4 | data & 15;
    }

    public int toCompressedData() {
        return toCompressedData(this.id, this.data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdAndData idAndData = (IdAndData) o;
        return id == idAndData.id &&
                data == idAndData.data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }

    @Override
    public String toString() {
        return "IdAndData{" +
                "id=" + id +
                ", data=" + data +
                '}';
    }

}
