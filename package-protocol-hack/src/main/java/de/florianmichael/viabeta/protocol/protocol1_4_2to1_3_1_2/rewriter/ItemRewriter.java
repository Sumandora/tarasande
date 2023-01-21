package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.rewriter;

import com.viaversion.viaversion.api.minecraft.item.Item;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.Protocol1_4_2to1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

public class ItemRewriter extends LegacyItemRewriter<Protocol1_4_2to1_3_1_2> {

    public ItemRewriter(final Protocol1_4_2to1_3_1_2 protocol) {
        super(protocol, "1.3.2");

        this.addNonExistentItemRange(137, 145);
        this.addNonExistentItemRange(389, 400);
        this.addNonExistentItem(383, 65);
        this.addNonExistentItem(383, 66);
        this.addNonExistentItems(422);
    }

    @Override
    protected void registerPackets() {
        this.registerCreativeInventoryAction(ServerboundPackets1_5_2.CREATIVE_INVENTORY_ACTION, Type1_7_6_10.COMPRESSED_ITEM);
    }

    @Override
    public Item handleItemToServer(Item item) {
        if (item != null && item.identifier() == ItemList1_6.emptyMap.itemID) {
            item.setIdentifier(ItemList1_6.map.itemID);
        }

        return super.handleItemToServer(item);
    }
}