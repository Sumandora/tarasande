package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.Protocol1_3_1_2to1_2_4_5;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_3_1_2to1_2_4_5> {

    public ItemRewriter(final Protocol1_3_1_2to1_2_4_5 protocol) {
        super(protocol, "1.2.5");

        this.addNonExistentItemRange(126, 136);
        this.addNonExistentItem(322, 1);
        this.addNonExistentItemRange(386, 388);
    }
}
