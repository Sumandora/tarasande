package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.provider;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.Provider;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.InventoryStorage;

public abstract class AlphaInventoryProvider implements Provider {

    public abstract boolean usesInventoryTracker();

    public Item getHandItem(final UserConnection user) {
        final InventoryStorage inventoryStorage = user.get(InventoryStorage.class);
        final Item[] inventory = this.getMainInventoryItems(user);
        return inventory[inventoryStorage.selectedHotbarSlot];
    }

    public abstract Item[] getMainInventoryItems(final UserConnection user);

    public abstract Item[] getCraftingInventoryItems(final UserConnection user);

    public abstract Item[] getArmorInventoryItems(final UserConnection user);

    public abstract Item[] getContainerItems(final UserConnection user);

    public abstract void addToInventory(final UserConnection user, final Item item);

}
