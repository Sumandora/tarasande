package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage;

import com.viaversion.viaversion.api.connection.StoredObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import de.florianmichael.viabeta.api.model.Location;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaIndex1_8to1_7_6;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.entity.HologramPartEntity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class EntityTracker_1_7_6_10 extends StoredObject {

    private final Map<Integer, Entity1_10Types.EntityType> entityMap = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> groundMap = new ConcurrentHashMap<>();
    private final Int2ObjectMap<HologramPartEntity> hologramParts = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<HologramPartEntity> virtualHolograms = new Int2ObjectOpenHashMap<>();

    private int playerID;

    public EntityTracker_1_7_6_10(UserConnection user) {
        super(user);
    }

    public int getPlayerID() {
        return this.playerID;
    }

    public void setPlayerID(final int playerID) {
        this.playerID = playerID;
    }

    public Map<Integer, Entity1_10Types.EntityType> getTrackedEntities() {
        return this.entityMap;
    }

    public Map<Integer, Boolean> getGroundMap() {
        return this.groundMap;
    }

    public Int2ObjectMap<HologramPartEntity> getVirtualHolograms() {
        return this.virtualHolograms;
    }

    public void trackEntity(final int entityId, final Entity1_10Types.EntityType entityType) throws Exception {
        if (this.virtualHolograms.containsKey(entityId)) {
            final int newMappedEntityId = this.getNextMappedEntityId();
            final HologramPartEntity hologramPartEntity = this.virtualHolograms.remove(entityId);
            hologramPartEntity.relocate(newMappedEntityId);
            this.hologramParts.put(newMappedEntityId, hologramPartEntity);
        }
        if (this.entityMap.containsKey(entityId)) {
            this.removeEntity(entityId);
        }

        this.entityMap.put(entityId, entityType);

        if (entityType == Entity1_10Types.EntityType.HORSE || entityType == Entity1_10Types.EntityType.WITHER_SKULL) {
            this.hologramParts.put(entityId, new HologramPartEntity(this.getUser(), entityId, entityType));
        }
    }

    public void removeEntity(final int entityId) throws Exception {
        this.entityMap.remove(entityId);
        this.groundMap.remove(entityId);
        final HologramPartEntity removedEntity = this.hologramParts.remove(entityId);
        if (removedEntity != null) {
            if (removedEntity.getRiderEntity() != null) {
                removedEntity.getRiderEntity().setVehicleEntity(null);
            }
            if (removedEntity.getVehicleEntity() != null) {
                removedEntity.setVehicleEntity(null);
            }
            removedEntity.onRemove();
        }
    }

    public void updateEntityLocation(final int entityId, final int x, final int y, final int z, final boolean relative) throws Exception {
        final HologramPartEntity entity = this.hologramParts.get(entityId);
        if (entity != null) {
            final Location oldLoc = entity.getLocation();

            final double xPos = x / 32.0D;
            final double yPos = y / 32.0D;
            final double zPos = z / 32.0D;

            Location newLoc;
            if (relative) {
                newLoc = new Location(oldLoc.getX() + xPos, oldLoc.getY() + yPos, oldLoc.getZ() + zPos);
            } else {
                newLoc = new Location(xPos, yPos, zPos);
            }

            entity.setLocation(newLoc);
        }
    }

    public void updateEntityMetadata(final int entityId, final List<Metadata> metadataList) throws Exception {
        final HologramPartEntity entity = this.hologramParts.get(entityId);
        if (entity != null) {
            for (Metadata metadata : metadataList) {
                final MetaIndex1_8to1_7_6 metaIndex = MetaIndex1_8to1_7_6.searchIndex(entity.getEntityType(), metadata.id());
                if (metaIndex != null) {
                    try {
                        metadata.setTypeAndValue(metaIndex.getOldType(), metadata.getValue());
                    } catch (Throwable ignored) {
                        continue;
                    }
                    entity.setMetadata(metaIndex, metadata.getValue());
                }
            }
            entity.onChange();
        }
    }

    public void updateEntityAttachState(final int ridingId, final int vehicleId) throws Exception {
        final HologramPartEntity ridingEntity = this.hologramParts.get(ridingId);
        if (ridingEntity != null) {
            ridingEntity.setVehicleEntity(this.hologramParts.get(vehicleId));
        }
    }

    public void clear() throws Exception {
        this.entityMap.clear();
        this.groundMap.clear();
        for (HologramPartEntity hologramPartEntity : this.hologramParts.values()) {
            hologramPartEntity.onRemove();
        }
        this.virtualHolograms.clear();
    }

    public int getNextMappedEntityId() {
        int id;
        do {
            id = ThreadLocalRandom.current().nextInt(1_000_000_000, Integer.MAX_VALUE);
        } while (this.entityMap.containsKey(id) || this.virtualHolograms.containsKey(id));
        return id;
    }

}
