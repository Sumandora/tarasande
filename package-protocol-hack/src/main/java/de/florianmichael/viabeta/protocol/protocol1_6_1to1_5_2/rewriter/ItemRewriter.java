package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.Protocol1_6_1to1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_6_1to1_5_2> {

    public ItemRewriter(final Protocol1_6_1to1_5_2 protocol) {
        super(protocol, "1.5.2");

        this.addNonExistentItems(159);
        this.addNonExistentItemRange(170, 173);
        this.addNonExistentItem(383, 100);
        this.addNonExistentItemRange(417, 421);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_6_4.CREATIVE_INVENTORY_ACTION, Type1_7_6_10.COMPRESSED_ITEM);
    }
}
