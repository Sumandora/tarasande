package de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class EntityIDs {

    public static final BiMap<Integer, String> ENTITY_IDS;

    static {
        HashBiMap<String, Integer> entityMap = HashBiMap.create();
        entityMap.put("Item", 1);
        entityMap.put("XPOrb", 2);
        entityMap.put("Painting", 9);
        entityMap.put("Arrow", 10);
        entityMap.put("Snowball", 11);
        entityMap.put("Fireball", 12);
        entityMap.put("SmallFireball", 13);
        entityMap.put("ThrownEnderpearl", 14);
        entityMap.put("EyeOfEnderSignal", 15);
        entityMap.put("ThrownPotion", 16);
        entityMap.put("ThrownExpBottle", 17);
        entityMap.put("PrimedTnt", 20);
        entityMap.put("FallingSand", 21);
        entityMap.put("Minecart", 40);
        entityMap.put("Boat", 41);
        entityMap.put("Mob", 48);
        entityMap.put("Monster", 49);
        entityMap.put("Creeper", 50);
        entityMap.put("Skeleton", 51);
        entityMap.put("Spider", 52);
        entityMap.put("Giant", 53);
        entityMap.put("Zombie", 54);
        entityMap.put("Slime", 55);
        entityMap.put("Ghast", 56);
        entityMap.put("PigZombie", 57);
        entityMap.put("Enderman", 58);
        entityMap.put("CaveSpider", 59);
        entityMap.put("Silverfish", 60);
        entityMap.put("Blaze", 61);
        entityMap.put("LavaSlime", 62);
        entityMap.put("EnderDragon", 63);
        entityMap.put("Pig", 90);
        entityMap.put("Sheep", 91);
        entityMap.put("Cow", 92);
        entityMap.put("Chicken", 93);
        entityMap.put("Squid", 94);
        entityMap.put("Wolf", 95);
        entityMap.put("MushroomCow", 96);
        entityMap.put("SnowMan", 97);
        entityMap.put("Ozelot", 98);
        entityMap.put("VillagerGolem", 99);
        entityMap.put("Villager", 120);
        entityMap.put("EnderCrystal", 200);

        ENTITY_IDS = entityMap.inverse();
    }
}
