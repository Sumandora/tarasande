package de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.*;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.api.model.Location;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.data.EntityList_1_2_5;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.TrackedEntity;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.TrackedLivingEntity;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.model.base.AbstractTrackedEntity;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.provider.OldAuthProvider;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound.Sound;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.sound.SoundType;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.ChestStateTracker;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.DimensionTracker_1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.storage.EntityTracker_1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.task.EntityTrackerTickTask;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.Type1_2_4_5;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.impl.Chunk_1_2_4_5Type;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.ClientboundPackets1_3_1;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.ServerboundPackets1_3_1;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.metadata.MetaIndex1_6_1to1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaIndex1_8to1_7_6;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.Chunk_1_7_6_10Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_3_1_2to1_2_4_5 extends AbstractProtocol<ClientboundPackets1_2_4, ClientboundPackets1_3_1, ServerboundPackets1_2_4, ServerboundPackets1_3_1> {

    public Protocol1_3_1_2to1_2_4_5() {
        super(ClientboundPackets1_2_4.class, ClientboundPackets1_3_1.class, ServerboundPackets1_2_4.class, ServerboundPackets1_3_1.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.LOGIN, ClientboundPackets1_2_4.HANDSHAKE.getId(), ClientboundPackets1_3_1.SHARED_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    handleHandshake(wrapper);
                    wrapper.write(Type.SHORT_BYTE_ARRAY, new byte[0]);
                    wrapper.write(Type.SHORT_BYTE_ARRAY, new byte[0]);

                    wrapper.user().get(ProtocolMetadataStorage.class).skipEncryption(true);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.HANDSHAKE, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    handleHandshake(wrapper); // Very hacky but some servers expect the client to send back a Packet1Login
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                read(Type1_6_4.STRING); // username
                map(Type1_6_4.STRING); // level type
                map(Type.INT, Type.BYTE); // game mode
                map(Type.INT, Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
                handler(wrapper -> {
                    wrapper.user().get(ClientWorld.class).setEnvironment(wrapper.get(Type.BYTE, 1));
                    wrapper.user().get(DimensionTracker_1_2_4_5.class).setDimension(wrapper.get(Type.BYTE, 1));
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    entityTracker.setPlayerID(wrapper.get(Type.INT, 0));
                    entityTracker.getTrackedEntities().put(entityTracker.getPlayerID(), new TrackedLivingEntity(entityTracker.getPlayerID(), new Location(8, 64, 8), Entity1_10Types.EntityType.PLAYER));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.SHORT); // slot
                handler(wrapper -> {
                    final int itemId = wrapper.read(Type.SHORT); // item id
                    final short itemDamage = wrapper.read(Type.SHORT); // item damage
                    wrapper.write(Type1_7_6_10.COMPRESSED_ITEM, itemId < 0 ? null : new DataItem(itemId, (byte) 1, itemDamage, null));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                map(Type1_6_4.STRING); // level type
                handler(wrapper -> {
                    final int oldDim = wrapper.user().get(DimensionTracker_1_2_4_5.class).getDimensionId();
                    final int newDim = wrapper.get(Type.INT, 0);
                    wrapper.user().get(ClientWorld.class).setEnvironment(newDim);
                    wrapper.user().get(DimensionTracker_1_2_4_5.class).setDimension(newDim);
                    if (oldDim != newDim) {
                        wrapper.user().get(ChestStateTracker.class).clear();
                        final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                        entityTracker.getTrackedEntities().clear();
                        entityTracker.getTrackedEntities().put(entityTracker.getPlayerID(), new TrackedLivingEntity(entityTracker.getPlayerID(), new Location(8, 64, 8), Entity1_10Types.EntityType.PLAYER));
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // name
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.UNSIGNED_SHORT); // item
                create(Type1_3_1_2.METADATA_LIST, new ArrayList<>()); // metadata
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final double x = wrapper.get(Type.INT, 1) / 32.0D;
                    final double y = wrapper.get(Type.INT, 2) / 32.0D;
                    final double z = wrapper.get(Type.INT, 3) / 32.0D;
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    tracker.getTrackedEntities().put(entityId, new TrackedLivingEntity(entityId, new Location(x, y, z), Entity1_10Types.EntityType.PLAYER));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.SPAWN_ITEM, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_3_1_2.NBTLESS_ITEM); // item
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // velocity x
                map(Type.BYTE); // velocity y
                map(Type.BYTE); // velocity z
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final double x = wrapper.get(Type.INT, 1) / 32.0D;
                    final double y = wrapper.get(Type.INT, 2) / 32.0D;
                    final double z = wrapper.get(Type.INT, 3) / 32.0D;
                    tracker.getTrackedEntities().put(entityId, new TrackedEntity(entityId, new Location(x, y, z), Entity1_10Types.ObjectType.ITEM.getType()));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.COLLECT_ITEM, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // collected entity id
                map(Type.INT); // collector entity id

                handler(wrapper -> wrapper.user().get(EntityTracker_1_2_4_5.class).getTrackedEntities().remove(wrapper.get(Type.INT, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.INT); // data
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte typeId = wrapper.get(Type.BYTE, 0);
                    final Entity1_10Types.EntityType type;
                    if (typeId == 70 || typeId == 71 || typeId == 74) {
                        type = Entity1_10Types.ObjectType.FALLING_BLOCK.getType();
                        wrapper.set(Type.BYTE, 0, (byte) Entity1_10Types.ObjectType.FALLING_BLOCK.getId());
                    } else if (typeId == 10 || typeId == 11 || typeId == 12) {
                        type = Entity1_10Types.ObjectType.MINECART.getType();
                    } else {
                        type = Entity1_10Types.getTypeFromId(typeId, true);
                    }
                    final double x = wrapper.get(Type.INT, 1) / 32.0D;
                    final double y = wrapper.get(Type.INT, 2) / 32.0D;
                    final double z = wrapper.get(Type.INT, 3) / 32.0D;
                    final Location location = new Location(x, y, z);
                    int throwerEntityId = wrapper.get(Type.INT, 4);
                    short speedX = 0;
                    short speedY = 0;
                    short speedZ = 0;
                    if (throwerEntityId > 0) {
                        speedX = wrapper.read(Type.SHORT); // velocity x
                        speedY = wrapper.read(Type.SHORT); // velocity y
                        speedZ = wrapper.read(Type.SHORT); // velocity z
                    }
                    if (typeId == 70) throwerEntityId = 12; // sand
                    if (typeId == 71) throwerEntityId = 13; // gravel
                    if (typeId == 74) throwerEntityId = 122; // dragon egg
                    if (typeId == Entity1_10Types.ObjectType.FISHIHNG_HOOK.getId()) {
                        final Optional<AbstractTrackedEntity> nearestEntity = entityTracker.getNearestEntity(location, 2.0D, e -> e.getEntityType().isOrHasParent(Entity1_10Types.EntityType.PLAYER));
                        throwerEntityId = nearestEntity.map(AbstractTrackedEntity::getEntityId).orElseGet(entityTracker::getPlayerID);
                    }
                    wrapper.set(Type.INT, 4, throwerEntityId);
                    if (throwerEntityId > 0) {
                        wrapper.write(Type.SHORT, speedX);
                        wrapper.write(Type.SHORT, speedY);
                        wrapper.write(Type.SHORT, speedZ);
                    }

                    entityTracker.getTrackedEntities().put(entityId, new TrackedEntity(entityId, location, type));
                    final Entity1_10Types.ObjectType objectType = Entity1_10Types.ObjectType.findById(typeId).orElse(null);
                    if (objectType == null) return;

                    float pitch;
                    switch (objectType) {
                        case TNT_PRIMED -> entityTracker.playSoundAt(location, Sound.RANDOM_FUSE, 1.0F, 1.0F);
                        case TIPPED_ARROW -> {
                            pitch = 1.0F / (entityTracker.RND.nextFloat() * 0.4F + 1.2F) + 0.5F;
                            entityTracker.playSoundAt(location, Sound.RANDOM_BOW, 1.0F, pitch);
                        }
                        case SNOWBALL, EGG, ENDER_PEARL, ENDER_SIGNAL, POTION, THROWN_EXP_BOTTLE, FISHIHNG_HOOK -> {
                            pitch = 0.4F / (entityTracker.RND.nextFloat() * 0.4F + 0.8F);
                            entityTracker.playSoundAt(location, Sound.RANDOM_BOW, 0.5F, pitch);
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.BYTE); // head yaw
                create(Type.SHORT, (short) 0); // velocity x
                create(Type.SHORT, (short) 0); // velocity y
                create(Type.SHORT, (short) 0); // velocity z
                map(Type1_3_1_2.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final short type = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final double x = wrapper.get(Type.INT, 1) / 32.0D;
                    final double y = wrapper.get(Type.INT, 2) / 32.0D;
                    final double z = wrapper.get(Type.INT, 3) / 32.0D;
                    final List<Metadata> metadataList = wrapper.get(Type1_3_1_2.METADATA_LIST, 0);
                    final Entity1_10Types.EntityType entityType = Entity1_10Types.getTypeFromId(type, false);
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    tracker.getTrackedEntities().put(entityId, new TrackedLivingEntity(entityId, new Location(x, y, z), entityType));
                    tracker.updateEntityMetadata(entityId, metadataList);
                    handleEntityMetadata(entityId, metadataList, wrapper);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.DESTROY_ENTITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type1_7_6_10.INT_ARRAY, i -> new int[]{i});
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    for (int entityId : wrapper.get(Type1_7_6_10.INT_ARRAY, 0)) {
                        tracker.getTrackedEntities().remove(entityId);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte x = wrapper.get(Type.BYTE, 0);
                    final byte y = wrapper.get(Type.BYTE, 1);
                    final byte z = wrapper.get(Type.BYTE, 2);
                    tracker.updateEntityLocation(entityId, x, y, z, true);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte x = wrapper.get(Type.BYTE, 0);
                    final byte y = wrapper.get(Type.BYTE, 1);
                    final byte z = wrapper.get(Type.BYTE, 2);
                    tracker.updateEntityLocation(entityId, x, y, z, true);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_TELEPORT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final int x = wrapper.get(Type.INT, 1);
                    final int y = wrapper.get(Type.INT, 2);
                    final int z = wrapper.get(Type.INT, 3);
                    tracker.updateEntityLocation(entityId, x, y, z, false);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_STATUS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // status
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte status = wrapper.get(Type.BYTE, 0);

                    if (status == 2) { // hurt
                        entityTracker.playSound(entityId, SoundType.HURT);
                    } else if (status == 3) { // death
                        entityTracker.playSound(entityId, SoundType.DEATH);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_3_1_2.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final List<Metadata> metadataList = wrapper.get(Type1_3_1_2.METADATA_LIST, 0);

                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    if (entityTracker.getTrackedEntities().containsKey(entityId)) {
                        entityTracker.updateEntityMetadata(entityId, metadataList);
                        handleEntityMetadata(entityId, metadataList, wrapper);
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.PRE_CHUNK, ClientboundPackets1_3_1.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final int chunkX = wrapper.read(Type.INT); // x
                    final int chunkZ = wrapper.read(Type.INT); // z
                    final short mode = wrapper.read(Type.UNSIGNED_BYTE); // mode
                    final boolean load = mode != 0;

                    wrapper.user().get(ChestStateTracker.class).unload(chunkX, chunkZ);

                    if (!load) {
                        final Chunk chunk = new BaseChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], null, new ArrayList<>());
                        wrapper.write(new Chunk_1_7_6_10Type(wrapper.user().get(ClientWorld.class)), chunk);
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    Chunk chunk = wrapper.read(new Chunk_1_2_4_5Type(clientWorld));

                    wrapper.user().get(ChestStateTracker.class).unload(chunk.getX(), chunk.getZ());

                    if (chunk.isFullChunk() && chunk.getBitmask() == 0) { // Remap to empty chunk
                        ViaBeta.getPlatform().getLogger().warning("Received empty 1.2.5 chunk packet");
                        chunk = new BaseChunk(chunk.getX(), chunk.getZ(), true, false, 65535, new ChunkSection[16], new int[256], new ArrayList<>());
                        for (int i = 0; i < chunk.getSections().length; i++) {
                            final ChunkSection chunkSection = chunk.getSections()[i] = new ChunkSectionImpl(true);
                            chunkSection.palette(PaletteType.BLOCKS).addId(0);
                            if (clientWorld.getEnvironment() == Environment.NORMAL) {
                                final byte[] skyLight = new byte[2048];
                                Arrays.fill(skyLight, (byte) 255);
                                chunkSection.getLight().setSkyLight(skyLight);
                            }
                        }
                    }

                    wrapper.write(new Chunk_1_7_6_10Type(clientWorld), chunk);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE, Type.UNSIGNED_SHORT); // block id
                map(Type.UNSIGNED_BYTE); // block data
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.BLOCK_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.BYTE); // type
                map(Type.BYTE); // data
                handler(wrapper -> {
                    final IdAndData block = wrapper.user().get(ChunkTracker.class).getBlockNotNull(wrapper.get(Type1_7_6_10.POSITION_SHORT, 0));
                    wrapper.write(Type.SHORT, (short) block.id); // block id
                });
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_SHORT, 0);
                    final byte type = wrapper.get(Type.BYTE, 0);
                    final short data = wrapper.get(Type.BYTE, 1);
                    final short blockId = wrapper.get(Type.SHORT, 0);
                    if (blockId <= 0) return;

                    float volume = 1F;
                    float pitch = 1F;
                    Sound sound = null;

                    if (blockId == BlockList1_6.music.blockID) {
                        sound = switch (type) {
                            case 0 -> Sound.NOTE_HARP;
                            case 1 -> Sound.NOTE_CLICK;
                            case 2 -> Sound.NOTE_SNARE;
                            case 3 -> Sound.NOTE_HAT;
                            case 4 -> Sound.NOTE_BASS_ATTACK;
                            default -> throw new IllegalStateException("Unexpected value: " + type);
                        };
                        volume = 3F;
                        pitch = (float) Math.pow(2D, (double) (data - 12) / 12D);
                    } else if (blockId == BlockList1_6.chest.blockID) {
                        if (type == 1) {
                            final ChestStateTracker chestStateTracker = wrapper.user().get(ChestStateTracker.class);
                            if (chestStateTracker.isChestOpen(pos) && data <= 0) {
                                sound = Sound.CHEST_CLOSE;
                                chestStateTracker.closeChest(pos);
                            } else if (!chestStateTracker.isChestOpen(pos) && data > 0) {
                                sound = Sound.CHEST_OPEN;
                                chestStateTracker.openChest(pos);
                            }
                            volume = 0.5F;
                            pitch = entityTracker.RND.nextFloat() * 0.1F + 0.9F;
                        }
                    } else if (blockId == BlockList1_6.pistonBase.blockID || blockId == BlockList1_6.pistonStickyBase.blockID) {
                        if (type == 0) {
                            sound = Sound.PISTON_OUT;
                            volume = 0.5F;
                            pitch = entityTracker.RND.nextFloat() * 0.25F + 0.6F;
                        } else if (type == 1) {
                            sound = Sound.PISTON_IN;
                            volume = 0.5F;
                            pitch = entityTracker.RND.nextFloat() * 0.15F + 0.6F;
                        }
                    }

                    if (sound != null) {
                        entityTracker.playSoundAt(new Location(pos), sound, volume, pitch);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.EXPLOSION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // radius
                map(Type.INT); // record count
                handler(wrapper -> {
                    final int count = wrapper.get(Type.INT, 0);
                    for (int i = 0; i < count * 3; i++) wrapper.passthrough(Type.BYTE);
                });
                create(Type.FLOAT, 0F); // velocity x
                create(Type.FLOAT, 0F); // velocity y
                create(Type.FLOAT, 0F); // velocity z
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final Location loc = new Location(wrapper.get(Type.DOUBLE, 0), wrapper.get(Type.DOUBLE, 1), wrapper.get(Type.DOUBLE, 2));
                    entityTracker.playSoundAt(loc, Sound.RANDOM_EXPLODE, 4F, (1.0F + (entityTracker.RND.nextFloat() - entityTracker.RND.nextFloat()) * 0.2F) * 0.7F);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type1_2_4_5.COMPRESSED_NBT_ITEM, Type1_7_6_10.COMPRESSED_ITEM); // item
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type1_2_4_5.COMPRESSED_NBT_ITEM_ARRAY, Type1_7_6_10.COMPRESSED_ITEM_ARRAY); // items
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.BYTE); // type
                handler(wrapper -> {
                    final int entityId = wrapper.read(Type.INT); // entity id
                    wrapper.read(Type.INT); // unused
                    wrapper.read(Type.INT); // unused
                    if (wrapper.get(Type.BYTE, 0) != 1) { // spawner
                        wrapper.cancel();
                        return;
                    }
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_SHORT, 0);

                    final CompoundTag tag = new CompoundTag();
                    tag.put("EntityId", new StringTag(EntityList_1_2_5.getEntityName(entityId)));
                    tag.put("Delay", new ShortTag((short) 20));
                    tag.put("x", new IntTag(pos.x()));
                    tag.put("y", new IntTag(pos.y()));
                    tag.put("z", new IntTag(pos.z()));
                    wrapper.write(Type1_7_6_10.COMPRESSED_NBT, tag);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_2_4.PLAYER_ABILITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final boolean disableDamage = wrapper.read(Type.BOOLEAN); // invulnerable
                    final boolean flying = wrapper.read(Type.BOOLEAN); // flying
                    final boolean allowFlying = wrapper.read(Type.BOOLEAN); // allow flying
                    final boolean creativeMode = wrapper.read(Type.BOOLEAN); // creative mode

                    byte mask = 0;
                    if (disableDamage) mask |= 1;
                    if (flying) mask |= 2;
                    if (allowFlying) mask |= 4;
                    if (creativeMode) mask |= 8;

                    wrapper.write(Type.BYTE, mask); // flags
                    wrapper.write(Type.BYTE, (byte) (0.05f * 255)); // fly speed
                    wrapper.write(Type.BYTE, (byte) (0.1f * 255)); // walk speed
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPackets1_2_4.HANDSHAKE.getId(), ServerboundPackets1_3_1.CLIENT_PROTOCOL.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.read(Type.UNSIGNED_BYTE); // protocol id
                    final String userName = wrapper.read(Type1_6_4.STRING); // user name
                    final String hostname = wrapper.read(Type1_6_4.STRING); // hostname
                    final int port = wrapper.read(Type.INT); // port
                    wrapper.write(Type1_6_4.STRING, userName + ";" + hostname + ":" + port); // info
                });
            }
        });
        this.cancelServerbound(ServerboundPackets1_3_1.CLIENT_PROTOCOL);
        this.cancelServerbound(ServerboundPackets1_3_1.SHARED_KEY);
        this.registerServerbound(ServerboundPackets1_3_1.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                map(Type.DOUBLE); // stance
                map(Type.DOUBLE); // z
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final AbstractTrackedEntity player = entityTracker.getTrackedEntities().get(entityTracker.getPlayerID());
                    if (wrapper.get(Type.DOUBLE, 1) == -999D && wrapper.get(Type.DOUBLE, 2) == -999D) {
                        player.setRiding(true);
                    } else {
                        player.setRiding(false);
                        player.getLocation().setX(wrapper.get(Type.DOUBLE, 0));
                        player.getLocation().setY(wrapper.get(Type.DOUBLE, 1));
                        player.getLocation().setZ(wrapper.get(Type.DOUBLE, 3));
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                map(Type.DOUBLE); // stance
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final EntityTracker_1_2_4_5 entityTracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
                    final AbstractTrackedEntity player = entityTracker.getTrackedEntities().get(entityTracker.getPlayerID());
                    if (wrapper.get(Type.DOUBLE, 1) == -999D && wrapper.get(Type.DOUBLE, 2) == -999D) {
                        player.setRiding(true);
                    } else {
                        player.setRiding(false);
                        player.getLocation().setX(wrapper.get(Type.DOUBLE, 0));
                        player.getLocation().setY(wrapper.get(Type.DOUBLE, 1));
                        player.getLocation().setZ(wrapper.get(Type.DOUBLE, 3));
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_7_6_10.COMPRESSED_ITEM, Type1_2_4_5.COMPRESSED_NBT_ITEM); // item
                read(Type.UNSIGNED_BYTE); // offset x
                read(Type.UNSIGNED_BYTE); // offset y
                read(Type.UNSIGNED_BYTE); // offset z
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                map(Type.BYTE); // mode
                map(Type1_7_6_10.COMPRESSED_ITEM, Type1_2_4_5.COMPRESSED_NBT_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM, Type1_2_4_5.COMPRESSED_NBT_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.PLAYER_ABILITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final byte mask = wrapper.read(Type.BYTE); // flags
                    wrapper.read(Type.BYTE); // fly speed
                    wrapper.read(Type.BYTE); // walk speed

                    final boolean disableDamage = ((mask & 1) > 0);
                    final boolean flying = ((mask & 2) > 0);
                    final boolean allowFlying = ((mask & 4) > 0);
                    final boolean creativeMode = ((mask & 8) > 0);

                    wrapper.write(Type.BOOLEAN, disableDamage); // invulnerable
                    wrapper.write(Type.BOOLEAN, flying); // flying
                    wrapper.write(Type.BOOLEAN, allowFlying); // allow flying
                    wrapper.write(Type.BOOLEAN, creativeMode); // creative mode
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_3_1.CLIENT_STATUS, ServerboundPackets1_2_4.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final byte action = wrapper.read(Type.BYTE); // force respawn
                    if (action != 1) {
                        wrapper.cancel();
                    }
                    wrapper.write(Type.INT, 0); // dimension id
                    wrapper.write(Type.BYTE, (byte) 0); // difficulty
                    wrapper.write(Type.BYTE, (byte) 0); // game mode
                    wrapper.write(Type.SHORT, (short) 0); // world height
                    wrapper.write(Type1_6_4.STRING, ""); // level type
                });
            }
        });
        this.cancelServerbound(ServerboundPackets1_3_1.TAB_COMPLETE);
        this.cancelServerbound(ServerboundPackets1_3_1.CLIENT_SETTINGS);
    }

    private void handleEntityMetadata(final int entityId, final List<Metadata> metadataList, final PacketWrapper wrapper) throws Exception {
        final EntityTracker_1_2_4_5 tracker = wrapper.user().get(EntityTracker_1_2_4_5.class);
        if (entityId == tracker.getPlayerID()) return;

        final AbstractTrackedEntity entity = tracker.getTrackedEntities().get(entityId);
        for (Metadata metadata : metadataList) {
            if (MetaIndex1_6_1to1_5_2.searchIndex(entity.getEntityType(), metadata.id()) != null) continue;
            final MetaIndex1_8to1_7_6 index = MetaIndex1_8to1_7_6.searchIndex(entity.getEntityType(), metadata.id());

            if (index == MetaIndex1_8to1_7_6.ENTITY_FLAGS) {
                if ((metadata.<Byte>value() & 4) != 0) { // entity mount
                    final Optional<AbstractTrackedEntity> oNearbyEntity = tracker.getNearestEntity(entity.getLocation(), 1.0D, e -> e.getEntityType().isOrHasParent(Entity1_10Types.EntityType.MINECART_RIDEABLE) || e.getEntityType().isOrHasParent(Entity1_10Types.EntityType.PIG) || e.getEntityType().isOrHasParent(Entity1_10Types.EntityType.BOAT));

                    if (oNearbyEntity.isPresent()) {
                        entity.setRiding(true);
                        final AbstractTrackedEntity nearbyEntity = oNearbyEntity.get();

                        final PacketWrapper attachEntity = PacketWrapper.create(ClientboundPackets1_3_1.ATTACH_ENTITY, wrapper.user());
                        attachEntity.write(Type.INT, entityId); // riding entity id
                        attachEntity.write(Type.INT, nearbyEntity.getEntityId()); // vehicle entity id

                        wrapper.send(Protocol1_3_1_2to1_2_4_5.class);
                        attachEntity.send(Protocol1_3_1_2to1_2_4_5.class);
                        wrapper.cancel();
                    }
                } else if ((metadata.<Byte>value() & 4) == 0 && entity.isRiding()) { // entity unmount
                    entity.setRiding(false);

                    final PacketWrapper detachEntity = PacketWrapper.create(ClientboundPackets1_3_1.ATTACH_ENTITY, wrapper.user());
                    detachEntity.write(Type.INT, entityId); // riding entity id
                    detachEntity.write(Type.INT, -1); // vehicle entity id

                    detachEntity.send(Protocol1_3_1_2to1_2_4_5.class);
                    wrapper.send(Protocol1_3_1_2to1_2_4_5.class);
                    wrapper.cancel();
                }
                break;
            } else if (index == MetaIndex1_8to1_7_6.CREEPER_STATE) {
                if(metadata.<Byte>value() > 0) {
                    tracker.playSoundAt(entity.getLocation(), Sound.RANDOM_FUSE, 1.0F, 0.5F);
                }
            }
        }
    }

    private void handleHandshake(final PacketWrapper wrapper) throws Exception {
        final ProtocolInfo info = wrapper.user().getProtocolInfo();
        final String serverHash = wrapper.read(Type1_6_4.STRING); // server hash
        if (!serverHash.trim().isEmpty() && !serverHash.equalsIgnoreCase("-")) {
            try {
                Via.getManager().getProviders().get(OldAuthProvider.class).sendAuthRequest(wrapper.user(), serverHash);
            } catch (Throwable e) {
                ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not authenticate with mojang for joinserver request!", e);
                wrapper.cancel();
                final PacketWrapper kick = PacketWrapper.create(ClientboundPackets1_3_1.DISCONNECT, wrapper.user());
                kick.write(Type1_6_4.STRING, "Failed to log in: Invalid session (Try restarting your game and the launcher)"); // reason
                kick.send(Protocol1_3_1_2to1_2_4_5.class);
                return;
            }
        }

        final PacketWrapper login = PacketWrapper.create(ServerboundPackets1_2_4.LOGIN, wrapper.user());
        login.write(Type.INT, -(info.getServerProtocolVersion() >> 2)); // protocol id
        login.write(Type1_6_4.STRING, info.getUsername()); // username
        login.write(Type1_6_4.STRING, ""); // level type
        login.write(Type.INT, 0); // game mode
        login.write(Type.INT, 0); // dimension id
        login.write(Type.BYTE, (byte) 0); // difficulty
        login.write(Type.BYTE, (byte) 0); // world height
        login.write(Type.BYTE, (byte) 0); // max players

        final State oldState = info.getState();
        info.setState(State.LOGIN);
        login.sendToServer(Protocol1_3_1_2to1_2_4_5.class);
        info.setState(oldState);
    }

    @Override
    public void register(ViaProviders providers) {
        providers.register(OldAuthProvider.class, new OldAuthProvider());

        if (ViaBeta.getConfig().isSoundEmulation()) {
            Via.getPlatform().runRepeatingSync(new EntityTrackerTickTask(), 1L);
        }
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_3_1_2to1_2_4_5.class, ClientboundPackets1_2_4::getPacket));

        userConnection.put(new ChestStateTracker(userConnection));
        userConnection.put(new EntityTracker_1_2_4_5(userConnection));
        userConnection.put(new DimensionTracker_1_2_4_5(userConnection));

        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
    }
}
