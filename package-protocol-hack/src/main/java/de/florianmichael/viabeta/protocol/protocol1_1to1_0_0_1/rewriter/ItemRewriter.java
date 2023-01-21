package de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_1to1_0_0_1.Protocol1_1to1_0_0_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.ServerboundPackets1_1;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.Type1_2_4_5;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_1to1_0_0_1> {

    public ItemRewriter(final Protocol1_1to1_0_0_1 protocol) {
        super(protocol, "1.0");

        this.addNonExistentItems(383);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_1.CREATIVE_INVENTORY_ACTION, Type1_2_4_5.COMPRESSED_NBT_ITEM);
    }
}
