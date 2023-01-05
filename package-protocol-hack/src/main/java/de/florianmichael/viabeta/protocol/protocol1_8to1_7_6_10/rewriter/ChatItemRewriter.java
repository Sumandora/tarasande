package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.nbt.BinaryTagIO;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.rewriter.legacy.ViaStringTagReader1_11_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;

import java.io.IOException;

public class ChatItemRewriter extends ComponentRewriter {

    private static final Int2ObjectOpenHashMap<String> ID_TO_NAME = new Int2ObjectOpenHashMap<>(315, 0.99F);

    static {
        ID_TO_NAME.put(1, "stone");
        ID_TO_NAME.put(2, "grass");
        ID_TO_NAME.put(3, "dirt");
        ID_TO_NAME.put(4, "cobblestone");
        ID_TO_NAME.put(5, "planks");
        ID_TO_NAME.put(6, "sapling");
        ID_TO_NAME.put(7, "bedrock");
        ID_TO_NAME.put(8, "flowing_water");
        ID_TO_NAME.put(9, "water");
        ID_TO_NAME.put(10, "flowing_lava");
        ID_TO_NAME.put(11, "lava");
        ID_TO_NAME.put(12, "sand");
        ID_TO_NAME.put(13, "gravel");
        ID_TO_NAME.put(14, "gold_ore");
        ID_TO_NAME.put(15, "iron_ore");
        ID_TO_NAME.put(16, "coal_ore");
        ID_TO_NAME.put(17, "log");
        ID_TO_NAME.put(18, "leaves");
        ID_TO_NAME.put(19, "sponge");
        ID_TO_NAME.put(20, "glass");
        ID_TO_NAME.put(21, "lapis_ore");
        ID_TO_NAME.put(22, "lapis_block");
        ID_TO_NAME.put(23, "dispenser");
        ID_TO_NAME.put(24, "sandstone");
        ID_TO_NAME.put(25, "noteblock");
        ID_TO_NAME.put(27, "golden_rail");
        ID_TO_NAME.put(28, "detector_rail");
        ID_TO_NAME.put(29, "sticky_piston");
        ID_TO_NAME.put(30, "web");
        ID_TO_NAME.put(31, "tallgrass");
        ID_TO_NAME.put(32, "deadbush");
        ID_TO_NAME.put(33, "piston");
        ID_TO_NAME.put(35, "wool");
        ID_TO_NAME.put(37, "yellow_flower");
        ID_TO_NAME.put(38, "red_flower");
        ID_TO_NAME.put(39, "brown_mushroom");
        ID_TO_NAME.put(40, "red_mushroom");
        ID_TO_NAME.put(41, "gold_block");
        ID_TO_NAME.put(42, "iron_block");
        ID_TO_NAME.put(43, "double_stone_slab");
        ID_TO_NAME.put(44, "stone_slab");
        ID_TO_NAME.put(45, "brick_block");
        ID_TO_NAME.put(46, "tnt");
        ID_TO_NAME.put(47, "bookshelf");
        ID_TO_NAME.put(48, "mossy_cobblestone");
        ID_TO_NAME.put(49, "obsidian");
        ID_TO_NAME.put(50, "torch");
        ID_TO_NAME.put(51, "fire");
        ID_TO_NAME.put(52, "mob_spawner");
        ID_TO_NAME.put(53, "oak_stairs");
        ID_TO_NAME.put(54, "chest");
        ID_TO_NAME.put(56, "diamond_ore");
        ID_TO_NAME.put(57, "diamond_block");
        ID_TO_NAME.put(58, "crafting_table");
        ID_TO_NAME.put(60, "farmland");
        ID_TO_NAME.put(61, "furnace");
        ID_TO_NAME.put(62, "lit_furnace");
        ID_TO_NAME.put(65, "ladder");
        ID_TO_NAME.put(66, "rail");
        ID_TO_NAME.put(67, "stone_stairs");
        ID_TO_NAME.put(69, "lever");
        ID_TO_NAME.put(70, "stone_pressure_plate");
        ID_TO_NAME.put(72, "wooden_pressure_plate");
        ID_TO_NAME.put(73, "redstone_ore");
        ID_TO_NAME.put(76, "redstone_torch");
        ID_TO_NAME.put(77, "stone_button");
        ID_TO_NAME.put(78, "snow_layer");
        ID_TO_NAME.put(79, "ice");
        ID_TO_NAME.put(80, "snow");
        ID_TO_NAME.put(81, "cactus");
        ID_TO_NAME.put(82, "clay");
        ID_TO_NAME.put(84, "jukebox");
        ID_TO_NAME.put(85, "fence");
        ID_TO_NAME.put(86, "pumpkin");
        ID_TO_NAME.put(87, "netherrack");
        ID_TO_NAME.put(88, "soul_sand");
        ID_TO_NAME.put(89, "glowstone");
        ID_TO_NAME.put(90, "portal");
        ID_TO_NAME.put(91, "lit_pumpkin");
        ID_TO_NAME.put(95, "stained_glass");
        ID_TO_NAME.put(96, "trapdoor");
        ID_TO_NAME.put(97, "monster_egg");
        ID_TO_NAME.put(98, "stonebrick");
        ID_TO_NAME.put(99, "brown_mushroom_block");
        ID_TO_NAME.put(100, "red_mushroom_block");
        ID_TO_NAME.put(101, "iron_bars");
        ID_TO_NAME.put(102, "glass_pane");
        ID_TO_NAME.put(103, "melon_block");
        ID_TO_NAME.put(106, "vine");
        ID_TO_NAME.put(107, "fence_gate");
        ID_TO_NAME.put(108, "brick_stairs");
        ID_TO_NAME.put(109, "stone_brick_stairs");
        ID_TO_NAME.put(110, "mycelium");
        ID_TO_NAME.put(111, "waterlily");
        ID_TO_NAME.put(112, "nether_brick");
        ID_TO_NAME.put(113, "nether_brick_fence");
        ID_TO_NAME.put(114, "nether_brick_stairs");
        ID_TO_NAME.put(116, "enchanting_table");
        ID_TO_NAME.put(119, "end_portal");
        ID_TO_NAME.put(120, "end_portal_frame");
        ID_TO_NAME.put(121, "end_stone");
        ID_TO_NAME.put(122, "dragon_egg");
        ID_TO_NAME.put(123, "redstone_lamp");
        ID_TO_NAME.put(125, "double_wooden_slab");
        ID_TO_NAME.put(126, "wooden_slab");
        ID_TO_NAME.put(127, "cocoa");
        ID_TO_NAME.put(128, "sandstone_stairs");
        ID_TO_NAME.put(129, "emerald_ore");
        ID_TO_NAME.put(130, "ender_chest");
        ID_TO_NAME.put(131, "tripwire_hook");
        ID_TO_NAME.put(133, "emerald_block");
        ID_TO_NAME.put(134, "spruce_stairs");
        ID_TO_NAME.put(135, "birch_stairs");
        ID_TO_NAME.put(136, "jungle_stairs");
        ID_TO_NAME.put(137, "command_block");
        ID_TO_NAME.put(138, "beacon");
        ID_TO_NAME.put(139, "cobblestone_wall");
        ID_TO_NAME.put(141, "carrots");
        ID_TO_NAME.put(142, "potatoes");
        ID_TO_NAME.put(143, "wooden_button");
        ID_TO_NAME.put(145, "anvil");
        ID_TO_NAME.put(146, "trapped_chest");
        ID_TO_NAME.put(147, "light_weighted_pressure_plate");
        ID_TO_NAME.put(148, "heavy_weighted_pressure_plate");
        ID_TO_NAME.put(151, "daylight_detector");
        ID_TO_NAME.put(152, "redstone_block");
        ID_TO_NAME.put(153, "quartz_ore");
        ID_TO_NAME.put(154, "hopper");
        ID_TO_NAME.put(155, "quartz_block");
        ID_TO_NAME.put(156, "quartz_stairs");
        ID_TO_NAME.put(157, "activator_rail");
        ID_TO_NAME.put(158, "dropper");
        ID_TO_NAME.put(159, "stained_hardened_clay");
        ID_TO_NAME.put(160, "stained_glass_pane");
        ID_TO_NAME.put(161, "leaves2");
        ID_TO_NAME.put(162, "log2");
        ID_TO_NAME.put(163, "acacia_stairs");
        ID_TO_NAME.put(164, "dark_oak_stairs");
        ID_TO_NAME.put(170, "hay_block");
        ID_TO_NAME.put(171, "carpet");
        ID_TO_NAME.put(172, "hardened_clay");
        ID_TO_NAME.put(173, "coal_block");
        ID_TO_NAME.put(174, "packed_ice");
        ID_TO_NAME.put(175, "double_plant");
        ID_TO_NAME.put(256, "iron_shovel");
        ID_TO_NAME.put(257, "iron_pickaxe");
        ID_TO_NAME.put(258, "iron_axe");
        ID_TO_NAME.put(259, "flint_and_steel");
        ID_TO_NAME.put(260, "apple");
        ID_TO_NAME.put(261, "bow");
        ID_TO_NAME.put(262, "arrow");
        ID_TO_NAME.put(263, "coal");
        ID_TO_NAME.put(264, "diamond");
        ID_TO_NAME.put(265, "iron_ingot");
        ID_TO_NAME.put(266, "gold_ingot");
        ID_TO_NAME.put(267, "iron_sword");
        ID_TO_NAME.put(268, "wooden_sword");
        ID_TO_NAME.put(269, "wooden_shovel");
        ID_TO_NAME.put(270, "wooden_pickaxe");
        ID_TO_NAME.put(271, "wooden_axe");
        ID_TO_NAME.put(272, "stone_sword");
        ID_TO_NAME.put(273, "stone_shovel");
        ID_TO_NAME.put(274, "stone_pickaxe");
        ID_TO_NAME.put(275, "stone_axe");
        ID_TO_NAME.put(276, "diamond_sword");
        ID_TO_NAME.put(277, "diamond_shovel");
        ID_TO_NAME.put(278, "diamond_pickaxe");
        ID_TO_NAME.put(279, "diamond_axe");
        ID_TO_NAME.put(280, "stick");
        ID_TO_NAME.put(281, "bowl");
        ID_TO_NAME.put(282, "mushroom_stew");
        ID_TO_NAME.put(283, "golden_sword");
        ID_TO_NAME.put(284, "golden_shovel");
        ID_TO_NAME.put(285, "golden_pickaxe");
        ID_TO_NAME.put(286, "golden_axe");
        ID_TO_NAME.put(287, "string");
        ID_TO_NAME.put(288, "feather");
        ID_TO_NAME.put(289, "gunpowder");
        ID_TO_NAME.put(290, "wooden_hoe");
        ID_TO_NAME.put(291, "stone_hoe");
        ID_TO_NAME.put(292, "iron_hoe");
        ID_TO_NAME.put(293, "diamond_hoe");
        ID_TO_NAME.put(294, "golden_hoe");
        ID_TO_NAME.put(295, "wheat_seeds");
        ID_TO_NAME.put(296, "wheat");
        ID_TO_NAME.put(297, "bread");
        ID_TO_NAME.put(298, "leather_helmet");
        ID_TO_NAME.put(299, "leather_chestplate");
        ID_TO_NAME.put(300, "leather_leggings");
        ID_TO_NAME.put(301, "leather_boots");
        ID_TO_NAME.put(302, "chainmail_helmet");
        ID_TO_NAME.put(303, "chainmail_chestplate");
        ID_TO_NAME.put(304, "chainmail_leggings");
        ID_TO_NAME.put(305, "chainmail_boots");
        ID_TO_NAME.put(306, "iron_helmet");
        ID_TO_NAME.put(307, "iron_chestplate");
        ID_TO_NAME.put(308, "iron_leggings");
        ID_TO_NAME.put(309, "iron_boots");
        ID_TO_NAME.put(310, "diamond_helmet");
        ID_TO_NAME.put(311, "diamond_chestplate");
        ID_TO_NAME.put(312, "diamond_leggings");
        ID_TO_NAME.put(313, "diamond_boots");
        ID_TO_NAME.put(314, "golden_helmet");
        ID_TO_NAME.put(315, "golden_chestplate");
        ID_TO_NAME.put(316, "golden_leggings");
        ID_TO_NAME.put(317, "golden_boots");
        ID_TO_NAME.put(318, "flint");
        ID_TO_NAME.put(319, "porkchop");
        ID_TO_NAME.put(320, "cooked_porkchop");
        ID_TO_NAME.put(321, "painting");
        ID_TO_NAME.put(322, "golden_apple");
        ID_TO_NAME.put(323, "sign");
        ID_TO_NAME.put(324, "wooden_door");
        ID_TO_NAME.put(325, "bucket");
        ID_TO_NAME.put(326, "water_bucket");
        ID_TO_NAME.put(327, "lava_bucket");
        ID_TO_NAME.put(328, "minecart");
        ID_TO_NAME.put(329, "saddle");
        ID_TO_NAME.put(330, "iron_door");
        ID_TO_NAME.put(331, "redstone");
        ID_TO_NAME.put(332, "snowball");
        ID_TO_NAME.put(333, "boat");
        ID_TO_NAME.put(334, "leather");
        ID_TO_NAME.put(335, "milk_bucket");
        ID_TO_NAME.put(336, "brick");
        ID_TO_NAME.put(337, "clay_ball");
        ID_TO_NAME.put(338, "reeds");
        ID_TO_NAME.put(339, "paper");
        ID_TO_NAME.put(340, "book");
        ID_TO_NAME.put(341, "slime_ball");
        ID_TO_NAME.put(342, "chest_minecart");
        ID_TO_NAME.put(343, "furnace_minecart");
        ID_TO_NAME.put(344, "egg");
        ID_TO_NAME.put(345, "compass");
        ID_TO_NAME.put(346, "fishing_rod");
        ID_TO_NAME.put(347, "clock");
        ID_TO_NAME.put(348, "glowstone_dust");
        ID_TO_NAME.put(349, "fish");
        ID_TO_NAME.put(350, "cooked_fished");
        ID_TO_NAME.put(351, "dye");
        ID_TO_NAME.put(352, "bone");
        ID_TO_NAME.put(353, "sugar");
        ID_TO_NAME.put(354, "cake");
        ID_TO_NAME.put(355, "bed");
        ID_TO_NAME.put(356, "repeater");
        ID_TO_NAME.put(357, "cookie");
        ID_TO_NAME.put(358, "filled_map");
        ID_TO_NAME.put(359, "shears");
        ID_TO_NAME.put(360, "melon");
        ID_TO_NAME.put(361, "pumpkin_seeds");
        ID_TO_NAME.put(362, "melon_seeds");
        ID_TO_NAME.put(363, "beef");
        ID_TO_NAME.put(364, "cooked_beef");
        ID_TO_NAME.put(365, "chicken");
        ID_TO_NAME.put(366, "cooked_chicken");
        ID_TO_NAME.put(367, "rotten_flesh");
        ID_TO_NAME.put(368, "ender_pearl");
        ID_TO_NAME.put(369, "blaze_rod");
        ID_TO_NAME.put(370, "ghast_tear");
        ID_TO_NAME.put(371, "gold_nugget");
        ID_TO_NAME.put(372, "nether_wart");
        ID_TO_NAME.put(373, "potion");
        ID_TO_NAME.put(374, "glass_bottle");
        ID_TO_NAME.put(375, "spider_eye");
        ID_TO_NAME.put(376, "fermented_spider_eye");
        ID_TO_NAME.put(377, "blaze_powder");
        ID_TO_NAME.put(378, "magma_cream");
        ID_TO_NAME.put(379, "brewing_stand");
        ID_TO_NAME.put(380, "cauldron");
        ID_TO_NAME.put(381, "ender_eye");
        ID_TO_NAME.put(382, "speckled_melon");
        ID_TO_NAME.put(383, "spawn_egg");
        ID_TO_NAME.put(384, "experience_bottle");
        ID_TO_NAME.put(385, "fire_charge");
        ID_TO_NAME.put(386, "writable_book");
        ID_TO_NAME.put(387, "written_book");
        ID_TO_NAME.put(388, "emerald");
        ID_TO_NAME.put(389, "item_frame");
        ID_TO_NAME.put(390, "flower_pot");
        ID_TO_NAME.put(391, "carrot");
        ID_TO_NAME.put(392, "potato");
        ID_TO_NAME.put(393, "baked_potato");
        ID_TO_NAME.put(394, "poisonous_potato");
        ID_TO_NAME.put(395, "map");
        ID_TO_NAME.put(396, "golden_carrot");
        ID_TO_NAME.put(397, "skull");
        ID_TO_NAME.put(398, "carrot_on_a_stick");
        ID_TO_NAME.put(399, "nether_star");
        ID_TO_NAME.put(400, "pumpkin_pie");
        ID_TO_NAME.put(401, "fireworks");
        ID_TO_NAME.put(402, "firework_charge");
        ID_TO_NAME.put(403, "enchanted_book");
        ID_TO_NAME.put(404, "comparator");
        ID_TO_NAME.put(405, "netherbrick");
        ID_TO_NAME.put(406, "quartz");
        ID_TO_NAME.put(407, "tnt_minecart");
        ID_TO_NAME.put(408, "hopper_minecart");
        ID_TO_NAME.put(417, "iron_horse_armor");
        ID_TO_NAME.put(418, "golden_horse_armor");
        ID_TO_NAME.put(419, "diamond_horse_armor");
        ID_TO_NAME.put(420, "lead");
        ID_TO_NAME.put(421, "name_tag");
        ID_TO_NAME.put(422, "command_block_minecart");
        ID_TO_NAME.put(2256, "record_13");
        ID_TO_NAME.put(2257, "record_cat");
        ID_TO_NAME.put(2258, "record_blocks");
        ID_TO_NAME.put(2259, "record_chirp");
        ID_TO_NAME.put(2260, "record_far");
        ID_TO_NAME.put(2261, "record_mall");
        ID_TO_NAME.put(2262, "record_mellohi");
        ID_TO_NAME.put(2263, "record_stal");
        ID_TO_NAME.put(2264, "record_strad");
        ID_TO_NAME.put(2265, "record_ward");
        ID_TO_NAME.put(2266, "record_11");
        ID_TO_NAME.put(2267, "record_wait");
    }

    private static final ComponentRewriter REMAP_SHOW_ITEM = new ComponentRewriter() {
        @Override
        protected void handleHoverEvent(JsonObject hoverEvent) {
            super.handleHoverEvent(hoverEvent);
            final String action = hoverEvent.getAsJsonPrimitive("action").getAsString();
            if (!action.equals("show_item")) return;

            final JsonElement value = hoverEvent.get("value");
            if (value == null) return;

            final String text = findItemNBT(value);
            if (text == null) return;

            final CompoundTag tag;
            try {
                tag = ViaStringTagReader1_11_2.getTagFromJson(text);
            } catch (Exception e) {
                ViaBeta.getPlatform().getLogger().warning("Error reading NBT in show_item:" + text);
                throw new RuntimeException(e);
            }

            final CompoundTag itemTag = tag.get("tag");
            final ShortTag idTag = tag.get("id");
            final ShortTag damageTag = tag.get("Damage");

            // Call item converter
            final short damage = damageTag != null ? damageTag.asShort() : 0;
            final short id = idTag != null ? idTag.asShort() : 1;
            final Item item = new DataItem();
            item.setIdentifier(id);
            item.setData(damage);
            item.setTag(itemTag);
            handleItem(item);

            // Serialize again
            if (damage != item.data()) {
                tag.put("Damage", new ShortTag(item.data()));
            }
            tag.put("id", new StringTag("minecraft:" + ID_TO_NAME.getOrDefault(item.identifier(), "stone")));
            if (item.tag() != null) {
                tag.put("tag", new CompoundTag(item.tag().getValue()));
            }

            final JsonArray array = new JsonArray();
            final JsonObject object = new JsonObject();
            array.add(object);
            final String serializedNBT;
            try {
                serializedNBT = BinaryTagIO.writeString(tag);
                object.addProperty("text", serializedNBT);
                hoverEvent.add("value", array);
            } catch (IOException e) {
                ViaBeta.getPlatform().getLogger().warning("Error writing NBT in show_item:" + text);
                e.printStackTrace();
            }
        }

        private void handleItem(Item item) {
            Protocol1_8to1_7_6_10.ITEM_REWRITER.rewriteRead(item);
        }

        private String findItemNBT(JsonElement element) {
            if (element.isJsonArray()) {
                for (JsonElement jsonElement : element.getAsJsonArray()) {
                    String value = findItemNBT(jsonElement);
                    if (value != null) {
                        return value;
                    }
                }
            } else if (element.isJsonObject()) {
                final JsonPrimitive text = element.getAsJsonObject().getAsJsonPrimitive("text");
                if (text != null) {
                    return text.getAsString();
                }
            } else if (element.isJsonPrimitive()) {
                return element.getAsJsonPrimitive().getAsString();
            }
            return null;
        }
    };

    public static String toClient(final String text) {
        return REMAP_SHOW_ITEM.processText(text).toString();
    }

}
