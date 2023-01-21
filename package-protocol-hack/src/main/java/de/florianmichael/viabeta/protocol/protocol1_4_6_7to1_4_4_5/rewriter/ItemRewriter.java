package de.florianmichael.viabeta.protocol.protocol1_4_6_7to1_4_4_5.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_4_6_7to1_4_4_5.Protocol1_4_6_7to1_4_4_5;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_4_6_7to1_4_4_5> {

    public ItemRewriter(final Protocol1_4_6_7to1_4_4_5 protocol) {
        super(protocol, "1.4.5");

        this.addNonExistentItem(43, 6);
        this.addNonExistentItem(44, 6);
        this.addNonExistentItemRange(401, 403);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_5_2.CREATIVE_INVENTORY_ACTION, Type1_7_6_10.COMPRESSED_ITEM);
    }
}
