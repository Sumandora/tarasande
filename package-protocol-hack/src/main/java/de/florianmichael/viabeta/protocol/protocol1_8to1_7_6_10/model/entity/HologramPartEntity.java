package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.entity;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.florianmichael.viabeta.api.model.Location;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaIndex1_8to1_7_6;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage.EntityTracker_1_7_6_10;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public class HologramPartEntity {

    private static final float HORSE_HEIGHT = 1.6F;
    private static final float WITHER_SKULL_HEIGHT = 0.3125F;

    private final UserConnection user;
    private final EntityTracker_1_7_6_10 entityTracker;

    private final int entityId;
    private final Entity1_10Types.EntityType entityType;
    private HologramPartEntity riderEntity;
    private HologramPartEntity vehicleEntity;
    private Location location;
    private final Map<MetaIndex1_8to1_7_6, Object> metadata = new HashMap<>();

    private Integer mappedEntityId;

    public HologramPartEntity(final UserConnection user, final int entityId, final Entity1_10Types.EntityType entityType) {
        this.user = user;
        this.entityTracker = this.user.get(EntityTracker_1_7_6_10.class);

        this.entityId = entityId;
        this.entityType = entityType;
        this.location = new Location(Float.NaN, Float.NaN, Float.NaN);

        if (entityType == Entity1_10Types.EntityType.HORSE) {
            this.metadata.put(MetaIndex1_8to1_7_6.ENTITY_FLAGS, (byte) 0);
            this.metadata.put(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG_VISIBILITY, (byte) 0);
            this.metadata.put(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG, "");
            this.metadata.put(MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE, 0);
        }
    }

    public void onChange() throws Exception {
        if (this.vehicleEntity == null && this.riderEntity != null) {
            this.riderEntity.setPositionFromVehicle();
        }

        if (this.isHologram()) {
            if (this.wouldBeInvisible()) {
                this.destroyHologramPartEntities();
                this.destroyArmorStand();
                return;
            }

            if (this.mappedEntityId == null) {
                this.mappedEntityId = this.entityTracker.getNextMappedEntityId();
                this.entityTracker.getVirtualHolograms().put(this.mappedEntityId.intValue(), this);
                this.destroyHologramPartEntities();
                this.spawnArmorStand();
            }

            this.updateArmorStand();
        } else if (this.mappedEntityId != null) {
            this.onRemove();
        }
    }

    public void onRemove() throws Exception {
        if (this.mappedEntityId != null) {
            this.entityTracker.getVirtualHolograms().remove(this.mappedEntityId.intValue());
            this.destroyArmorStand();
            this.spawnHologramPartEntities();
            this.mappedEntityId = null;
        }
    }

    public void relocate(final int newMappedEntityId) throws Exception {
        this.destroyArmorStand();
        this.mappedEntityId = newMappedEntityId;
        this.spawnArmorStand();
        this.updateArmorStand();
    }

    private void spawnHologramPartEntities() throws Exception {
        {
            final PacketWrapper spawnMob = PacketWrapper.create(ClientboundPackets1_8.SPAWN_MOB, this.user);
            spawnMob.write(Type.VAR_INT, this.entityId); // entity id
            spawnMob.write(Type.UNSIGNED_BYTE, (short) this.entityType.getId()); // type id
            spawnMob.write(Type.INT, (int) (this.location.getX() * 32F)); // x
            spawnMob.write(Type.INT, (int) (this.location.getY() * 32F)); // y
            spawnMob.write(Type.INT, (int) (this.location.getZ() * 32F)); // z
            spawnMob.write(Type.BYTE, (byte) 0); // yaw
            spawnMob.write(Type.BYTE, (byte) 0); // pitch
            spawnMob.write(Type.BYTE, (byte) 0); // head yaw
            spawnMob.write(Type.SHORT, (short) 0); // velocity x
            spawnMob.write(Type.SHORT, (short) 0); // velocity y
            spawnMob.write(Type.SHORT, (short) 0); // velocity z
            spawnMob.write(Types1_8.METADATA_LIST, this.get1_8Metadata()); // metadata
            spawnMob.send(Protocol1_8to1_7_6_10.class);
        }
        if (this.vehicleEntity != null) {
            final int objectId = Arrays.stream(Entity1_10Types.ObjectType.values()).filter(o -> o.getType() == this.vehicleEntity.entityType).map(Entity1_10Types.ObjectType::getId).findFirst().orElse(-1);
            if (objectId == -1) {
                throw new IllegalStateException("Could not find object id for entity type " + this.vehicleEntity.entityType);
            }

            final PacketWrapper spawnEntity = PacketWrapper.create(ClientboundPackets1_8.SPAWN_ENTITY, this.user);
            spawnEntity.write(Type.VAR_INT, this.vehicleEntity.entityId); // entity id
            spawnEntity.write(Type.BYTE, (byte) objectId); // type id
            spawnEntity.write(Type.INT, (int) (this.vehicleEntity.location.getX() * 32F)); // x
            spawnEntity.write(Type.INT, (int) (this.vehicleEntity.location.getY() * 32F)); // y
            spawnEntity.write(Type.INT, (int) (this.vehicleEntity.location.getZ() * 32F)); // z
            spawnEntity.write(Type.BYTE, (byte) 0); // yaw
            spawnEntity.write(Type.BYTE, (byte) 0); // pitch
            spawnEntity.write(Type.INT, 0); // data
            spawnEntity.send(Protocol1_8to1_7_6_10.class);

            final PacketWrapper entityMetadata = PacketWrapper.create(ClientboundPackets1_8.ENTITY_METADATA, this.user);
            entityMetadata.write(Type.VAR_INT, this.vehicleEntity.entityId); // entity id
            entityMetadata.write(Types1_8.METADATA_LIST, this.vehicleEntity.get1_8Metadata()); // metadata
            entityMetadata.send(Protocol1_8to1_7_6_10.class);

            final PacketWrapper attachEntity = PacketWrapper.create(ClientboundPackets1_8.ATTACH_ENTITY, this.user);
            attachEntity.write(Type.INT, this.entityId); // entity id
            attachEntity.write(Type.INT, this.vehicleEntity.entityId); // vehicle id
            attachEntity.write(Type.UNSIGNED_BYTE, (short) 0); // leash state
            attachEntity.send(Protocol1_8to1_7_6_10.class);
        }
    }

    private void destroyHologramPartEntities() throws Exception {
        final PacketWrapper destroyEntities = PacketWrapper.create(ClientboundPackets1_8.DESTROY_ENTITIES, this.user);
        destroyEntities.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{this.entityId, this.vehicleEntity.entityId}); // entity ids
        destroyEntities.scheduleSend(Protocol1_8to1_7_6_10.class);
    }

    private void spawnArmorStand() throws Exception {
        final PacketWrapper spawnMob = PacketWrapper.create(ClientboundPackets1_8.SPAWN_MOB, this.user);
        spawnMob.write(Type.VAR_INT, this.mappedEntityId); // entity id
        spawnMob.write(Type.UNSIGNED_BYTE, (short) Entity1_10Types.EntityType.ARMOR_STAND.getId()); // type id
        spawnMob.write(Type.INT, (int) (this.location.getX() * 32F)); // x
        spawnMob.write(Type.INT, (int) ((this.location.getY() + this.getHeight()) * 32F)); // y
        spawnMob.write(Type.INT, (int) (this.location.getZ() * 32F)); // z
        spawnMob.write(Type.BYTE, (byte) 0); // yaw
        spawnMob.write(Type.BYTE, (byte) 0); // pitch
        spawnMob.write(Type.BYTE, (byte) 0); // head yaw
        spawnMob.write(Type.SHORT, (short) 0); // velocity x
        spawnMob.write(Type.SHORT, (short) 0); // velocity y
        spawnMob.write(Type.SHORT, (short) 0); // velocity z
        spawnMob.write(Types1_8.METADATA_LIST, this.getArmorStandMetadata()); // metadata
        spawnMob.send(Protocol1_8to1_7_6_10.class);
    }

    private void destroyArmorStand() throws Exception {
        if (this.mappedEntityId == null) return;

        final PacketWrapper destroyEntities = PacketWrapper.create(ClientboundPackets1_8.DESTROY_ENTITIES, this.user);
        destroyEntities.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[]{this.mappedEntityId}); // entity ids
        destroyEntities.send(Protocol1_8to1_7_6_10.class);
    }

    private void updateArmorStand() throws Exception {
        if (this.mappedEntityId == null) return;

        final PacketWrapper entityMetadata = PacketWrapper.create(ClientboundPackets1_8.ENTITY_METADATA, this.user);
        entityMetadata.write(Type.VAR_INT, this.mappedEntityId); // entity id
        entityMetadata.write(Types1_8.METADATA_LIST, this.getArmorStandMetadata()); // metadata
        entityMetadata.send(Protocol1_8to1_7_6_10.class);

        final PacketWrapper entityTeleport = PacketWrapper.create(ClientboundPackets1_8.ENTITY_TELEPORT, this.user);
        entityTeleport.write(Type.VAR_INT, this.mappedEntityId); // entity id
        entityTeleport.write(Type.INT, (int) (this.location.getX() * 32F)); // x
        entityTeleport.write(Type.INT, (int) ((this.location.getY() + this.getHeight()) * 32F)); // y
        entityTeleport.write(Type.INT, (int) (this.location.getZ() * 32F)); // z
        entityTeleport.write(Type.BYTE, (byte) 0); // yaw
        entityTeleport.write(Type.BYTE, (byte) 0); // pitch
        entityTeleport.write(Type.BOOLEAN, false); // onGround
        entityTeleport.send(Protocol1_8to1_7_6_10.class);
    }

    private boolean isHologram() {
        if (this.entityType != Entity1_10Types.EntityType.HORSE) return false;
        if (this.vehicleEntity == null) return false;
        if (this.riderEntity != null) return false;
        if (this.vehicleEntity.riderEntity != this) return false;
        if (this.vehicleEntity.vehicleEntity != null) return false;

        return ((int) this.getMetadata(MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE)) <= -44_000;
    }

    private boolean wouldBeInvisible() {
        if (this.entityType != Entity1_10Types.EntityType.HORSE) return false;

        final int age = (int) this.getMetadata(MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE);
        return age >= -50_000;
    }

    private float getHeight() {
        if (this.entityType == Entity1_10Types.EntityType.HORSE) {
            final int age = (int) this.getMetadata(MetaIndex1_8to1_7_6.ENTITY_AGEABLE_AGE);
            final float size = age >= 0 ? 1F : (0.5F + (-24_000F - age) / -24_000F * 0.5F);
            return HORSE_HEIGHT * size;
        } else {
            return WITHER_SKULL_HEIGHT;
        }
    }

    private void setPositionFromVehicle() {
        if (this.vehicleEntity != null) {
            this.location = new Location(this.vehicleEntity.location.getX(), this.vehicleEntity.location.getY() + this.vehicleEntity.getHeight() * 0.75F, this.vehicleEntity.location.getZ());
        }
        if (this.riderEntity != null) {
            this.riderEntity.setPositionFromVehicle();
        }
    }

    public int getEntityId() {
        return this.entityId;
    }

    public Entity1_10Types.EntityType getEntityType() {
        return this.entityType;
    }

    public void setVehicleEntity(final HologramPartEntity vehicleEntity) throws Exception {
        if (vehicleEntity == null) {
            if (this.vehicleEntity != null) {
                this.location = this.vehicleEntity.location;
                this.location = new Location(this.location.getX(), this.location.getY() + (this.vehicleEntity.entityType == Entity1_10Types.EntityType.HORSE ? HORSE_HEIGHT : WITHER_SKULL_HEIGHT), this.location.getZ());
                this.vehicleEntity.riderEntity = null;
                this.vehicleEntity.onChange();
            }

            this.vehicleEntity = null;
        } else {
            if (this.vehicleEntity != null) {
                this.vehicleEntity.riderEntity = null;
                this.vehicleEntity.onChange();
            }

            for (HologramPartEntity entity = vehicleEntity.vehicleEntity; entity != null; entity = entity.riderEntity) {
                if (entity == this) return;
            }

            this.vehicleEntity = vehicleEntity;
            vehicleEntity.riderEntity = this;
            vehicleEntity.onChange();
        }

        this.onChange();
    }

    public HologramPartEntity getVehicleEntity() {
        return this.vehicleEntity;
    }

    public HologramPartEntity getRiderEntity() {
        return this.riderEntity;
    }

    public void setLocation(final Location location) throws Exception {
        this.location = location;
        this.onChange();
    }

    public Location getLocation() {
        return this.location;
    }

    public void setMetadata(final MetaIndex1_8to1_7_6 index, final Object value) {
        this.metadata.put(index, value);
        // The onChange() method is called from the EntityTracker
    }

    public Object getMetadata(final MetaIndex1_8to1_7_6 index) {
        return this.metadata.get(index);
    }

    private List<Metadata> get1_8Metadata() {
        final List<Metadata> metadataList = new ArrayList<>();
        for (final Map.Entry<MetaIndex1_8to1_7_6, Object> entry : this.metadata.entrySet()) {
            metadataList.add(new Metadata(entry.getKey().getOldIndex(), entry.getKey().getOldType(), entry.getValue()));
        }
        this.user.getProtocolInfo().getPipeline().getProtocol(Protocol1_8to1_7_6_10.class).getMetadataRewriter().transform(this.entityType, metadataList);
        return metadataList;
    }

    private List<Metadata> getArmorStandMetadata() {
        final List<Metadata> metadataList = new ArrayList<>();
        if (this.entityType == Entity1_10Types.EntityType.HORSE) {
            metadataList.add(new Metadata(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG_VISIBILITY.getNewIndex(), MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG_VISIBILITY.getNewType(), this.getMetadata(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG_VISIBILITY)));
            metadataList.add(new Metadata(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG.getNewIndex(), MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG.getNewType(), this.getMetadata(MetaIndex1_8to1_7_6.ENTITY_LIVING_NAME_TAG)));
            metadataList.add(new Metadata(MetaIndex1_8to1_7_6.ENTITY_FLAGS.getNewIndex(), MetaIndex1_8to1_7_6.ENTITY_FLAGS.getNewType(), (byte) (1 << 5)));
            metadataList.add(new Metadata(MetaIndex1_8to1_7_6.ARMOR_STAND_FLAGS.getNewIndex(), MetaIndex1_8to1_7_6.ARMOR_STAND_FLAGS.getNewType(), (byte) (1 << 4)));
        }
        return metadataList;
    }
}
