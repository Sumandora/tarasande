package de.florianmichael.viacursed.protocol.protocol1_19_3toBedrock1_19_51.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.HashMap;
import java.util.Map;

public class EntityTracker_Bedrock_1_19_51 extends StoredObject {

    private final Map<Long, Long> entityMap = new HashMap<>();

    public EntityTracker_Bedrock_1_19_51(UserConnection user) {
        super(user);
    }

    public Map<Long, Long> getEntityMap() {
        return entityMap;
    }
}
