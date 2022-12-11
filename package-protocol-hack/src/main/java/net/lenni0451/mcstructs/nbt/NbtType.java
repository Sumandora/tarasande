package net.lenni0451.mcstructs.nbt;

import net.lenni0451.mcstructs.core.UnsafeUtils;
import net.lenni0451.mcstructs.nbt.tags.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum NbtType {

    END(0, null, null),
    BYTE(1, ByteNbt.class, byte.class),
    SHORT(2, ShortNbt.class, short.class),
    INT(3, IntNbt.class, int.class),
    LONG(4, LongNbt.class, long.class),
    FLOAT(5, FloatNbt.class, float.class),
    DOUBLE(6, DoubleNbt.class, double.class),
    BYTE_ARRAY(7, ByteArrayNbt.class, byte[].class),
    STRING(8, StringNbt.class, String.class),
    LIST(9, ListNbt.class, List.class),
    COMPOUND(10, CompoundNbt.class, Map.class),
    INT_ARRAY(11, IntArrayNbt.class, int[].class),
    LONG_ARRAY(12, LongArrayNbt.class, long[].class);

    public static NbtType byName(final String name) {
        for (NbtType type : NbtType.values()) {
            if (type.name().equals(name)) return type;
        }
        return null;
    }

    public static NbtType byId(final int id) {
        for (NbtType type : NbtType.values()) {
            if (type.getId() == id) return type;
        }
        return null;
    }

    public static NbtType byClass(final Class<? extends INbtTag> tagClass) {
        for (NbtType type : NbtType.values()) {
            if (Objects.equals(type.getTagClass(), tagClass)) return type;
        }
        return null;
    }

    public static NbtType byDataType(final Class<?> dataType) {
        for (NbtType type : NbtType.values()) {
            if (Objects.equals(type.getDataType(), dataType)) return type;
        }
        return null;
    }


    private final int id;
    private final Class<? extends INbtTag> tagClass;
    private final Class<?> dataType;

    NbtType(final int id, final Class<? extends INbtTag> tagClass, final Class<?> dataType) {
        this.id = id;
        this.tagClass = tagClass;
        this.dataType = dataType;
    }

    public int getId() {
        return this.id;
    }

    public Class<? extends INbtTag> getTagClass() {
        return this.tagClass;
    }

    public INbtTag newInstance() {
        if (this.tagClass == null) throw new IllegalStateException("Unable to allocate instance of END tag");
        try {
            return UnsafeUtils.allocateInstance(this.tagClass);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to allocate instance of " + this.tagClass.getSimpleName(), e);
        }
    }

    public Class<?> getDataType() {
        return this.dataType;
    }

    public boolean isNumber() {
        if (this.dataType == null) return false;
        return byte.class.equals(this.dataType) || short.class.equals(this.dataType) || int.class.equals(this.dataType) || long.class.equals(this.dataType) || float.class.equals(this.dataType) || double.class.equals(this.dataType);
    }

}
