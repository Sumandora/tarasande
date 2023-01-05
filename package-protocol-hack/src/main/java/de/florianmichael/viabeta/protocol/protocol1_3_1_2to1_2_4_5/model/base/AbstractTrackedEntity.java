package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.base;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import de.florianmichael.viabeta.api.model.Location;

public abstract class AbstractTrackedEntity {

    private int entityId;
    private Location location;
    private Entity1_10Types.EntityType entityType;

    private boolean isRiding;

    public AbstractTrackedEntity(final int entityId, final Location location, final Entity1_10Types.EntityType entityType) {
        this.entityId = entityId;
        this.location = location;
        this.entityType = entityType;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Entity1_10Types.EntityType getEntityType() {
        return this.entityType;
    }

    public void setEntityType(Entity1_10Types.EntityType entityType) {
        this.entityType = entityType;
    }

    public boolean isRiding() {
        return this.isRiding;
    }

    public void setRiding(boolean riding) {
        this.isRiding = riding;
    }

}
