package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.HashMap;
import java.util.Map;

public class GroundTracker extends StoredObject {

    private final Map<Integer, Boolean> groundTracker = new HashMap<>();

    public GroundTracker(UserConnection user) {
        super(user);
    }

    public void track(final int entityId, final boolean onGround) {
        groundTracker.put(entityId, onGround);
    }

    public boolean isGround(final int entityId) {
        return groundTracker.get(entityId);
    }
}
