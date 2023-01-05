package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound;

public enum Sound {

    NOTE_SNARE("note.snare"),
    NOTE_HAT("note.hat"),
    NOTE_CLICK("note.bd"),
    NOTE_HARP("note.harp"),
    NOTE_BASS_ATTACK("note.bassattack"),
    PISTON_OUT("tile.piston.out"),
    PISTON_IN("tile.piston.in"),
    CHEST_OPEN("random.chestopen"),
    CHEST_CLOSE("random.chestclosed"),
    RANDOM_FUSE("random.fuse"),
    RANDOM_EXPLODE("random.explode"),
    RANDOM_BOW("random.bow"),
    MOB_ZOMBIE("mob.zombie"),
    MOB_HUMAN_HURT("damage.hurtflesh"),
    MOB_ZOMBIE_HURT("mob.zombiehurt"),
    MOB_ZOMBIE_DEATH("mob.zombiedeath"),
    MOB_PIG_ZOMBIE("mob.zombiepig.zpig"),
    MOB_PIG_ZOMBIE_HURT("mob.zombiepig.zpighurt"),
    MOB_PIG_ZOMBIE_DEATH("mob.zombiepig.zpigdeath"),
    MOB_SPIDER("mob.spider"),
    MOB_SPIDER_DEATH("mob.spiderdeath"),
    MOB_CREEPER("mob.creeper"),
    MOB_CREEPER_DEATH("mob.creeperdeath"),
    MOB_SKELETON("mob.skeleton"),
    MOB_SKELETON_HURT("mob.skeletonhurt"),
    MOB_SKELETON_DEATH("mob.skeletondeath"),
    MOB_ENDERMEN("mob.endermen.idle"),
    MOB_ENDERMEN_HURT("mob.endermen.hit"),
    MOB_ENDERMEN_DEATH("mob.endermen.death"),
    MOB_BLAZE("mob.blaze.breathe"),
    MOB_BLAZE_HURT("mob.blaze.hit"),
    MOB_BLAZE_DEATH("mob.blaze.death"),
    MOB_GHAST("mob.ghast.moan"),
    MOB_GHAST_HURT("mob.ghast.scream"),
    MOB_GHAST_DEATH("mob.ghast.death"),
    MOB_SILVERFISH("mob.silverfish.say"),
    MOB_SILVERFISH_HURT("mob.silverfish.hit"),
    MOB_SILVERFISH_DEATH("mob.silverfish.kill"),
    MOB_CAT("mob.cat.meow"),
    MOB_CAT_HURT("mob.cat.hitt"),
    MOB_CAT_PURREOW("mob.cat.purreow"),
    MOB_CAT_MEOW("mob.cat.meow"),
    MOB_IRON_GOLEM_HURT("mob.irongolem.hit"),
    MOB_IRON_GOLEM_DEATH("mob.irongolem.death"),
    MOB_WOLF("mob.wolf.bark"),
    MOB_WOLF_HURT("mob.wolf.hurt"),
    MOB_WOLF_DEATH("mob.wolf.death"),
    MOB_WOLF_GROWL("mob.wolf.growl"),
    MOB_WOLF_WHINE("mob.wolf.whine"),
    MOB_WOLF_PANTING("mob.wolf.panting"),
    MOB_SHEEP("mob.sheep"),
    MOB_PIG("mob.pig"),
    MOB_PIG_DEATH("mob.pigdeath"),
    MOB_COW("mob.cow"),
    MOB_COW_HURT("mob.cowhurt"),
    MOB_CHICKEN("mob.chicken"),
    MOB_CHICKEN_HURT("mob.chickenhurt"),
    MOB_SLIME("mob.slime"),
    MOB_MAGMACUBE_SMALL("mob.magmacube.small"),
    MOB_VILLAGER("mob.villager.default"),
    MOB_VILLAGER_HURT("mob.villager.defaulthurt"),
    MOB_VILLAGER_DEATH("mob.villager.defaultdeath"),
    NO_SOUND("");

    private final String soundName;

    Sound(final String soundName) {
        this.soundName = soundName;
    }

    public String getSoundName() {
        return this.soundName;
    }

}
