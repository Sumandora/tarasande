package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.Protocol1_7_2_5to1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.ServerboundPackets1_7_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_7_2_5to1_6_4> {

    public ItemRewriter(final Protocol1_7_2_5to1_6_4 protocol) {
        super(protocol, "1.6.4");

        this.addRemappedItem(26, 355, "Bed Block");
        this.addRemappedItem(34, 33, "Piston Head");
        this.addRemappedItem(36, 33, "Piston Moving");
        this.addRemappedItem(55, 331, "Redstone Wire");
        this.addRemappedItem(59, 295, "Wheat Crops");
        this.addRemappedItem(63, 323, "Standing Sign");
        this.addRemappedItem(64, 324, "Oak Door Block");
        this.addRemappedItem(68, 323, "Wall Sign");
        this.addRemappedItem(71, 330, "Iron Door Block");
        this.addRemappedItem(74, 73, "Lit Redstone Ore");
        this.addRemappedItem(75, 76, "Unlit Redstone Torch");
        this.addRemappedItem(83, 338, "Sugar Cane Block");
        this.addRemappedItem(92, 354, "Cake Block");
        this.addRemappedItem(93, 356, "Unlit Redstone Repeater");
        this.addRemappedItem(94, 356, "Lit Redstone Repeater");
        this.addRemappedItem(95, 146, "Locked Chest");
        this.addRemappedItem(104, 361, "Pumpkin Stem");
        this.addRemappedItem(105, 362, "Melon Stem");
        this.addRemappedItem(115, 372, "Nether Wart Block");
        this.addRemappedItem(117, 379, "Brewing Stand Block");
        this.addRemappedItem(118, 380, "Cauldron Block");
        this.addRemappedItem(124, 123, "Lit Redstone Lamp");
        this.addRemappedItem(132, 287, "Tripwire");
        this.addRemappedItem(140, 390, "Flower Pot");
        this.addRemappedItem(144, 397, "Undefined Mob Head");
        this.addRemappedItem(149, 404, "Unlit Redstone Comparator");
        this.addRemappedItem(150, 404, "Lit Redstone Comparator");

        this.addNonExistentItem(3, 2);
        this.addNonExistentItem(5, 4, 5);
        this.addNonExistentItem(6, 4, 5);
        this.addNonExistentItem(12, 1);
        this.addNonExistentItem(38, 1, 8);
        this.addNonExistentItems(95);
        this.addNonExistentItem(97, 3, 5);
        this.addNonExistentItem(126, 4, 5);
        this.addNonExistentItemRange(160, 164);
        this.addNonExistentItemRange(174, 175);
        this.addNonExistentItem(349, 1, 3);
        this.addNonExistentItemRange(350, 1);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_7_2.CREATIVE_INVENTORY_ACTION, Type1_7_6_10.COMPRESSED_ITEM);
    }
}
