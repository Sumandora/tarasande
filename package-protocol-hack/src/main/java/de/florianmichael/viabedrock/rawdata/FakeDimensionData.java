package de.florianmichael.viabedrock.rawdata;

import com.viaversion.viaversion.libs.opennbt.NBTIO;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPInputStream;

public class FakeDimensionData {
    private static CompoundTag dimensionRegistry;

    public static void load() {
        try {
            dimensionRegistry = NBTIO.readTag(new GZIPInputStream(Objects.requireNonNull(FakeDimensionData.class.getResourceAsStream("/assets/viabedrock/dimension-registry.nbt"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompoundTag getDimensionRegistry() {
        return dimensionRegistry;
    }
}
