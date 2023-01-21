package de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.Protocol1_2_4_5to1_2_1_3;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.ServerboundPackets1_2_4;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.Type1_2_4_5;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_2_4_5to1_2_1_3> {

    public ItemRewriter(final Protocol1_2_4_5to1_2_1_3 protocol) {
        super(protocol, "1.2.3");

        this.addNonExistentItem(5, 1, 3);
        this.addNonExistentItem(24, 1, 2);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_2_4.CREATIVE_INVENTORY_ACTION, Type1_2_4_5.COMPRESSED_NBT_ITEM);
    }
}
