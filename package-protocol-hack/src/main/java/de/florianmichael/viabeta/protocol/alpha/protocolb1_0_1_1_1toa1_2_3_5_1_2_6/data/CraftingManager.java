package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.data;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.model.CraftingRecipe;

import java.util.*;

public class CraftingManager {

    private static final List<CraftingRecipe> recipes = new ArrayList<>();

    private static final String[][] tools_pattern = new String[][]{{"XXX", " # ", " # "}, {"X", "#", "#"}, {"XX", "X#", " #"}, {"XX", " #", " #"}};
    private static final int[][] tools_ingredients = new int[][]{{BlockList1_6.planks.blockID, BlockList1_6.cobblestone.blockID, ItemList1_6.ingotIron.itemID, ItemList1_6.diamond.itemID, ItemList1_6.ingotGold.itemID}, {ItemList1_6.pickaxeWood.itemID, ItemList1_6.pickaxeStone.itemID, ItemList1_6.pickaxeIron.itemID, ItemList1_6.pickaxeDiamond.itemID, ItemList1_6.pickaxeGold.itemID}, {ItemList1_6.shovelWood.itemID, ItemList1_6.shovelStone.itemID, ItemList1_6.shovelIron.itemID, ItemList1_6.shovelDiamond.itemID, ItemList1_6.shovelGold.itemID}, {ItemList1_6.axeWood.itemID, ItemList1_6.axeStone.itemID, ItemList1_6.axeIron.itemID, ItemList1_6.axeDiamond.itemID, ItemList1_6.axeGold.itemID}, {ItemList1_6.hoeWood.itemID, ItemList1_6.hoeStone.itemID, ItemList1_6.hoeIron.itemID, ItemList1_6.hoeDiamond.itemID, ItemList1_6.hoeGold.itemID}};

    private static final String[][] weapons_pattern = new String[][]{{"X", "X", "#"}};
    private static final int[][] weapons_ingredients = new int[][]{{BlockList1_6.planks.blockID, BlockList1_6.cobblestone.blockID, ItemList1_6.ingotIron.itemID, ItemList1_6.diamond.itemID, ItemList1_6.ingotGold.itemID}, {ItemList1_6.swordWood.itemID, ItemList1_6.swordStone.itemID, ItemList1_6.swordIron.itemID, ItemList1_6.swordDiamond.itemID, ItemList1_6.swordGold.itemID}};

    private static final int[][] ingots_ingredients = new int[][]{{BlockList1_6.blockGold.blockID, ItemList1_6.ingotGold.itemID}, {BlockList1_6.blockIron.blockID, ItemList1_6.ingotIron.itemID}, {BlockList1_6.blockDiamond.blockID, ItemList1_6.diamond.itemID}};

    private static final String[][] armor_pattern = new String[][]{{"XXX", "X X"}, {"X X", "XXX", "XXX"}, {"XXX", "X X", "X X"}, {"X X", "X X"}};
    private static final int[][] armor_ingredients = new int[][]{{ItemList1_6.leather.itemID, BlockList1_6.fire.blockID, ItemList1_6.ingotIron.itemID, ItemList1_6.diamond.itemID, ItemList1_6.ingotGold.itemID}, {ItemList1_6.helmetLeather.itemID, ItemList1_6.helmetChain.itemID, ItemList1_6.helmetIron.itemID, ItemList1_6.helmetDiamond.itemID, ItemList1_6.helmetGold.itemID}, {ItemList1_6.plateLeather.itemID, ItemList1_6.plateChain.itemID, ItemList1_6.plateIron.itemID, ItemList1_6.plateDiamond.itemID, ItemList1_6.plateGold.itemID}, {ItemList1_6.legsLeather.itemID, ItemList1_6.legsChain.itemID, ItemList1_6.legsIron.itemID, ItemList1_6.legsDiamond.itemID, ItemList1_6.legsGold.itemID}, {ItemList1_6.bootsLeather.itemID, ItemList1_6.bootsChain.itemID, ItemList1_6.bootsIron.itemID, ItemList1_6.bootsDiamond.itemID, ItemList1_6.bootsGold.itemID}};

    static {
        for (int i = 0; i < tools_ingredients[0].length; ++i) {
            for (int i1 = 0; i1 < tools_ingredients.length - 1; ++i1) {
                addRecipe(new DataItem(tools_ingredients[i1 + 1][i], (byte) 1, (short) 0, null), tools_pattern[i1], '#', ItemList1_6.stick.itemID, 'X', tools_ingredients[0][i]);
            }
        }
        for (int i = 0; i < weapons_ingredients[0].length; ++i) {
            for (int i1 = 0; i1 < weapons_ingredients.length - 1; ++i1) {
                addRecipe(new DataItem(weapons_ingredients[i1 + 1][i], (byte) 1, (short) 0, null), weapons_pattern[i1], '#', ItemList1_6.stick.itemID, 'X', weapons_ingredients[0][i]);
            }
        }
        addRecipe(new DataItem(ItemList1_6.bow.itemID, (byte) 1, (short) 0, null), " #X", "# X", " #X", 'X', ItemList1_6.silk.itemID, '#', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(ItemList1_6.arrow.itemID, (byte) 4, (short) 0, null), "X", "#", "Y", 'Y', ItemList1_6.feather.itemID, 'X', ItemList1_6.flint.itemID, '#', ItemList1_6.stick.itemID);
        for (int[] ingots_ingredient : ingots_ingredients) {
            addRecipe(new DataItem(ingots_ingredient[0], (byte) 1, (short) 0, null), "###", "###", "###", '#', ingots_ingredient[1]);
            addRecipe(new DataItem(ingots_ingredient[1], (byte) 9, (short) 0, null), "#", '#', ingots_ingredient[0]);
        }
        addRecipe(new DataItem(ItemList1_6.bowlSoup.itemID, (byte) 1, (short) 0, null), "Y", "X", "#", 'X', BlockList1_6.mushroomBrown.blockID, 'Y', BlockList1_6.mushroomRed.blockID, '#', ItemList1_6.bowlEmpty.itemID);
        addRecipe(new DataItem(ItemList1_6.bowlSoup.itemID, (byte) 1, (short) 0, null), "Y", "X", "#", 'X', BlockList1_6.mushroomRed.blockID, 'Y', BlockList1_6.mushroomBrown.blockID, '#', ItemList1_6.bowlEmpty.itemID);
        addRecipe(new DataItem(BlockList1_6.chest.blockID, (byte) 1, (short) 0, null), "###", "# #", "###", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(BlockList1_6.furnaceIdle.blockID, (byte) 1, (short) 0, null), "###", "# #", "###", '#', BlockList1_6.cobblestone.blockID);
        addRecipe(new DataItem(BlockList1_6.workbench.blockID, (byte) 1, (short) 0, null), "##", "##", '#', BlockList1_6.planks.blockID);
        for (int i = 0; i < armor_ingredients[0].length; ++i) {
            for (int i1 = 0; i1 < armor_ingredients.length - 1; ++i1) {
                addRecipe(new DataItem(armor_ingredients[i1 + 1][i], (byte) 1, (short) 0, null), armor_pattern[i1], 'X', armor_ingredients[0][i]);
            }
        }
        addRecipe(new DataItem(ItemList1_6.paper.itemID, (byte) 3, (short) 0, null), "###", '#', ItemList1_6.reed.itemID);
        addRecipe(new DataItem(ItemList1_6.book.itemID, (byte) 1, (short) 0, null), "#", "#", "#", '#', ItemList1_6.paper.itemID);
        addRecipe(new DataItem(BlockList1_6.fence.blockID, (byte) 2, (short) 0, null), "###", "###", '#', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(BlockList1_6.jukebox.blockID, (byte) 1, (short) 0, null), "###", "#X#", "###", '#', BlockList1_6.planks.blockID, 'X', ItemList1_6.diamond.itemID);
        addRecipe(new DataItem(BlockList1_6.bookShelf.blockID, (byte) 1, (short) 0, null), "###", "XXX", "###", '#', BlockList1_6.planks.blockID, 'X', ItemList1_6.book.itemID);
        addRecipe(new DataItem(BlockList1_6.blockSnow.blockID, (byte) 1, (short) 0, null), "##", "##", '#', ItemList1_6.snowball.itemID);
        addRecipe(new DataItem(BlockList1_6.blockClay.blockID, (byte) 1, (short) 0, null), "##", "##", '#', ItemList1_6.clay.itemID);
        addRecipe(new DataItem(BlockList1_6.brick.blockID, (byte) 1, (short) 0, null), "##", "##", '#', ItemList1_6.brick.itemID);
        addRecipe(new DataItem(BlockList1_6.glowStone.blockID, (byte) 1, (short) 0, null), "###", "###", "###", '#', ItemList1_6.glowstone.itemID);
        addRecipe(new DataItem(BlockList1_6.cloth.blockID, (byte) 1, (short) 0, null), "###", "###", "###", '#', ItemList1_6.silk.itemID);
        addRecipe(new DataItem(BlockList1_6.tnt.blockID, (byte) 1, (short) 0, null), "X#X", "#X#", "X#X", 'X', ItemList1_6.gunpowder.itemID, '#', BlockList1_6.sand.blockID);
        addRecipe(new DataItem(BlockList1_6.stoneSingleSlab.blockID, (byte) 3, (short) 0, null), "###", '#', BlockList1_6.cobblestone.blockID);
        addRecipe(new DataItem(BlockList1_6.ladder.blockID, (byte) 1, (short) 0, null), "# #", "###", "# #", '#', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(ItemList1_6.doorWood.itemID, (byte) 1, (short) 0, null), "##", "##", "##", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(ItemList1_6.doorIron.itemID, (byte) 1, (short) 0, null), "##", "##", "##", '#', ItemList1_6.ingotIron.itemID);
        addRecipe(new DataItem(ItemList1_6.sign.itemID, (byte) 1, (short) 0, null), "###", "###", " X ", '#', BlockList1_6.planks.blockID, 'X', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(BlockList1_6.planks.blockID, (byte) 4, (short) 0, null), "#", '#', BlockList1_6.wood.blockID);
        addRecipe(new DataItem(ItemList1_6.stick.itemID, (byte) 4, (short) 0, null), "#", "#", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(BlockList1_6.torchWood.blockID, (byte) 4, (short) 0, null), "X", "#", 'X', ItemList1_6.coal.itemID, '#', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(ItemList1_6.bowlEmpty.itemID, (byte) 4, (short) 0, null), "# #", " # ", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(BlockList1_6.rail.blockID, (byte) 16, (short) 0, null), "X X", "X#X", "X X", 'X', ItemList1_6.ingotIron.itemID, '#', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(ItemList1_6.minecartEmpty.itemID, (byte) 1, (short) 0, null), "# #", "###", '#', ItemList1_6.ingotIron.itemID);
        addRecipe(new DataItem(BlockList1_6.pumpkinLantern.blockID, (byte) 1, (short) 0, null), "A", "B", 'A', BlockList1_6.pumpkin.blockID, 'B', BlockList1_6.torchWood.blockID);
        addRecipe(new DataItem(ItemList1_6.minecartCrate.itemID, (byte) 1, (short) 0, null), "A", "B", 'A', BlockList1_6.chest.blockID, 'B', ItemList1_6.minecartEmpty.itemID);
        addRecipe(new DataItem(ItemList1_6.minecartPowered.itemID, (byte) 1, (short) 0, null), "A", "B", 'A', BlockList1_6.furnaceIdle.blockID, 'B', ItemList1_6.minecartEmpty.itemID);
        addRecipe(new DataItem(ItemList1_6.boat.itemID, (byte) 1, (short) 0, null), "# #", "###", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(ItemList1_6.bucketEmpty.itemID, (byte) 1, (short) 0, null), "# #", " # ", '#', ItemList1_6.ingotIron.itemID);
        addRecipe(new DataItem(ItemList1_6.flintAndSteel.itemID, (byte) 1, (short) 0, null), "A ", " B", 'A', ItemList1_6.ingotIron.itemID, 'B', ItemList1_6.flint.itemID);
        addRecipe(new DataItem(ItemList1_6.bread.itemID, (byte) 1, (short) 0, null), "###", '#', ItemList1_6.wheat.itemID);
        addRecipe(new DataItem(BlockList1_6.stairsWoodOak.blockID, (byte) 4, (short) 0, null), "#  ", "## ", "###", '#', BlockList1_6.planks.blockID);
        addRecipe(new DataItem(ItemList1_6.fishingRod.itemID, (byte) 1, (short) 0, null), "  #", " #X", "# X", '#', ItemList1_6.stick.itemID, 'X', ItemList1_6.silk.itemID);
        addRecipe(new DataItem(BlockList1_6.stairsCobblestone.blockID, (byte) 4, (short) 0, null), "#  ", "## ", "###", '#', BlockList1_6.cobblestone.blockID);
        addRecipe(new DataItem(ItemList1_6.painting.itemID, (byte) 1, (short) 0, null), "###", "#X#", "###", '#', ItemList1_6.stick.itemID, 'X', BlockList1_6.cloth.blockID);
        addRecipe(new DataItem(ItemList1_6.appleGold.itemID, (byte) 1, (short) 0, null), "###", "#X#", "###", '#', BlockList1_6.blockGold.blockID, 'X', ItemList1_6.appleRed.itemID);
        addRecipe(new DataItem(BlockList1_6.lever.blockID, (byte) 1, (short) 0, null), "X", "#", '#', BlockList1_6.cobblestone.blockID, 'X', ItemList1_6.stick.itemID);
        addRecipe(new DataItem(BlockList1_6.torchRedstoneActive.blockID, (byte) 1, (short) 0, null), "X", "#", '#', ItemList1_6.stick.itemID, 'X', ItemList1_6.redstone.itemID);
        addRecipe(new DataItem(ItemList1_6.pocketSundial.itemID, (byte) 1, (short) 0, null), " # ", "#X#", " # ", '#', ItemList1_6.ingotGold.itemID, 'X', ItemList1_6.redstone.itemID);
        addRecipe(new DataItem(ItemList1_6.compass.itemID, (byte) 1, (short) 0, null), " # ", "#X#", " # ", '#', ItemList1_6.ingotIron.itemID, 'X', ItemList1_6.redstone.itemID);
        addRecipe(new DataItem(BlockList1_6.stoneButton.blockID, (byte) 1, (short) 0, null), "#", "#", '#', BlockList1_6.stone.blockID);
        addRecipe(new DataItem(BlockList1_6.pressurePlateStone.blockID, (byte) 1, (short) 0, null), "###", '#', BlockList1_6.stone.blockID);
        addRecipe(new DataItem(BlockList1_6.pressurePlatePlanks.blockID, (byte) 1, (short) 0, null), "###", '#', BlockList1_6.planks.blockID);
        recipes.sort((o1, o2) -> Integer.compare(o2.getRecipeSize(), o1.getRecipeSize()));
    }

    private static void addRecipe(final Item resultItem, final Object... objects) {
        StringBuilder var3 = new StringBuilder();
        int pos = 0;
        int width = 0;
        int height = 0;

        if (objects[pos] instanceof String[]) {
            String[] var11 = (String[]) objects[pos++];

            for (String var9 : var11) {
                height++;
                width = var9.length();
                var3.append(var9);
            }
        } else {
            while (objects[pos] instanceof String) {
                String var7 = (String) objects[pos++];
                height++;
                width = var7.length();
                var3.append(var7);
            }
        }

        final HashMap<Character, Integer> lookup = new HashMap<>();
        while (pos < objects.length) {
            lookup.put((char) objects[pos], (int) objects[pos + 1]);
            pos += 2;
        }

        final int[] ingredientMap = new int[width * height];
        for (int i = 0; i < ingredientMap.length; i++) {
            ingredientMap[i] = lookup.getOrDefault(var3.charAt(i), -1);
        }

        recipes.add(new CraftingRecipe(width, height, ingredientMap, resultItem));
    }

    public static Item getResult(final Item[] craftingGrid) {
        final int gridSize = (int) Math.sqrt(craftingGrid.length);
        final int[] ingredientMap = new int[9];
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int ingredient = -1;
                if (x < gridSize && y < gridSize) {
                    final Item item = craftingGrid[x + y * gridSize];
                    if (item != null) {
                        ingredient = item.identifier();
                    }
                }
                ingredientMap[x + y * 3] = ingredient;
            }
        }

        return getResult(ingredientMap);
    }

    public static Item getResult(final int[] ingredientMap) {
        for (CraftingRecipe recipe : recipes) {
            if (recipe.matches(ingredientMap)) {
                return recipe.createResult();
            }
        }

        return null;
    }
}
