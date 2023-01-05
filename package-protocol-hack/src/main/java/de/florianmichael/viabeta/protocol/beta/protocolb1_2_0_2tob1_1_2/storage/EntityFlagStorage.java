package de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class EntityFlagStorage extends StoredObject {

    private final Int2IntMap animationFlags = new Int2IntOpenHashMap();

    public EntityFlagStorage(UserConnection user) {
        super(user);
    }

    public boolean getFlag(final int entityId, final int index) {
        return (this.getFlagMask(entityId) & 1 << index) != 0;
    }

    public int getFlagMask(final int entityId) {
        return this.animationFlags.get(entityId);
    }

    public void setFlag(final int entityId, final int index, final boolean flag) {
        final int mask = this.animationFlags.get(entityId);
        if (flag) {
            this.animationFlags.put(entityId, mask | 1 << index);
        } else {
            this.animationFlags.put(entityId, mask & ~(1 << index));
        }
    }
}
