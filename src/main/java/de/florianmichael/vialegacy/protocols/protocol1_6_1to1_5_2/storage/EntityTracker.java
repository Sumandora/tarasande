package de.florianmichael.vialegacy.protocols.protocol1_6_1to1_5_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;

import java.util.HashMap;
import java.util.Map;

public class EntityTracker extends StoredObject {

    private final Map<Integer, Entity1_10Types.EntityType> entities = new HashMap<>();
    public int ownEntityId;

    public EntityTracker(UserConnection user) {
        super(user);
    }

    public void track(final int entityId, final Entity1_10Types.EntityType entityType) {
        entities.putIfAbsent(entityId, entityType);
    }

    public Entity1_10Types.EntityType get(final int entityId) {
        if (entities.containsKey(entityId)) {
            return entities.get(entityId);
        }

        return null;
    }

    public void remove(final int entityId) {
        if (entities.containsKey(entityId)) {
            entities.remove(entityId);
        }
    }
}
