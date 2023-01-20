package de.florianmichael.clampclient.injection.instrumentation_1_8;

import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorDefinition_1_8 {
    public static final List<Item> ARMOR_ITEMS_IN_1_8 = Arrays.asList(
            Items.LEATHER_HELMET,
            Items.LEATHER_CHESTPLATE,
            Items.LEATHER_BOOTS,
            Items.CHAINMAIL_HELMET,
            Items.CHAINMAIL_CHESTPLATE,
            Items.CHAINMAIL_LEGGINGS,
            Items.CHAINMAIL_BOOTS,
            Items.IRON_HELMET,
            Items.IRON_CHESTPLATE,
            Items.IRON_LEGGINGS,
            Items.IRON_BOOTS,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,
            Items.GOLDEN_HELMET,
            Items.GOLDEN_CHESTPLATE,
            Items.GOLDEN_LEGGINGS,
            Items.GOLDEN_BOOTS
    );

    private final static Map<Item, Integer> armorTracker = new HashMap<>();

    static {
        for (Item armorItem : ARMOR_ITEMS_IN_1_8) {
            armorTracker.put(armorItem, ArmorType.findByType(Registries.ITEM.getId(armorItem).toString()).getArmorPoints());
        }
    }

    public static int sum() {
        return MinecraftClient.getInstance().player.getInventory().armor.stream().mapToInt(itemStack -> armorTracker.get(itemStack.getItem())).sum();
    }
}
