package de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InventoryStorage extends StoredObject {

    public static final byte WORKBENCH_WID = 33;
    public static final byte FURNACE_WID = 44;
    public static final byte CHEST_WID = 55;

    public Item handItem = null;
    public Item[] mainInventory = null;
    public Item[] craftingInventory = null;
    public Item[] armorInventory = null;

    public final Map<Position, Item[]> containers = new HashMap<>();

    public Position openContainerPos = null;
    public short selectedHotbarSlot = 0;

    public InventoryStorage(UserConnection user) {
        super(user);
        this.resetPlayerInventory();
    }

    public void unload(final int chunkX, final int chunkZ) {
        final Iterator<Position> it = this.containers.keySet().iterator();
        while (it.hasNext()) {
            final Position entry = it.next();
            final int x = entry.x() >> 4;
            final int z = entry.z() >> 4;

            if (chunkX == x && chunkZ == z) {
                it.remove();
            }
        }
    }

    public void resetPlayerInventory() {
        // alpha keeps handItem value after respawn
        this.mainInventory = new Item[37];
        this.craftingInventory = new Item[4];
        this.armorInventory = new Item[4];
        this.openContainerPos = null;
    }

}
