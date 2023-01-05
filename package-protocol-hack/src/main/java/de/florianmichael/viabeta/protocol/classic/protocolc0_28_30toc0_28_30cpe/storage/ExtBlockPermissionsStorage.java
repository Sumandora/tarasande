package de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;

public class ExtBlockPermissionsStorage extends StoredObject {

    private final IntSet placingAllowed = new IntOpenHashSet();
    private final IntSet breakingAllowed = new IntOpenHashSet();

    public ExtBlockPermissionsStorage(final UserConnection user) {
        super(user);
    }

    public void addPlaceable(final int block) {
        this.placingAllowed.add(block);
    }

    public void addBreakable(final int block) {
        this.breakingAllowed.add(block);
    }

    public void removePlaceable(final int block) {
        this.placingAllowed.remove(block);
    }

    public void removeBreakable(final int block) {
        this.breakingAllowed.remove(block);
    }

    public boolean isPlacingAllowed(final int block) {
        return this.placingAllowed.contains(block);
    }

    public boolean isBreakingAllowed(final int block) {
        return this.breakingAllowed.contains(block);
    }
}
