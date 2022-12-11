package net.lenni0451.mcstructs.nbt.snbt;

import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtDeserializeException;
import net.lenni0451.mcstructs.nbt.exceptions.SNbtSerializeException;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_12.SNbtDeserializer_v1_12;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_12.SNbtSerializer_v1_12;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_13.SNbtDeserializer_v1_13;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_14.SNbtDeserializer_v1_14;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_14.SNbtSerializer_v1_14;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_7.SNbtDeserializer_v1_7;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_7.SNbtSerializer_v1_7;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_8.SNbtDeserializer_v1_8;
import net.lenni0451.mcstructs.nbt.snbt.impl.v1_8.SNbtSerializer_v1_8;
import net.lenni0451.mcstructs.nbt.tags.CompoundNbt;

import java.util.function.Supplier;

public class SNbtSerializer<T extends INbtTag> {

    public static final SNbtSerializer<INbtTag> V1_7 = new SNbtSerializer<>(SNbtSerializer_v1_7::new, SNbtDeserializer_v1_7::new);
    public static final SNbtSerializer<CompoundNbt> V1_8 = new SNbtSerializer<>(SNbtSerializer_v1_8::new, SNbtDeserializer_v1_8::new);
    public static final SNbtSerializer<CompoundNbt> V1_12 = new SNbtSerializer<>(SNbtSerializer_v1_12::new, SNbtDeserializer_v1_12::new);
    public static final SNbtSerializer<CompoundNbt> V1_13 = new SNbtSerializer<>(SNbtSerializer_v1_12::new, SNbtDeserializer_v1_13::new);
    public static final SNbtSerializer<CompoundNbt> V1_14 = new SNbtSerializer<>(SNbtSerializer_v1_14::new, SNbtDeserializer_v1_14::new);


    private final Supplier<ISNbtSerializer> serializerSupplier;
    private final Supplier<ISNbtDeserializer<T>> deserializerSupplier;
    private ISNbtSerializer serializer;
    private ISNbtDeserializer<T> deserializer;

    public SNbtSerializer(final Supplier<ISNbtSerializer> serializerSupplier, final Supplier<ISNbtDeserializer<T>> deserializerSupplier) {
        this.serializerSupplier = serializerSupplier;
        this.deserializerSupplier = deserializerSupplier;
    }

    public ISNbtSerializer getSerializer() {
        if (this.serializer == null) this.serializer = this.serializerSupplier.get();
        return this.serializer;
    }

    public ISNbtDeserializer<T> getDeserializer() {
        if (this.deserializer == null) this.deserializer = this.deserializerSupplier.get();
        return this.deserializer;
    }

    public String serialize(final INbtTag tag) throws SNbtSerializeException {
        return this.getSerializer().serialize(tag);
    }

    public String trySerialize(final INbtTag tag) {
        try {
            return this.serialize(tag);
        } catch (SNbtSerializeException t) {
            return null;
        }
    }

    public T deserialize(final String s) throws SNbtDeserializeException {
        return this.getDeserializer().deserialize(s);
    }

    public T tryDeserialize(final String s) {
        try {
            return this.deserialize(s);
        } catch (SNbtDeserializeException t) {
            return null;
        }
    }

}
