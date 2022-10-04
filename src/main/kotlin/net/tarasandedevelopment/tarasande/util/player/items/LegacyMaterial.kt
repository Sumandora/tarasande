package net.tarasandedevelopment.tarasande.util.player.items

import net.minecraft.item.Item
import net.minecraft.item.Items

enum class LegacyMaterial(val location: String, val id: Int, val damage: Float, val item: Item) {

    WOODEN_SWORD("wooden_sword", 0, 4.0F, Items.WOODEN_SWORD),
    STONE_SWORD("stone_sword", 1, 5.0F, Items.STONE_SWORD),
    IRON_SWORD("iron_sword", 2, 6.0F, Items.IRON_SWORD),
    GOLD_SWORD("gold_sword", 3, 4.0F, Items.GOLDEN_SWORD),
    DIAMOND_SWORD("diamond_sword", 4, 7.0F, Items.DIAMOND_SWORD),

    WOODEN_SHOVEL("wooden_shovel", 5, 1.0F, Items.WOODEN_SHOVEL),
    STONE_SHOVEL("stone_shovel", 6, 2.0F, Items.STONE_SHOVEL),
    IRON_SHOVEL("iron_shovel", 7, 3.0F, Items.IRON_SHOVEL),
    GOLDEN_SHOVEL("golden_shovel", 8, 1.0F, Items.GOLDEN_SHOVEL),
    DIAMOND_SHOVEL("diamond_shovel", 9, 4.0F, Items.DIAMOND_SHOVEL),

    WOODEN_PICKAXE("wooden_pickaxe", 10, 2.0F, Items.WOODEN_PICKAXE),
    STONE_PICKAXE("stone_pickaxe", 11, 3.0F, Items.STONE_PICKAXE),
    IRON_PICKAXE("iron_pickaxe", 12, 4.0F, Items.IRON_PICKAXE),
    GOLDEN_PICKAXE("golden_pickaxe", 13, 2.0F, Items.GOLDEN_PICKAXE),
    DIAMOND_PICKAXE("diamond_pickaxe", 14, 5.0F, Items.DIAMOND_PICKAXE),

    WOODEN_AXE("wooden_axe", 15, 3.0F, Items.WOODEN_AXE),
    STONE_AXE("stone_axe", 16, 4.0F, Items.STONE_AXE),
    IRON_AXE("iron_axe", 17, 5.0F, Items.IRON_AXE),
    GOLDEN_AXE("golden_axe", 18, 3.0F, Items.GOLDEN_AXE),
    DIAMOND_AXE("diamond_axe", 19, 6.0F, Items.DIAMOND_AXE),

    WOODEN_HOE("wooden_hoe", 20, 1.0F, Items.WOODEN_HOE),
    STONE_HOE("stone_hoe", 21, 1.0F, Items.STONE_HOE),
    IRON_HOE("iron_hoe", 22, 1.0F, Items.IRON_HOE),
    GOLDEN_HOE("golden_hoe", 23, 1.0F, Items.GOLDEN_HOE),
    DIAMOND_HOE("diamond_hoe", 24, 1.0F, Items.DIAMOND_HOE);
}
