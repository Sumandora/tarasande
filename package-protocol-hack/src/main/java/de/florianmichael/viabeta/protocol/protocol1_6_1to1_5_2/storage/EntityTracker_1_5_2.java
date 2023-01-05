package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTracker_1_5_2 extends StoredObject {

    private final Map<Integer, Entity1_10Types.EntityType> entityMap = new ConcurrentHashMap<>();

    private int playerID;

    public EntityTracker_1_5_2(UserConnection user) {
        super(user);
    }

    public int getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public void removeEntity(int entityId) {
        this.entityMap.remove(entityId);
    }

    public Map<Integer, Entity1_10Types.EntityType> getTrackedEntities() {
        return this.entityMap;
    }

}
