package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.data;

import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FakeDimensionData {
    private static final CompoundTag dimensionRegistry;

    static {
        try {
            dimensionRegistry = NBTIO.readTag(new GZIPInputStream(Objects.requireNonNull(FakeDimensionData.class.getResourceAsStream("/assets/viacursed/dimension-registry.nbt"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompoundTag getDimensionRegistry() {
        return dimensionRegistry;
    }
}
