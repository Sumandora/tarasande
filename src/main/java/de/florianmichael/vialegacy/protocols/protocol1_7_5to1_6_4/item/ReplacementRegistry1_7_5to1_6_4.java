package de.florianmichael.vialegacy.protocols.protocol1_7_5to1_6_4.item;

import com.viaversion.viaversion.api.minecraft.item.Item;
import de.florianmichael.vialegacy.api.replacement.Replacement;
import de.florianmichael.vialegacy.api.replacement.ReplacementRegistry;

public class ReplacementRegistry1_7_5to1_6_4 {

    private static final ReplacementRegistry registry = new ReplacementRegistry();

    static {

        registry.registerBlock(26, 0, new Replacement(355, 0, "Bed block"));
        registry.registerBlock(34, 0, new Replacement(33, 0, "Piston extension"));
        registry.registerBlock(36, 0, new Replacement(33, 0, "Piston moving piece"));
        registry.registerBlock(55, 0, new Replacement(331, 0, "Redstone wire"));
        registry.registerBlock(59, 0, new Replacement(295, 0, "Wheat crops"));
        registry.registerBlock(63, 0, new Replacement(323, 0, "Standing sign"));
        registry.registerBlock(64, 0, new Replacement(324, 0, "Oak door block"));
        registry.registerBlock(68, 0, new Replacement(323, 0, "Wall sign"));
        registry.registerBlock(71, 0, new Replacement(330, 0, "Iron door block"));
        registry.registerBlock(74, 0, new Replacement(73, 0, "Glowing redstone ore"));
        registry.registerBlock(75, 0, new Replacement(76, 0, "Restone torch off"));
        registry.registerBlock(83, 0, new Replacement(338, 0, "Sugar cane block"));
        registry.registerBlock(92, 0, new Replacement(354, 0, "Cake block"));
        registry.registerBlock(93, 0, new Replacement(356, 0, "Diode block off"));
        registry.registerBlock(94, 0, new Replacement(356, 0, "Diode block on"));
        registry.registerBlock(95, 0, new Replacement(146, 0, "Locked Chest"));
        registry.registerBlock(104, 0, new Replacement(361, 0, "Pumpkin stem"));
        registry.registerBlock(105, 0, new Replacement(362, 0, "Melon stem"));
        registry.registerBlock(115, 0, new Replacement(372, 0, "Nether warts"));
        registry.registerBlock(117, 0, new Replacement(379, 0, "Brewing stand"));
        registry.registerBlock(118, 0, new Replacement(380, 0, "Cauldron"));
        registry.registerBlock(124, 0, new Replacement(123, 0, "Restone lamp on"));
        registry.registerBlock(132, 0, new Replacement(287, 0, "Tripwire"));
        registry.registerBlock(140, 0, new Replacement(390, 0, "Flower pot"));
        registry.registerBlock(144, 0, new Replacement(397, 0, "Skull"));
        registry.registerBlock(149, 0, new Replacement(404, 0, "Redstone comparator off"));
        registry.registerBlock(150, 0, new Replacement(404, 0, "Redstone comparator on"));
    }

    public static Item replace(Item item) {
        if (item == null) {
            return null;
        }
        return registry.replace(item);
    }
}
