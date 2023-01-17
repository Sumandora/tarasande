package de.florianmichael.clampclient.injection.instrumentation_c_0_30;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of tarasande and a copy of the ItemSplitter object class (de.florianmichael.tarasande_protocol_hack.util.inventory.ItemSplitter)
 *
 * @copyright FlorianMichael as Jesse - 2023
 */
public class ClassicItemSplitter {
    public static final List<Item> allowedItems = new ArrayList<>();

    static {
        // c0.30 default items
        allowedItems.add(Items.OAK_WOOD);
        allowedItems.add(Items.OAK_PLANKS);
        allowedItems.add(Items.STONE);
        allowedItems.add(Items.COBBLESTONE);
        allowedItems.add(Items.MOSSY_COBBLESTONE);
        allowedItems.add(Items.SMOOTH_STONE_SLAB);
        allowedItems.add(Items.BRICKS);
        allowedItems.add(Items.IRON_BLOCK);
        allowedItems.add(Items.GOLD_BLOCK);
        allowedItems.add(Items.WHITE_WOOL);
        allowedItems.add(Items.ORANGE_WOOL);
        allowedItems.add(Items.MAGENTA_WOOL);
        allowedItems.add(Items.LIGHT_BLUE_WOOL);
        allowedItems.add(Items.YELLOW_WOOL);
        allowedItems.add(Items.LIME_WOOL);
        allowedItems.add(Items.PINK_WOOL);
        allowedItems.add(Items.GRAY_WOOL);
        allowedItems.add(Items.LIGHT_GRAY_WOOL);
        allowedItems.add(Items.CYAN_WOOL);
        allowedItems.add(Items.PURPLE_WOOL);
        allowedItems.add(Items.BLUE_WOOL);
        allowedItems.add(Items.BROWN_WOOL);
        allowedItems.add(Items.GREEN_WOOL);
        allowedItems.add(Items.RED_WOOL);
        allowedItems.add(Items.BLACK_WOOL);
        allowedItems.add(Items.GLASS);
        allowedItems.add(Items.DIRT);
        allowedItems.add(Items.GRAVEL);
        allowedItems.add(Items.SAND);
        allowedItems.add(Items.OBSIDIAN);
        allowedItems.add(Items.COAL_ORE);
        allowedItems.add(Items.IRON_ORE);
        allowedItems.add(Items.GOLD_ORE);
        allowedItems.add(Items.OAK_LEAVES);
        allowedItems.add(Items.OAK_SAPLING);
        allowedItems.add(Items.BROWN_MUSHROOM);
        allowedItems.add(Items.RED_MUSHROOM);
        allowedItems.add(Items.DANDELION);
        allowedItems.add(Items.POPPY);
        allowedItems.add(Items.SPONGE);
        allowedItems.add(Items.BOOKSHELF);
        allowedItems.add(Items.TNT);
    }

    public static List<Item> get(final UserConnection userConnection) {
        final List<Item> versionAllowedItems = new ArrayList<>(allowedItems);
        if (userConnection == null) return versionAllowedItems;

        final VersionListEnum version = VersionListEnum.fromUserConnection(userConnection);
        final boolean isClassicExtension = version == VersionListEnum.c0_30cpe;
        if (isClassicExtension) {
            versionAllowedItems.add(Items.MAGMA_BLOCK);
            versionAllowedItems.add(Items.QUARTZ_PILLAR);
            versionAllowedItems.add(Items.SANDSTONE_STAIRS);
            versionAllowedItems.add(Items.STONE_BRICKS);
            versionAllowedItems.add(Items.COBBLESTONE_SLAB);
            versionAllowedItems.add(Items.ICE);
            versionAllowedItems.add(Items.SNOW);
        }
        if (version.isOlderThan(VersionListEnum.c0_28toc0_30)) {
            versionAllowedItems.remove(Items.WHITE_WOOL);
            versionAllowedItems.remove(Items.ORANGE_WOOL);
            versionAllowedItems.remove(Items.MAGENTA_WOOL);
            versionAllowedItems.remove(Items.LIGHT_BLUE_WOOL);
            versionAllowedItems.remove(Items.YELLOW_WOOL);
            versionAllowedItems.remove(Items.LIME_WOOL);
            if (!isClassicExtension) {
                versionAllowedItems.remove(Items.PINK_WOOL);
                versionAllowedItems.remove(Items.CYAN_WOOL);
                versionAllowedItems.remove(Items.BLUE_WOOL);
                versionAllowedItems.remove(Items.BROWN_WOOL);
                versionAllowedItems.remove(Items.GREEN_WOOL);
                versionAllowedItems.remove(Items.BROWN_MUSHROOM);
            }
            versionAllowedItems.remove(Items.GRAY_WOOL);
            versionAllowedItems.remove(Items.LIGHT_GRAY_WOOL);
            versionAllowedItems.remove(Items.PURPLE_WOOL);
            versionAllowedItems.remove(Items.RED_WOOL);
            versionAllowedItems.remove(Items.BLACK_WOOL);
            versionAllowedItems.remove(Items.SMOOTH_STONE_SLAB);
            versionAllowedItems.remove(Items.POPPY);
            versionAllowedItems.remove(Items.DANDELION);
            versionAllowedItems.remove(Items.RED_MUSHROOM);

            if (version.isOlderThan(VersionListEnum.c0_0_19a_06)) {
                versionAllowedItems.remove(Items.SPONGE);
            }
        }
        return versionAllowedItems;
    }
}
