package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChestStateTracker extends StoredObject {

    private final Set<Position> openChests = new HashSet<>();

    public ChestStateTracker(final UserConnection userConnection) {
        super(userConnection);
    }

    public void openChest(final Position position) {
        this.openChests.add(position);
    }

    public void closeChest(final Position position) {
        this.openChests.remove(position);
    }

    public boolean isChestOpen(final Position position) {
        return this.openChests.contains(position);
    }

    public void clear() {
        this.openChests.clear();
    }

    public void unload(final int chunkX, final int chunkZ) {
        final Iterator<Position> it = this.openChests.iterator();
        while (it.hasNext()) {
            final Position entry = it.next();
            final int x = entry.x() >> 4;
            final int z = entry.z() >> 4;

            if (chunkX == x && chunkZ == z) {
                it.remove();
            }
        }
    }
}
