package de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.api.rewriter.LegacyItemRewriter;
import de.florianmichael.viabeta.api.rewriter.LegacySoundRewriter;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.metadata.MetadataRewriter;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.rewriter.ItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.rewriter.SoundRewriter;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage.AttachTracker;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage.EntityTracker_1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_6_2to1_6_1.ClientboundPackets1_6_1;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.ServerboundPackets1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaIndex1_8to1_7_6;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.List;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_6_1to1_5_2 extends AbstractProtocol<ClientboundPackets1_5_2, ClientboundPackets1_6_1, ServerboundPackets1_5_2, ServerboundPackets1_6_4> {

    private final LegacyItemRewriter<Protocol1_6_1to1_5_2> itemRewriter = new ItemRewriter(this);
    private final LegacySoundRewriter<Protocol1_6_1to1_5_2> soundRewriter = new SoundRewriter(this);

    public Protocol1_6_1to1_5_2() {
        super(ClientboundPackets1_5_2.class, ClientboundPackets1_6_1.class, ServerboundPackets1_5_2.class, ServerboundPackets1_6_4.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();
        this.itemRewriter.register();
        this.soundRewriter.register();

        this.registerClientbound(ClientboundPackets1_5_2.JOIN_GAME, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // level type
                map(Type.BYTE); // game mode
                map(Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final EntityTracker_1_5_2 tracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    tracker.getTrackedEntities().put(entityId, Entity1_10Types.EntityType.PLAYER);
                    tracker.setPlayerID(entityId);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.UPDATE_HEALTH, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.SHORT, Type.FLOAT); // health
                map(Type.SHORT); // food
                map(Type.FLOAT); // saturation
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.SPAWN_PLAYER, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // name
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.UNSIGNED_SHORT); // item
                map(Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, wrapper.get(Type1_6_4.METADATA_LIST, 0)));
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().put(entityId, Entity1_10Types.EntityType.PLAYER);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.COLLECT_ITEM, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // collected entity id
                map(Type.INT); // collector entity id

                handler(wrapper -> wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().remove(wrapper.get(Type.INT, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.SPAWN_ENTITY, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // pitch
                map(Type.BYTE); // yaw
                map(Type.INT); // data
                // more conditional data
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.INT, 0);
                    final int typeID = wrapper.get(Type.BYTE, 0);
                    wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().put(entityID, Entity1_10Types.getTypeFromId(typeID, true));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.SPAWN_MOB, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.BYTE); // head yaw
                map(Type.SHORT); // velocity x
                map(Type.SHORT); // velocity y
                map(Type.SHORT); // velocity z
                map(Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.INT, 0);
                    final int typeID = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final Entity1_10Types.EntityType entityType = Entity1_10Types.getTypeFromId(typeID, false);
                    final List<Metadata> metadataList = wrapper.get(Type1_6_4.METADATA_LIST, 0);
                    wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().put(entityID, entityType);
                    MetadataRewriter.transform(entityType, metadataList);

                    if (entityType.isOrHasParent(Entity1_10Types.EntityType.WOLF)) {
                        handleWolfMetadata(entityID, metadataList, wrapper);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.SPAWN_PAINTING, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // motive
                map(Type1_7_6_10.POSITION_INT); // position
                map(Type.INT); // rotation
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.INT, 0);
                    wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().put(entityID, Entity1_10Types.EntityType.PAINTING);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.SPAWN_EXPERIENCE_ORB, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.SHORT); // count
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.INT, 0);
                    wrapper.user().get(EntityTracker_1_5_2.class).getTrackedEntities().put(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.DESTROY_ENTITIES, new PacketHandlers() {
            @Override
            public void register() {
                map(Type1_7_6_10.INT_ARRAY); // entity ids
                handler(wrapper -> {
                    final EntityTracker_1_5_2 tracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    for (int entityId : wrapper.get(Type1_7_6_10.INT_ARRAY, 0)) {
                        tracker.getTrackedEntities().remove(entityId);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.ATTACH_ENTITY, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // riding entity id
                map(Type.INT); // vehicle entity id
                handler(wrapper -> {
                    final AttachTracker attachTracker = wrapper.user().get(AttachTracker.class);
                    final EntityTracker_1_5_2 entityTracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    final int ridingId = wrapper.get(Type.INT, 0);
                    final int vehicleId = wrapper.get(Type.INT, 1);
                    if (entityTracker.getPlayerID() == ridingId) {
                        attachTracker.vehicleEntityId = vehicleId;
                    }
                });
                create(Type.UNSIGNED_BYTE, (short) 0); // leash state
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.ENTITY_METADATA, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type1_6_4.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final EntityTracker_1_5_2 tracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    final List<Metadata> metadataList = wrapper.get(Type1_6_4.METADATA_LIST, 0);
                    final int entityID = wrapper.get(Type.INT, 0);
                    final Entity1_10Types.EntityType entityType = tracker.getTrackedEntities().get(entityID);
                    if (tracker.getTrackedEntities().containsKey(entityID)) {
                        MetadataRewriter.transform(entityType, metadataList);
                        if (metadataList.isEmpty()) wrapper.cancel();

                        if (entityType.isOrHasParent(Entity1_10Types.EntityType.WOLF)) {
                            handleWolfMetadata(entityID, metadataList, wrapper);
                        }
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.STATISTICS, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // statistic id
                map(Type.BYTE, Type.INT); // increment
            }
        });
        this.registerClientbound(ClientboundPackets1_5_2.PLAYER_ABILITIES, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE); // flags
                map(Type.BYTE, Type.FLOAT, b -> b / 255F); // fly speed
                map(Type.BYTE, Type.FLOAT, b -> b / 255F); // walk speed
            }
        });

        this.registerServerbound(ServerboundPackets1_6_4.ENTITY_ACTION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT); // entity id
                map(Type.BYTE); // action id
                read(Type.INT); // action parameter
                handler(wrapper -> {
                    if (wrapper.get(Type.BYTE, 0) > 5) wrapper.cancel();
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_6_4.STEER_VEHICLE, ServerboundPackets1_5_2.INTERACT_ENTITY, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final AttachTracker attachTracker = wrapper.user().get(AttachTracker.class);
                    final EntityTracker_1_5_2 entityTracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    wrapper.read(Type.FLOAT); // sideways
                    wrapper.read(Type.FLOAT); // forwards
                    wrapper.read(Type.BOOLEAN); // jumping
                    final boolean sneaking = wrapper.read(Type.BOOLEAN); // sneaking

                    if (attachTracker.lastSneakState != sneaking) {
                        attachTracker.lastSneakState = sneaking;
                        if (sneaking) {
                            wrapper.write(Type.INT, entityTracker.getPlayerID()); // player id
                            wrapper.write(Type.INT, attachTracker.vehicleEntityId); // entity id
                            wrapper.write(Type.BYTE, (byte) 0); // mode
                            return;
                        }
                    }
                    wrapper.cancel();
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_6_4.PLAYER_ABILITIES, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE); // flags
                map(Type.FLOAT, Type.BYTE, f -> (byte) (f * 255F)); // fly speed
                map(Type.FLOAT, Type.BYTE, f -> (byte) (f * 255F)); // walk speed
            }
        });
    }

    private void handleWolfMetadata(final int entityId, final List<Metadata> metadataList, final PacketWrapper wrapper) throws Exception {
        for (Metadata metadata : metadataList) {
            final MetaIndex1_8to1_7_6 index = MetaIndex1_8to1_7_6.searchIndex(Entity1_10Types.EntityType.WOLF, metadata.id());

            if (index == MetaIndex1_8to1_7_6.TAMEABLE_FLAGS) {
                if ((metadata.<Byte>value() & 4) != 0) { // is tamed
                    final PacketWrapper attributes = PacketWrapper.create(ClientboundPackets1_6_1.ENTITY_PROPERTIES, wrapper.user());
                    attributes.write(Type.INT, entityId); // entity id
                    attributes.write(Type.INT, 1); // count
                    attributes.write(Type1_6_4.STRING, "generic.maxHealth"); // id
                    attributes.write(Type.DOUBLE, 20.0D); // value

                    wrapper.send(Protocol1_6_1to1_5_2.class);
                    attributes.send(Protocol1_6_1to1_5_2.class);
                    wrapper.cancel();
                }
                break;
            }
        }
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_6_1to1_5_2.class, ClientboundPackets1_5_2::getPacket));

        userConnection.put(new EntityTracker_1_5_2(userConnection));
        userConnection.put(new AttachTracker(userConnection));
    }

    @Override
    public LegacyItemRewriter<Protocol1_6_1to1_5_2> getItemRewriter() {
        return this.itemRewriter;
    }
}
