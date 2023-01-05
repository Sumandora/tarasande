package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.rewriter;

import de.florianmichael.viabeta.api.rewriter.AbstractItemRewriter;

public class ItemRewriter extends AbstractItemRewriter {

    public ItemRewriter() {
        super("1.6.4", false);

        registerRemappedItem(26, 355, "Bed Block");
        registerRemappedItem(34, 33, "Piston Head");
        registerRemappedItem(36, 33, "Piston Moving");
        registerRemappedItem(55, 331, "Redstone Wire");
        registerRemappedItem(59, 295, "Wheat Crops");
        registerRemappedItem(63, 323, "Standing Sign");
        registerRemappedItem(64, 324, "Oak Door Block");
        registerRemappedItem(68, 323, "Wall Sign");
        registerRemappedItem(71, 330, "Iron Door Block");
        registerRemappedItem(74, 73, "Lit Redstone Ore");
        registerRemappedItem(75, 76, "Unlit Redstone Torch");
        registerRemappedItem(83, 338, "Sugar Cane Block");
        registerRemappedItem(92, 354, "Cake Block");
        registerRemappedItem(93, 356, "Unlit Redstone Repeater");
        registerRemappedItem(94, 356, "Lit Redstone Repeater");
        registerRemappedItem(95, 146, "Locked Chest");
        registerRemappedItem(104, 361, "Pumpkin Stem");
        registerRemappedItem(105, 362, "Melon Stem");
        registerRemappedItem(115, 372, "Nether Wart Block");
        registerRemappedItem(117, 379, "Brewing Stand Block");
        registerRemappedItem(118, 380, "Cauldron Block");
        registerRemappedItem(124, 123, "Lit Redstone Lamp");
        registerRemappedItem(132, 287, "Tripwire");
        registerRemappedItem(140, 390, "Flower Pot");
        registerRemappedItem(144, 397, "Undefined Mob Head");
        registerRemappedItem(149, 404, "Unlit Redstone Comparator");
        registerRemappedItem(150, 404, "Lit Redstone Comparator");
    }
}
