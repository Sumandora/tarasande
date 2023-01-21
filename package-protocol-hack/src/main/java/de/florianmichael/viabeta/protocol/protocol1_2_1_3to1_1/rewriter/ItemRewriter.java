package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.Protocol1_2_1_3to1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.ServerboundPackets1_2_1;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.Type1_2_4_5;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_2_1_3to1_1> {

    public ItemRewriter(final Protocol1_2_1_3to1_1 protocol) {
        super(protocol, "1.1");

        this.addNonExistentItem(6, 3);
        this.addNonExistentItem(17, 3);
        this.addNonExistentItem(18, 3);
        this.addNonExistentItem(98, 3);
        this.addNonExistentItemRange(123, 124);
        this.addNonExistentItemRange(384, 385);
        this.addNonExistentItem(383, 98);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_2_1.CREATIVE_INVENTORY_ACTION, Type1_2_4_5.COMPRESSED_NBT_ITEM);
    }
}
