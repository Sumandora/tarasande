package de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.rewriter;

import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.Protocol1_0_0_1tob1_8_0_1;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_0_0_1tob1_8_0_1> {

    public ItemRewriter(final Protocol1_0_0_1tob1_8_0_1 protocol) {
        super(protocol, "b1.8.1");

        this.addNonExistentItemRange(110, 122);
        this.addNonExistentItemRange(369, 382);
        this.addNonExistentItems(438);
        this.addNonExistentItemRange(2256, 2266);
    }
}