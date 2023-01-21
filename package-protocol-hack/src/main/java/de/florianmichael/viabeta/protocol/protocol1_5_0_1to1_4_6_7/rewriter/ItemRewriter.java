package de.florianmichael.viabeta.protocol.protocol1_5_0_1to1_4_6_7.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_5_0_1to1_4_6_7.Protocol1_5_0_1to1_4_6_7;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_5_0_1to1_4_6_7> {

    public ItemRewriter(final Protocol1_5_0_1to1_4_6_7 protocol) {
        super(protocol, "1.4.7");

        this.addNonExistentItem(43, 7);
        this.addNonExistentItem(44, 7);
        this.addNonExistentItemRange(146, 158);
        this.addNonExistentItems(178);
        this.addNonExistentItemRange(404, 408);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_5_2.CREATIVE_INVENTORY_ACTION, Type1_7_6_10.COMPRESSED_ITEM);
    }
}
