package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.entity;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;

import java.util.HashMap;
import java.util.Map;

public class EntityOffsets {

    private final static Map<Integer, Double> entityOffsets = new HashMap<>();

    static {
        entityOffsets.put(Entity1_10Types.ObjectType.ITEM.getId(), 4.0);
        entityOffsets.put(Entity1_10Types.ObjectType.THROWN_EXP_BOTTLE.getId(), 4.0);
        entityOffsets.put(Entity1_10Types.ObjectType.TNT_PRIMED.getId(), 16.0);
        entityOffsets.put(Entity1_10Types.ObjectType.MINECART.getId(), 11.2);
        entityOffsets.put(Entity1_10Types.ObjectType.BOAT.getId(), 4.8);
        entityOffsets.put(Entity1_10Types.ObjectType.AREA_EFFECT_CLOUD.getId(), 1.6);
    }

    public static double getOffset(final int objectType) {
        if (entityOffsets.containsKey(objectType)) {
            return entityOffsets.get(objectType);
        }
        return 0.0;
    }
}
