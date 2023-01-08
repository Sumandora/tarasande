package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.ConfiguredSound;

public class SoundEmulation {

    private final static Sound[][] ENTITY_SOUNDS = new Sound[256][];
    private final static float[] VOL_ADJUST = new float[256];
    private final static int[] INTERVAL_ADJUST = new int[256];

    static {
        ENTITY_SOUNDS[48/*HUMAN*/] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_HUMAN_HURT,
                Sound.MOB_HUMAN_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.CREEPER.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_CREEPER,
                Sound.MOB_CREEPER_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SKELETON.getId()] = new Sound[]{
                Sound.MOB_SKELETON,
                Sound.MOB_SKELETON_HURT,
                Sound.MOB_SKELETON_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SPIDER.getId()] = new Sound[]{
                Sound.MOB_SPIDER,
                Sound.MOB_SPIDER,
                Sound.MOB_SPIDER_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.GIANT.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_HUMAN_HURT,
                Sound.MOB_HUMAN_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.ZOMBIE.getId()] = new Sound[]{
                Sound.MOB_ZOMBIE,
                Sound.MOB_ZOMBIE_HURT,
                Sound.MOB_ZOMBIE_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SLIME.getId()] = new Sound[]{
                Sound.MOB_SLIME,
                Sound.MOB_SLIME,
                Sound.MOB_SLIME
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.GHAST.getId()] = new Sound[]{
                Sound.MOB_GHAST,
                Sound.MOB_GHAST_HURT,
                Sound.MOB_GHAST_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.PIG_ZOMBIE.getId()] = new Sound[]{
                Sound.MOB_PIG_ZOMBIE,
                Sound.MOB_PIG_ZOMBIE_HURT,
                Sound.MOB_PIG_ZOMBIE_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.ENDERMAN.getId()] = new Sound[]{
                Sound.MOB_ENDERMEN,
                Sound.MOB_ENDERMEN_HURT,
                Sound.MOB_ENDERMEN_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.CAVE_SPIDER.getId()] = new Sound[]{
                Sound.MOB_SPIDER,
                Sound.MOB_SPIDER,
                Sound.MOB_SPIDER_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SILVERFISH.getId()] = new Sound[]{
                Sound.MOB_SILVERFISH,
                Sound.MOB_SILVERFISH_HURT,
                Sound.MOB_SILVERFISH_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.BLAZE.getId()] = new Sound[]{
                Sound.MOB_BLAZE,
                Sound.MOB_BLAZE_HURT,
                Sound.MOB_BLAZE_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.MAGMA_CUBE.getId()] = new Sound[]{
                Sound.MOB_MAGMACUBE_SMALL,
                Sound.MOB_SLIME,
                Sound.MOB_SLIME
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.ENDER_DRAGON.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_HUMAN_HURT,
                Sound.MOB_HUMAN_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.PIG.getId()] = new Sound[]{
                Sound.MOB_PIG,
                Sound.MOB_PIG,
                Sound.MOB_PIG_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SHEEP.getId()] = new Sound[]{
                Sound.MOB_SHEEP,
                Sound.MOB_SHEEP,
                Sound.MOB_SHEEP
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.COW.getId()] = new Sound[]{
                Sound.MOB_COW,
                Sound.MOB_COW_HURT,
                Sound.MOB_COW_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.CHICKEN.getId()] = new Sound[]{
                Sound.MOB_CHICKEN,
                Sound.MOB_CHICKEN_HURT,
                Sound.MOB_CHICKEN_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SQUID.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.NO_SOUND,
                Sound.NO_SOUND
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.WOLF.getId()] = new Sound[]{
                Sound.MOB_WOLF,
                Sound.MOB_WOLF_HURT,
                Sound.MOB_WOLF_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.MUSHROOM_COW.getId()] = new Sound[]{
                Sound.MOB_COW,
                Sound.MOB_COW_HURT,
                Sound.MOB_COW_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.SNOWMAN.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.NO_SOUND,
                Sound.NO_SOUND
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.OCELOT.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_CAT_HURT,
                Sound.MOB_CAT_HURT
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.IRON_GOLEM.getId()] = new Sound[]{
                Sound.NO_SOUND,
                Sound.MOB_IRON_GOLEM_HURT,
                Sound.MOB_IRON_GOLEM_DEATH
        };
        ENTITY_SOUNDS[Entity1_10Types.EntityType.VILLAGER.getId()] = new Sound[]{
                Sound.MOB_VILLAGER,
                Sound.MOB_VILLAGER_HURT,
                Sound.MOB_VILLAGER_DEATH
        };

        VOL_ADJUST[Entity1_10Types.EntityType.SLIME.getId()] = 1.6F;
        VOL_ADJUST[Entity1_10Types.EntityType.MAGMA_CUBE.getId()] = 1.6F;
        VOL_ADJUST[Entity1_10Types.EntityType.GHAST.getId()] = 10.0F;
        VOL_ADJUST[Entity1_10Types.EntityType.COW.getId()] = 0.4F;
        VOL_ADJUST[Entity1_10Types.EntityType.WOLF.getId()] = 0.4F;
        VOL_ADJUST[Entity1_10Types.EntityType.SQUID.getId()] = 0.4F;
        VOL_ADJUST[Entity1_10Types.EntityType.MUSHROOM_COW.getId()] = 0.4F;
        VOL_ADJUST[Entity1_10Types.EntityType.OCELOT.getId()] = 0.4F;

        INTERVAL_ADJUST[Entity1_10Types.EntityType.PIG.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.SHEEP.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.WOLF.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.SNOWMAN.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.IRON_GOLEM.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.MUSHROOM_COW.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.COW.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.CHICKEN.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.SQUID.getId()] = 120;
        INTERVAL_ADJUST[Entity1_10Types.EntityType.OCELOT.getId()] = 120;
    }

    public static ConfiguredSound getEntitySound(Entity1_10Types.EntityType entityType, SoundType soundType) {
        ConfiguredSound sound = new ConfiguredSound(Sound.NO_SOUND, 0.0F, 1.0F);
        int entityTypeID = entityType.getId();
        if (entityType.isOrHasParent(Entity1_10Types.EntityType.PLAYER)) entityTypeID = 48;

        final Sound[] entitySounds = ENTITY_SOUNDS[entityTypeID];
        if (entitySounds == null) {
            return sound;
        }

        sound = switch (soundType) {
            case IDLE -> new ConfiguredSound(entitySounds[0], 1.0F, 1.0F);
            case HURT -> new ConfiguredSound(entitySounds[1], 1.0F, 1.0F);
            case DEATH -> new ConfiguredSound(entitySounds[2], 1.0F, 1.0F);
        };

        final float correctedVolume = VOL_ADJUST[entityTypeID];
        if (correctedVolume != 0F) {
            sound.setVolume(correctedVolume);
        }

        return sound;
    }

    public static int getSoundDelayTime(Entity1_10Types.EntityType entityType) {
        int entityTypeID = entityType.getId();
        if (entityType.isOrHasParent(Entity1_10Types.EntityType.PLAYER)) entityTypeID = 48;

        int soundTime = -80;
        final int ajustedSoundTime = SoundEmulation.INTERVAL_ADJUST[entityTypeID];
        if (ajustedSoundTime != 0) soundTime = -ajustedSoundTime;

        return soundTime;
    }

}
