package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.metadata;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.util.Pair;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl.MetaType1_6_4;

import java.util.HashMap;
import java.util.Optional;

public enum MetaIndex1_6_1to1_5_2 {

    ENTITY_LIVING_POTION_EFFECT_COLOR(Entity1_10Types.EntityType.ENTITY_LIVING, 8, MetaType1_6_4.Int, 7, MetaType1_6_4.Int),
    ENTITY_LIVING_IS_POTION_EFFECT_AMBIENT(Entity1_10Types.EntityType.ENTITY_LIVING, 9, MetaType1_6_4.Byte, 8, MetaType1_6_4.Byte),
    ENTITY_LIVING_ARROWS(Entity1_10Types.EntityType.ENTITY_LIVING, 10, MetaType1_6_4.Byte, 9, MetaType1_6_4.Byte),
    ENTITY_LIVING_NAME_TAG(Entity1_10Types.EntityType.ENTITY_LIVING, 5, MetaType1_6_4.String, 10, MetaType1_6_4.String),
    ENTITY_LIVING_NAME_TAG_VISIBILITY(Entity1_10Types.EntityType.ENTITY_LIVING, 6, MetaType1_6_4.Byte, 11, MetaType1_6_4.Byte),
    HUMAN_ABSORPTION_HEARTS(Entity1_10Types.EntityType.ENTITY_HUMAN, 17, MetaType1_6_4.Byte, MetaType1_6_4.Float),
    BOAT_DAMAGE_TAKEN(Entity1_10Types.EntityType.BOAT, 19, MetaType1_6_4.Int, MetaType1_6_4.Float),
    MINECART_DAMAGE_TAKEN(Entity1_10Types.EntityType.MINECART_ABSTRACT, 19, MetaType1_6_4.Int, MetaType1_6_4.Float),
    WITHER_HEALTH(Entity1_10Types.EntityType.WITHER, 16, MetaType1_6_4.Int, 6, MetaType1_6_4.Float),
    ENDER_DRAGON_HEALTH(Entity1_10Types.EntityType.ENDER_DRAGON, 16, MetaType1_6_4.Int, 6, MetaType1_6_4.Float),
    WOLF_HEALTH(Entity1_10Types.EntityType.WOLF, 18, MetaType1_6_4.Int, MetaType1_6_4.Float),
    ;

    private static final HashMap<Pair<Entity1_10Types.EntityType, Integer>, MetaIndex1_6_1to1_5_2> metadataRewrites = new HashMap<>();

    static {
        for (MetaIndex1_6_1to1_5_2 index : MetaIndex1_6_1to1_5_2.values()) {
            metadataRewrites.put(new Pair<>(index.getEntityType(), index.getOldIndex()), index);
        }
    }

    private final Entity1_10Types.EntityType entityType;
    private final int oldIndex;
    private final int newIndex;
    private final MetaType1_6_4 oldType;
    private final MetaType1_6_4 newType;

    MetaIndex1_6_1to1_5_2(Entity1_10Types.EntityType entityType, int oldIndex, MetaType1_6_4 oldType, MetaType1_6_4 newType) {
        this.entityType = entityType;
        this.oldIndex = oldIndex;
        this.newIndex = oldIndex;
        this.oldType = oldType;
        this.newType = newType;
    }

    MetaIndex1_6_1to1_5_2(Entity1_10Types.EntityType entityType, int oldIndex, MetaType1_6_4 oldType, int newIndex, MetaType1_6_4 newType) {
        this.entityType = entityType;
        this.oldIndex = oldIndex;
        this.oldType = oldType;
        this.newIndex = newIndex;
        this.newType = newType;
    }

    public Entity1_10Types.EntityType getEntityType() {
        return this.entityType;
    }

    public int getOldIndex() {
        return this.oldIndex;
    }

    public int getNewIndex() {
        return this.newIndex;
    }

    public MetaType1_6_4 getOldType() {
        return this.oldType;
    }

    public MetaType1_6_4 getNewType() {
        return this.newType;
    }

    private static Optional<MetaIndex1_6_1to1_5_2> getIndex(Entity1_10Types.EntityType type, int index) {
        final Pair<Entity1_10Types.EntityType, Integer> pair = new Pair<>(type, index);
        return Optional.ofNullable(metadataRewrites.get(pair));
    }

    public static MetaIndex1_6_1to1_5_2 searchIndex(Entity1_10Types.EntityType type, int index) {
        Entity1_10Types.EntityType currentType = type;
        do {
            Optional<MetaIndex1_6_1to1_5_2> optMeta = getIndex(currentType, index);
            if (optMeta.isPresent()) {
                return optMeta.get();
            }

            currentType = currentType.getParent();
        } while (currentType != null);

        return null;
    }
}
