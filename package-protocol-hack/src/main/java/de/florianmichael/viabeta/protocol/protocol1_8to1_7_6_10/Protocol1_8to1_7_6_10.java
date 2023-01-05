package de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10;

import com.google.common.base.Joiner;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.Chunk1_8Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.ChunkBulk1_8Type;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.ClientboundPackets1_7_2;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.ServerboundPackets1_7_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetadataRewriter;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.GameProfile;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.TabListEntry;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.map.MapData;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.model.map.MapIcon;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter.ChatItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter.ItemRewriter;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.rewriter.TranslationRewriter;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.storage.*;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ChunkBulk_1_7_6_10Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.Chunk_1_7_6_10Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.util.Particle_1_7_6_10;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_8to1_7_6_10 extends AbstractProtocol<ClientboundPackets1_7_2, ClientboundPackets1_8, ServerboundPackets1_7_2, ServerboundPackets1_8> {

    public static final ItemRewriter ITEM_REWRITER = new ItemRewriter();

    public Protocol1_8to1_7_6_10() {
        super(ClientboundPackets1_7_2.class, ClientboundPackets1_8.class, ServerboundPackets1_7_2.class, ServerboundPackets1_8.class);
    }

    public static final ValueTransformer<String, String> LEGACY_TO_JSON = new ValueTransformer<>(Type.STRING, Type.STRING) {
        @Override
        public String transform(PacketWrapper packetWrapper, String message) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", message);
            return jsonObject.toString();
        }
    };

    public static final ValueTransformer<String, String> LEGACY_TO_JSON_TRANSLATE = new ValueTransformer<>(Type.STRING, Type.STRING) {
        @Override
        public String transform(PacketWrapper packetWrapper, String message) {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("translate", message);
            return jsonObject.toString();
        }
    };

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // server hash
                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE); // public key
                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE); // verify token
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.KEEP_ALIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // key
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // game mode
                map(Type.BYTE); // dimension id
                map(Type.UNSIGNED_BYTE); // difficulty
                map(Type.UNSIGNED_BYTE); // max players
                map(Type.STRING); // level type
                create(Type.BOOLEAN, false); // reduced debug info
                handler(wrapper -> {
                    final ProtocolInfo protocolInfo = wrapper.user().getProtocolInfo();

                    final TabListStorage tabListStorage = wrapper.user().get(TabListStorage.class);
                    if (tabListStorage != null) {
                        tabListStorage.sendTempEntry(new TabListEntry(protocolInfo.getUsername(), protocolInfo.getUuid())); // load own skin
                    }
                });
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte dimensionId = wrapper.get(Type.BYTE, 0);

                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    if (entityTracker != null) {
                        entityTracker.trackEntity(entityId, Entity1_10Types.EntityType.PLAYER);
                        entityTracker.setPlayerID(entityId);
                    }

                    updateDimension(wrapper.user(), dimensionId);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, Type.STRING, msg -> TranslationRewriter.toClient(ChatItemRewriter.toClient(msg))); // message
                create(Type.BYTE, (byte) 0); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM, Type.ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteRead(wrapper.get(Type.ITEM, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_INT, Type.POSITION); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.UPDATE_HEALTH, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // health
                map(Type.SHORT, Type.VAR_INT); // food
                map(Type.FLOAT); // saturation
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // dimension id
                map(Type.UNSIGNED_BYTE); // difficulty
                map(Type.UNSIGNED_BYTE); // game mode
                map(Type.STRING); // worldType
                handler(wrapper -> {
                    final int oldDimension = wrapper.user().get(DimensionTracker_1_7_6_10.class).getDimensionId();
                    final int newDimension = wrapper.get(Type.INT, 0);
                    updateDimension(wrapper.user(), newDimension);

                    if (oldDimension != newDimension) {
                        wrapper.user().get(ChunkTracker_1_7_6_10.class).clear();
                        wrapper.user().get(EntityTracker_1_7_6_10.class).clear();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE, Type.DOUBLE, stance -> stance - 1.62F); // y
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                handler(wrapper -> {
                    final boolean onGround = wrapper.read(Type.BOOLEAN); // On Ground
                    final TeleportTracker teleportTracker = wrapper.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        teleportTracker.setPending(onGround);
                    }

                    wrapper.write(Type.BYTE, (byte) 0); // flags
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.USE_BED, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type1_7_6_10.POSITION_BYTE, Type.POSITION); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT); // entity id
                    final UUID uuid = UUID.fromString(wrapper.read(Type.STRING)); // uuid
                    wrapper.write(Type.UUID, uuid);
                    final String name = wrapper.read(Type.STRING); // name

                    final TabListStorage tabListStorage = wrapper.user().get(TabListStorage.class);
                    final TabListEntry tempTabEntry = new TabListEntry(name, uuid);

                    final int dataCount = wrapper.read(Type.VAR_INT); // properties count
                    for (int i = 0; i < dataCount; i++) {
                        final String key = wrapper.read(Type.STRING); // name
                        final String value = wrapper.read(Type.STRING); // value
                        final String signature = wrapper.read(Type.STRING); // signature

                        tempTabEntry.gameProfile.addProperty(new GameProfile.Property(key, value, signature));
                    }

                    wrapper.passthrough(Type.INT); // x
                    wrapper.passthrough(Type.INT); // y
                    wrapper.passthrough(Type.INT); // z
                    wrapper.passthrough(Type.BYTE); // yaw
                    wrapper.passthrough(Type.BYTE); // pitch

                    final short itemId = wrapper.read(Type.SHORT); // item in hand
                    final Item currentItem = new DataItem(itemId, (byte) 1, (short) 0, null);
                    ITEM_REWRITER.rewriteRead(currentItem);
                    wrapper.write(Type.SHORT, (short) currentItem.identifier());

                    final List<Metadata> metadata = wrapper.read(Type1_7_6_10.METADATA_LIST); // metadata
                    MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadata);
                    wrapper.write(Types1_8.METADATA_LIST, metadata);

                    tabListStorage.sendTempEntry(tempTabEntry);
                });
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.VAR_INT, 0);

                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    if (entityTracker != null) {
                        entityTracker.trackEntity(entityID, Entity1_10Types.EntityType.PLAYER);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.COLLECT_ITEM, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // collected entity id
                map(Type.INT, Type.VAR_INT); // collector entity id

                handler(wrapper -> wrapper.user().get(EntityTracker_1_7_6_10.class).removeEntity(wrapper.get(Type.VAR_INT, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // pitch
                map(Type.BYTE); // yaw
                map(Type.INT); // data
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    if (entityTracker == null) return;

                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    final int typeID = wrapper.get(Type.BYTE, 0);
                    final int x = wrapper.get(Type.INT, 0);
                    final int y = wrapper.get(Type.INT, 1);
                    final int z = wrapper.get(Type.INT, 2);
                    entityTracker.trackEntity(entityID, Entity1_10Types.getTypeFromId(typeID, true));
                    entityTracker.updateEntityLocation(entityID, x, y, z, false);
                });
                handler(wrapper -> {
                    final Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(wrapper.get(Type.BYTE, 0), true);
                    int x = wrapper.get(Type.INT, 0);
                    int y = wrapper.get(Type.INT, 1);
                    int z = wrapper.get(Type.INT, 2);
                    byte yaw = wrapper.get(Type.BYTE, 2);
                    int data = wrapper.get(Type.INT, 3);

                    if (type == Entity1_10Types.ObjectType.ITEM_FRAME.getType()) {
                        switch (data) {
                            case 0 -> {
                                z += 32;
                                yaw = 0;
                            }
                            case 1 -> {
                                x -= 32;
                                yaw = 64;
                            }
                            case 2 -> {
                                z -= 32;
                                yaw = -128;
                            }
                            case 3 -> {
                                x += 32;
                                yaw = -64;
                            }
                        }
                    } else if (type == Entity1_10Types.ObjectType.FALLING_BLOCK.getType()) {
                        final IdAndData block = new IdAndData(data & 0xffff, data >> 16);
                        wrapper.user().get(ChunkTracker_1_7_6_10.class).remapBlockParticle(block);
                        data = block.id | block.data << 12;
                    }

                    y = realignEntityY(type, y);

                    wrapper.set(Type.INT, 0, x);
                    wrapper.set(Type.INT, 1, y);
                    wrapper.set(Type.INT, 2, z);
                    wrapper.set(Type.BYTE, 2, yaw);
                    wrapper.set(Type.INT, 3, data);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // entity id
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
                map(Type1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    if (entityTracker == null) return;

                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    final int typeID = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final int x = wrapper.get(Type.INT, 0);
                    final int y = wrapper.get(Type.INT, 1);
                    final int z = wrapper.get(Type.INT, 2);
                    final List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    final Entity1_10Types.EntityType entityType = Entity1_10Types.getTypeFromId(typeID, false);
                    entityTracker.trackEntity(entityID, entityType);
                    entityTracker.updateEntityLocation(entityID, x, y, z, false);
                    entityTracker.updateEntityMetadata(entityID, metadataList);

                    MetadataRewriter.transform(entityType, metadataList);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // entity id
                map(Type.STRING); // motive
                map(Type1_7_6_10.POSITION_INT, Type.POSITION); // position
                map(Type.INT, Type.BYTE); // rotation
                handler(wrapper -> {
                    final short rotation = wrapper.get(Type.BYTE, 0);
                    final Position pos = wrapper.get(Type.POSITION, 0);
                    int modX = 0;
                    int modZ = 0;
                    switch (rotation) {
                        case 0:
                            modZ = 1;
                            break;
                        case 1:
                            modX = -1;
                            break;
                        case 2:
                            modZ = -1;
                            break;
                        case 3:
                            modX = 1;
                            break;
                    }
                    wrapper.set(Type.POSITION, 0, new Position(pos.x() + modX, pos.y(), pos.z() + modZ));
                });
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    wrapper.user().get(EntityTracker_1_7_6_10.class).trackEntity(entityID, Entity1_10Types.EntityType.PAINTING);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.SHORT); // count
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    wrapper.user().get(EntityTracker_1_7_6_10.class).trackEntity(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);

                    wrapper.set(Type.INT, 1, realignEntityY(Entity1_10Types.EntityType.EXPERIENCE_ORB, wrapper.get(Type.INT, 1)));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_VELOCITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.SHORT); // velocity x
                map(Type.SHORT); // velocity y
                map(Type.SHORT); // velocity z
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.DESTROY_ENTITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.INT_ARRAY, Type.VAR_INT_ARRAY_PRIMITIVE); // entity ids
                handler(wrapper -> {
                    for (int entityId : wrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0)) {
                        wrapper.user().get(EntityTracker_1_7_6_10.class).removeEntity(entityId);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_MOVEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 tracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    final int entityId = wrapper.get(Type.VAR_INT, 0);
                    final byte x = wrapper.get(Type.BYTE, 0);
                    final byte y = wrapper.get(Type.BYTE, 1);
                    final byte z = wrapper.get(Type.BYTE, 2);
                    tracker.updateEntityLocation(entityId, x, y, z, true);
                });
                handler(wrapper -> {
                    if (ViaBeta.getConfig().isDynamicOnground()) {
                        final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                        final boolean onGround = wrapper.get(Type.BYTE, 1) > -8/*0.25D*/;
                        entityTracker.getGroundMap().put(wrapper.get(Type.VAR_INT, 0), onGround);
                        wrapper.write(Type.BOOLEAN, onGround); // onGround
                    } else {
                        wrapper.write(Type.BOOLEAN, true); // onGround
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    if (ViaBeta.getConfig().isDynamicOnground()) {
                        final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                        wrapper.write(Type.BOOLEAN, entityTracker.getGroundMap().getOrDefault(wrapper.get(Type.VAR_INT, 0), true)); // onGround
                    } else {
                        wrapper.write(Type.BOOLEAN, true); // onGround
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 tracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    final int entityId = wrapper.get(Type.VAR_INT, 0);
                    final byte x = wrapper.get(Type.BYTE, 0);
                    final byte y = wrapper.get(Type.BYTE, 1);
                    final byte z = wrapper.get(Type.BYTE, 2);
                    tracker.updateEntityLocation(entityId, x, y, z, true);
                });
                handler(wrapper -> {
                    if (ViaBeta.getConfig().isDynamicOnground()) {
                        final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                        final boolean onGround = wrapper.get(Type.BYTE, 1) > -8/*0.25D*/;
                        entityTracker.getGroundMap().put(wrapper.get(Type.VAR_INT, 0), onGround);
                        wrapper.write(Type.BOOLEAN, onGround); // onGround
                    } else {
                        wrapper.write(Type.BOOLEAN, true); // onGround
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_TELEPORT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                create(Type.BOOLEAN, true); // onGround
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 tracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    final int entityId = wrapper.get(Type.VAR_INT, 0);
                    final int x = wrapper.get(Type.INT, 0);
                    final int y = wrapper.get(Type.INT, 1);
                    final int z = wrapper.get(Type.INT, 2);
                    tracker.updateEntityLocation(entityId, x, y, z, false);
                });
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    final Entity1_10Types.EntityType type = entityTracker.getTrackedEntities().get(wrapper.get(Type.VAR_INT, 0));

                    wrapper.set(Type.INT, 1, realignEntityY(type, wrapper.get(Type.INT, 1)));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_HEAD_LOOK, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // head yaw
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ATTACH_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // riding entity id
                map(Type.INT); // vehicle entity id
                map(Type.UNSIGNED_BYTE); // leash state
                handler(wrapper -> {
                    final short leashState = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (leashState == 0) {
                        final EntityTracker_1_7_6_10 tracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                        final int ridingId = wrapper.get(Type.INT, 0);
                        final int vehicleId = wrapper.get(Type.INT, 1);
                        tracker.updateEntityAttachState(ridingId, vehicleId);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 tracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    final List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    if (tracker.getTrackedEntities().containsKey(entityID)) {
                        tracker.updateEntityMetadata(entityID, metadataList);
                        MetadataRewriter.transform(tracker.getTrackedEntities().get(entityID), metadataList);
                        if (metadataList.isEmpty()) wrapper.cancel();
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // effect id
                map(Type.BYTE); // amplifier
                map(Type.SHORT, Type.VAR_INT); // duration
                create(Type.BOOLEAN, false); // hide particles
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.REMOVE_ENTITY_EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // effect id
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SET_EXPERIENCE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // experience bar
                map(Type.SHORT, Type.VAR_INT); // level
                map(Type.SHORT, Type.VAR_INT); // total experience
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                handler(wrapper -> {
                    final int amount = wrapper.passthrough(Type.INT); // count
                    for (int i = 0; i < amount; i++) {
                        wrapper.passthrough(Type.STRING); // id
                        wrapper.passthrough(Type.DOUBLE); // baseValue
                        final int modifierLength = wrapper.read(Type.SHORT); // modifier count
                        wrapper.write(Type.VAR_INT, modifierLength);
                        for (int j = 0; j < modifierLength; j++) {
                            wrapper.passthrough(Type.UUID); // modifier uuid
                            wrapper.passthrough(Type.DOUBLE); // modifier amount
                            wrapper.passthrough(Type.BYTE); // modifier operation
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk chunk = wrapper.read(new Chunk_1_7_6_10Type(wrapper.user().get(ClientWorld.class)));
                    wrapper.user().get(ChunkTracker_1_7_6_10.class).trackAndRemap(chunk);
                    wrapper.write(new Chunk1_8Type(wrapper.user().get(ClientWorld.class)), chunk);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // chunkX
                map(Type.INT); // chunkZ
                map(Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY, Type.BLOCK_CHANGE_RECORD_ARRAY); // blockChangeRecords
                handler(wrapper -> {
                    final int chunkX = wrapper.get(Type.INT, 0);
                    final int chunkZ = wrapper.get(Type.INT, 1);
                    final BlockChangeRecord[] blockChangeRecords = wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0);
                    for (BlockChangeRecord record : blockChangeRecords) {
                        final int targetX = record.getSectionX() + (chunkX << 4);
                        final int targetY = record.getY(-1);
                        final int targetZ = record.getSectionZ() + (chunkZ << 4);
                        final IdAndData block = IdAndData.fromCompressedData(record.getBlockId());
                        final Position pos = new Position(targetX, targetY, targetZ);
                        wrapper.user().get(ChunkTracker_1_7_6_10.class).trackAndRemap(pos, block);
                        record.setBlockId(block.toCompressedData());
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE, Type.POSITION); // position
                handler(wrapper -> {
                    final int blockId = wrapper.read(Type.VAR_INT); // block id
                    final int data = wrapper.read(Type.UNSIGNED_BYTE); // block data
                    final Position pos = wrapper.get(Type.POSITION, 0); // position
                    final IdAndData block = new IdAndData(blockId, data);
                    wrapper.user().get(ChunkTracker_1_7_6_10.class).trackAndRemap(pos, block);
                    wrapper.write(Type.VAR_INT, block.toCompressedData());
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.BLOCK_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT, Type.POSITION); // position
                map(Type.UNSIGNED_BYTE); // type
                map(Type.UNSIGNED_BYTE); // data
                map(Type.VAR_INT); // block id
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // entity id
                map(Type1_7_6_10.POSITION_INT, Type.POSITION); // position
                map(Type.BYTE); // progress
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.MAP_BULK_CHUNK, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk[] chunks = wrapper.read(new ChunkBulk_1_7_6_10Type(wrapper.user().get(ClientWorld.class)));
                    for (Chunk chunk : chunks) {
                        wrapper.user().get(ChunkTracker_1_7_6_10.class).trackAndRemap(chunk);
                    }
                    wrapper.write(new ChunkBulk1_8Type(wrapper.user().get(ClientWorld.class)), chunks);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.EXPLOSION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // x
                map(Type.FLOAT); // y
                map(Type.FLOAT); // z
                map(Type.FLOAT); // radius
                map(Type.INT); // record count
                handler(wrapper -> {
                    final int x = wrapper.get(Type.FLOAT, 0).intValue();
                    final int y = wrapper.get(Type.FLOAT, 1).intValue();
                    final int z = wrapper.get(Type.FLOAT, 2).intValue();
                    final int recordCount = wrapper.get(Type.INT, 0);

                    final ChunkTracker_1_7_6_10 chunkTracker = wrapper.user().get(ChunkTracker_1_7_6_10.class);
                    for (int i = 0; i < recordCount; i++) {
                        final Position pos = new Position(x + wrapper.passthrough(Type.BYTE), y + wrapper.passthrough(Type.BYTE), z + wrapper.passthrough(Type.BYTE));
                        chunkTracker.trackAndRemap(pos, new IdAndData(0, 0));
                    }
                });
                map(Type.FLOAT); // velocity x
                map(Type.FLOAT); // velocity y
                map(Type.FLOAT); // velocity z
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    int effectId = wrapper.read(Type.INT); // effect id
                    final Position pos = wrapper.read(Type1_7_6_10.POSITION_UBYTE); // position
                    int data = wrapper.read(Type.INT); // data
                    final boolean disableRelativeVolume = wrapper.read(Type.BOOLEAN); // server wide

                    if (!disableRelativeVolume && effectId == 2006) { // block dust effect
                        wrapper.setPacketType(ClientboundPackets1_8.SPAWN_PARTICLE);
                        final Random rnd = new Random();
                        final ChunkTracker_1_7_6_10 chunkTracker = wrapper.user().get(ChunkTracker_1_7_6_10.class);
                        final IdAndData block = chunkTracker.getBlockNotNull(pos);
                        if (block.id != 0) {
                            double var21 = Math.min(0.2F + (float) data / 15.0F, 10.0F);
                            if (var21 > 2.5D) var21 = 2.5D;
                            final float var25 = randomFloatClamp(rnd, 0.0F, ((float) Math.PI * 2F));
                            final double var26 = randomFloatClamp(rnd, 0.75F, 1.0F);

                            final float offsetY = (float) (0.20000000298023224D + var21 / 100.0D);
                            final float offsetX = (float) (Math.cos(var25) * 0.2F * var26 * var26 * (var21 + 0.2D));
                            final float offsetZ = (float) (Math.sin(var25) * 0.2F * var26 * var26 * (var21 + 0.2D));
                            final int amount = (int) (150.0D * var21);

                            wrapper.write(Type.INT, Particle_1_7_6_10.BLOCK_DUST.ordinal());
                            wrapper.write(Type.BOOLEAN, false); // longDistance
                            wrapper.write(Type.FLOAT, pos.x() + 0.5F);
                            wrapper.write(Type.FLOAT, pos.y() + 1.0F);
                            wrapper.write(Type.FLOAT, pos.z() + 0.5F);
                            wrapper.write(Type.FLOAT, offsetX);
                            wrapper.write(Type.FLOAT, offsetY);
                            wrapper.write(Type.FLOAT, offsetZ);
                            wrapper.write(Type.FLOAT, 0.15000000596046448F); // particleSpeed
                            wrapper.write(Type.INT, amount);
                            wrapper.write(Type.VAR_INT, block.id | (block.data << 12));
                        } else {
                            wrapper.cancel();
                        }
                    } else {
                        if (!disableRelativeVolume && effectId == 1003) { // door_open
                            if (Math.random() > 0.5) {
                                effectId = 1006; // door_close
                            }
                        } else if (!disableRelativeVolume && effectId == 2001) { // block break effect
                            final ChunkTracker_1_7_6_10 chunkTracker = wrapper.user().get(ChunkTracker_1_7_6_10.class);
                            final int blockID = data & 4095;
                            final int blockData = data >> 12 & 255;
                            final IdAndData block = new IdAndData(blockID, blockData);
                            chunkTracker.remapBlockParticle(block);
                            data = block.id | (block.data << 12);
                        }

                        wrapper.write(Type.INT, effectId);
                        wrapper.write(Type.POSITION, pos);
                        wrapper.write(Type.INT, data);
                        wrapper.write(Type.BOOLEAN, disableRelativeVolume);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SPAWN_PARTICLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String[] parts = wrapper.read(Type.STRING).split("_", 3);
                    Particle_1_7_6_10 particle = Particle_1_7_6_10.find(parts[0]);
                    if (particle == null) {
                        particle = Particle_1_7_6_10.BARRIER;
                        ViaBeta.getPlatform().getLogger().warning("Could not find 1.8 particle for " + Arrays.toString(parts));
                    }
                    wrapper.write(Type.INT, particle.ordinal()); // particle id
                    wrapper.write(Type.BOOLEAN, false); // long distance
                    wrapper.passthrough(Type.FLOAT); // x
                    wrapper.passthrough(Type.FLOAT); // y
                    wrapper.passthrough(Type.FLOAT); // z
                    wrapper.passthrough(Type.FLOAT); // offset x
                    wrapper.passthrough(Type.FLOAT); // offset y
                    wrapper.passthrough(Type.FLOAT); // offset z
                    wrapper.passthrough(Type.FLOAT); // speed
                    wrapper.passthrough(Type.INT); // amount

                    if (particle == Particle_1_7_6_10.ICON_CRACK) {
                        final int id = Integer.parseInt(parts[1]);
                        int damage = 0;
                        if (parts.length > 2) damage = Integer.parseInt(parts[2]);
                        final DataItem item = new DataItem(id, (byte) 1, (short) damage, null);
                        ITEM_REWRITER.rewriteRead(item);
                        wrapper.write(Type.VAR_INT, item.identifier()); // particle data
                        if (item.data() != 0)
                            wrapper.write(Type.VAR_INT, (int) item.data()); // particle data
                    } else if (particle == Particle_1_7_6_10.BLOCK_CRACK || particle == Particle_1_7_6_10.BLOCK_DUST) {
                        final int id = Integer.parseInt(parts[1]);
                        final int metadata = Integer.parseInt(parts[2]);
                        final IdAndData block = new IdAndData(id, metadata);
                        wrapper.user().get(ChunkTracker_1_7_6_10.class).remapBlockParticle(block);
                        wrapper.write(Type.VAR_INT, block.id | block.data << 12); // particle data
                    } else if (particle.extra > 0)
                        throw new IllegalStateException("Tried to write particle which requires extra data, but no handler was found");
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.GAME_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // reason
                map(Type.FLOAT); // value
                handler(wrapper -> {
                    if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 3) {
                        final PacketWrapper chatMessage = PacketWrapper.create(ClientboundPackets1_8.CHAT_MESSAGE, wrapper.user());
                        chatMessage.write(Type.STRING, LEGACY_TO_JSON.transform(chatMessage, "Your game mode has been updated")); // message
                        chatMessage.write(Type.BYTE, (byte) 0); // position
                        chatMessage.send(Protocol1_8to1_7_6_10.class);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final short windowId = wrapper.passthrough(Type.UNSIGNED_BYTE); // window id
                    final short windowType = wrapper.read(Type.UNSIGNED_BYTE); // window type
                    String title = wrapper.read(Type.STRING); // title
                    short slots = wrapper.read(Type.UNSIGNED_BYTE); // slots
                    boolean useProvidedWindowTitle = wrapper.read(Type.BOOLEAN); // use provided title

                    wrapper.user().get(WindowTracker.class).types.put(windowId, windowType);

                    final String inventoryName;
                    switch (windowType) {
                        case 0:
                            inventoryName = "minecraft:chest";
                            break;
                        case 1:
                            inventoryName = "minecraft:crafting_table";
                            break;
                        case 2:
                            inventoryName = "minecraft:furnace";
                            break;
                        case 3:
                            inventoryName = "minecraft:dispenser";
                            break;
                        case 4:
                            inventoryName = "minecraft:enchanting_table";
                            break;
                        case 5:
                            inventoryName = "minecraft:brewing_stand";
                            break;
                        case 6:
                            inventoryName = "minecraft:villager";
                            if (!useProvidedWindowTitle || title.isEmpty()) {
                                title = "entity.Villager.name";
                                useProvidedWindowTitle = false;
                            }
                            break;
                        case 7:
                            inventoryName = "minecraft:beacon";
                            break;
                        case 8:
                            inventoryName = "minecraft:anvil";
                            break;
                        case 9:
                            inventoryName = "minecraft:hopper";
                            break;
                        case 10:
                            inventoryName = "minecraft:dropper";
                            break;
                        case 11:
                            inventoryName = "EntityHorse";
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown window type: " + windowType);
                    }

                    if (windowType == 1/*crafting_table*/ || windowType == 4/*enchanting_table*/ || windowType == 8/*anvil*/) {
                        slots = 0;
                    }

                    if (useProvidedWindowTitle) {
                        title = LEGACY_TO_JSON.transform(wrapper, title);
                    } else {
                        title = LEGACY_TO_JSON_TRANSLATE.transform(wrapper, title);
                    }

                    wrapper.write(Type.STRING, inventoryName);
                    wrapper.write(Type.STRING, title);
                    wrapper.write(Type.UNSIGNED_BYTE, slots);
                    if (windowType == 11) wrapper.passthrough(Type.INT); // entity id
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final short windowId = wrapper.read(Type.BYTE); // window id
                    wrapper.write(Type.UNSIGNED_BYTE, windowId); // actually wrong, should be BYTE but Via uses U_BYTE
                    short slot = wrapper.read(Type.SHORT); // slot
                    final short windowType = wrapper.user().get(WindowTracker.class).get(windowId);
                    if (windowType == 4/*enchanting_table*/ && slot >= 1) slot += 1;
                    wrapper.write(Type.SHORT, slot);
                });
                map(Type1_7_6_10.COMPRESSED_ITEM, Type.ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteRead(wrapper.get(Type.ITEM, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final short windowId = wrapper.passthrough(Type.UNSIGNED_BYTE); // window id
                    final short windowType = wrapper.user().get(WindowTracker.class).get(windowId);
                    Item[] items = wrapper.read(Type1_7_6_10.COMPRESSED_ITEM_ARRAY); // items
                    if (windowType == 4/*enchanting_table*/) {
                        final Item[] old = items;
                        items = new Item[old.length + 1];
                        items[0] = old[0];
                        System.arraycopy(old, 1, items, 2, old.length - 1);
                        items[1] = new DataItem(351/*lapis_lazuli*/, (byte) 3, (short) 4, null);
                    }
                    for (Item item : items) {
                        ITEM_REWRITER.rewriteRead(item);
                    }
                    wrapper.write(Type.ITEM_ARRAY, items);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.WINDOW_PROPERTY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // window id
                map(Type.SHORT); // progress bar id
                map(Type.SHORT); // progress bar value
                handler(wrapper -> {
                    final short windowId = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    short progressBar = wrapper.get(Type.SHORT, 0);
                    final short windowType = wrapper.user().get(WindowTracker.class).get(windowId);
                    if (windowType == 2) { // furnace
                        switch (progressBar) {
                            case 0 -> { // cookTime
                                progressBar = 2;
                                final PacketWrapper windowProperty = PacketWrapper.create(ClientboundPackets1_8.WINDOW_PROPERTY, wrapper.user());
                                windowProperty.write(Type.UNSIGNED_BYTE, windowId);
                                windowProperty.write(Type.SHORT, (short) 3);
                                windowProperty.write(Type.SHORT, (short) 200);
                                windowProperty.send(Protocol1_8to1_7_6_10.class);
                            }
                            case 1 -> // furnaceBurnTime
                                    progressBar = 0;
                            case 2 -> // currentItemBurnTime
                                    progressBar = 1;
                        }
                        wrapper.set(Type.SHORT, 0, progressBar);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT, Type.POSITION); // position
                map(LEGACY_TO_JSON); // line 1
                map(LEGACY_TO_JSON); // line 2
                map(LEGACY_TO_JSON); // line 3
                map(LEGACY_TO_JSON); // line 4
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.MAP_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final int id = wrapper.passthrough(Type.VAR_INT); // map id
                    final byte[] data = wrapper.read(Type.SHORT_BYTE_ARRAY); // data

                    final MapStorage mapStorage = wrapper.user().get(MapStorage.class);
                    MapData mapData = mapStorage.getMapData(id);
                    if (mapData == null) mapStorage.putMapData(id, mapData = new MapData());

                    if (data[0] == 1) {
                        final int count = (data.length - 1) / 3;
                        mapData.mapIcons = new MapIcon[count];

                        for (int i = 0; i < count; i++) {
                            mapData.mapIcons[i] = new MapIcon((byte) (data[i * 3 + 1] >> 4), (byte) (data[i * 3 + 1] & 0xF), data[i * 3 + 2], data[i * 3 + 3]);
                        }
                    } else if (data[0] == 2) {
                        mapData.scale = data[1];
                    }

                    wrapper.write(Type.BYTE, mapData.scale);
                    wrapper.write(Type.VAR_INT, mapData.mapIcons.length);
                    for (MapIcon mapIcon : mapData.mapIcons) {
                        wrapper.write(Type.BYTE, (byte) (mapIcon.direction << 4 | mapIcon.type & 0xF));
                        wrapper.write(Type.BYTE, mapIcon.x);
                        wrapper.write(Type.BYTE, mapIcon.z);
                    }

                    if (data[0] == 0) {
                        final byte x = data[1];
                        final byte z = data[2];
                        final int rows = data.length - 3;
                        final byte[] newData = new byte[rows];
                        System.arraycopy(data, 3, newData, 0, rows);

                        wrapper.write(Type.BYTE, (byte) 1);
                        wrapper.write(Type.BYTE, (byte) rows);
                        wrapper.write(Type.BYTE, x);
                        wrapper.write(Type.BYTE, z);
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, newData);
                    } else {
                        wrapper.write(Type.BYTE, (byte) 0);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT, Type.POSITION); // position
                map(Type.UNSIGNED_BYTE); // type
                map(Type1_7_6_10.COMPRESSED_NBT, Type.NBT); // data
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.OPEN_SIGN_EDITOR, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_INT, Type.POSITION); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.PLAYER_INFO, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String name = wrapper.read(Type.STRING); // name
                    final boolean online = wrapper.read(Type.BOOLEAN); // online
                    final short ping = wrapper.read(Type.SHORT); // ping

                    final TabListStorage tabListStorage = wrapper.user().get(TabListStorage.class);
                    TabListEntry entry = tabListStorage.tablist.get(name);

                    if (entry == null && online) { // add entry
                        tabListStorage.tablist.put(name, entry = new TabListEntry(name, ping));
                        wrapper.write(Type.VAR_INT, 0); // action
                        wrapper.write(Type.VAR_INT, 1); // count
                        wrapper.write(Type.UUID, entry.gameProfile.uuid); // uuid
                        wrapper.write(Type.STRING, entry.gameProfile.userName); // name
                        wrapper.write(Type.VAR_INT, 0); // properties count
                        wrapper.write(Type.VAR_INT, 0); // gamemode
                        wrapper.write(Type.VAR_INT, entry.ping); // ping
                        wrapper.write(Type.OPTIONAL_STRING, null); // display name
                    } else if (entry != null && !online) { // remove entry
                        tabListStorage.tablist.remove(name);
                        wrapper.write(Type.VAR_INT, 4); // action
                        wrapper.write(Type.VAR_INT, 1); // count
                        wrapper.write(Type.UUID, entry.gameProfile.uuid); // uuid
                    } else if (entry != null) { // update ping
                        entry.ping = ping;
                        wrapper.write(Type.VAR_INT, 2); // action
                        wrapper.write(Type.VAR_INT, 1); // count
                        wrapper.write(Type.UUID, entry.gameProfile.uuid); // uuid
                        wrapper.write(Type.VAR_INT, entry.ping); // ping
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // name
                handler(wrapper -> {
                    final String value = wrapper.read(Type.STRING); // value
                    final byte mode = wrapper.passthrough(Type.BYTE); // mode

                    if (mode == 0/*CREATE*/ || mode == 2/*UPDATE*/) {
                        wrapper.write(Type.STRING, value);
                        wrapper.write(Type.STRING, "integer");
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.UPDATE_SCORE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // name
                handler(wrapper -> {
                    final byte mode = wrapper.passthrough(Type.BYTE); // mode
                    if (mode == 0/*UPDATE*/) {
                        wrapper.passthrough(Type.STRING); // objective
                        wrapper.write(Type.VAR_INT, wrapper.read(Type.INT)); // score
                    } else {
                        wrapper.write(Type.STRING, "");
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.TEAMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // name
                handler(wrapper -> {
                    final byte mode = wrapper.passthrough(Type.BYTE); // mode
                    if (mode == 0 || mode == 2) {
                        wrapper.passthrough(Type.STRING); // display name
                        wrapper.passthrough(Type.STRING); // prefix
                        wrapper.passthrough(Type.STRING); // suffix
                        wrapper.passthrough(Type.BYTE); // flags
                        wrapper.write(Type.STRING, "always"); // nametag visibility
                        wrapper.write(Type.BYTE, (byte) 0); // color
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        final int count = wrapper.read(Type.SHORT); // count
                        final String[] playerNames = new String[count];
                        for (int i = 0; i < count; i++) {
                            playerNames[i] = wrapper.read(Type.STRING); // player name
                        }
                        wrapper.write(Type.STRING_ARRAY, playerNames);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_7_2.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // channel
                handler(wrapper -> {
                    final String channel = wrapper.get(Type.STRING, 0);
                    wrapper.read(Type.SHORT); // length

                    switch (channel) {
                        case "MC|Brand": {
                            final byte[] data = wrapper.read(Type.REMAINING_BYTES);
                            final String brand = new String(data, StandardCharsets.UTF_8);
                            wrapper.write(Type.STRING, brand);
                            break;
                        }
                        case "MC|TrList":
                            wrapper.passthrough(Type.INT); // window id
                            final int count = wrapper.passthrough(Type.UNSIGNED_BYTE); // count
                            for (int i = 0; i < count; i++) {
                                Item item = wrapper.read(Type1_7_6_10.COMPRESSED_ITEM);
                                ITEM_REWRITER.rewriteRead(item);
                                wrapper.write(Type.ITEM, item); // item 1

                                item = wrapper.read(Type1_7_6_10.COMPRESSED_ITEM);
                                ITEM_REWRITER.rewriteRead(item);
                                wrapper.write(Type.ITEM, item); // item 3

                                final boolean has3Items = wrapper.passthrough(Type.BOOLEAN); // has 3 items
                                if (has3Items) {
                                    item = wrapper.read(Type1_7_6_10.COMPRESSED_ITEM);
                                    ITEM_REWRITER.rewriteRead(item);
                                    wrapper.write(Type.ITEM, item); // item 2
                                }

                                wrapper.passthrough(Type.BOOLEAN); // unavailable
                                wrapper.write(Type.INT, 0); // uses
                                wrapper.write(Type.INT, Integer.MAX_VALUE); // max uses
                            }
                            break;
                        case "MC|RPack": {
                            final byte[] data = wrapper.read(Type.REMAINING_BYTES);
                            final String resourcePackURL = new String(data, StandardCharsets.UTF_8);
                            wrapper.setPacketType(ClientboundPackets1_8.RESOURCE_PACK);
                            wrapper.clearPacket();
                            wrapper.write(Type.STRING, resourcePackURL);
                            wrapper.write(Type.STRING, "legacy");
                            break;
                        }
                    }
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY); // shared secret
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY); // verify token
            }
        });
        this.registerServerbound(ServerboundPackets1_8.KEEP_ALIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT); // key
            }
        });
        this.registerServerbound(ServerboundPackets1_8.INTERACT_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT); // entity id
                handler(wrapper -> {
                    final int mode = wrapper.read(Type.VAR_INT); // mode
                    if (mode == 2) { // interactAt
                        wrapper.write(Type.BYTE, (byte) 0); // mode
                        wrapper.read(Type.FLOAT); // offsetX
                        wrapper.read(Type.FLOAT); // offsetY
                        wrapper.read(Type.FLOAT); // offsetZ
                        final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                        final Entity1_10Types.EntityType entityType = entityTracker.getTrackedEntities().get(wrapper.get(Type.INT, 0));
                        if (entityType == null || !entityType.isOrHasParent(Entity1_10Types.EntityType.ARMOR_STAND)) {
                            wrapper.cancel();
                        }
                    } else {
                        wrapper.write(Type.BYTE, (byte) mode); // mode
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                handler(wrapper -> wrapper.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 1) + 1.62)); // stance
                map(Type.DOUBLE); // z
                map(Type.BOOLEAN); // onGround
            }
        });
        this.registerServerbound(ServerboundPackets1_8.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                handler(wrapper -> wrapper.write(Type.DOUBLE, wrapper.get(Type.DOUBLE, 1) + 1.62)); // stance
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final TeleportTracker teleportTracker = wrapper.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        Boolean pendingTeleport = teleportTracker.getPending();
                        if (pendingTeleport != null) {
                            wrapper.set(Type.BOOLEAN, 0, pendingTeleport);
                            teleportTracker.setPending(null);
                        }
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.PLAYER_DIGGING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.UNSIGNED_BYTE); // status
                map(Type.POSITION, Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
            }
        });
        this.registerServerbound(ServerboundPackets1_8.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION, Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type.ITEM, Type1_7_6_10.COMPRESSED_ITEM); // item
                map(Type.UNSIGNED_BYTE); // offset x
                map(Type.UNSIGNED_BYTE); // offset y
                map(Type.UNSIGNED_BYTE); // offset z
                handler(wrapper -> {
                    final short direction = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    final Item item = wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0);
                    ITEM_REWRITER.rewriteWrite(item);

                    if (item != null && item.identifier() == ItemList1_6.writtenBook.itemID && direction == 255) { // If placed item is a book then cancel it and send an MC|BOpen to the client
                        final PacketWrapper openBook = PacketWrapper.create(ClientboundPackets1_8.PLUGIN_MESSAGE, wrapper.user());
                        openBook.write(Type.STRING, "MC|BOpen"); // channel
                        openBook.write(Type.REMAINING_BYTES, new byte[0]); // data

                        openBook.send(Protocol1_8to1_7_6_10.class);
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final EntityTracker_1_7_6_10 entityTracker = wrapper.user().get(EntityTracker_1_7_6_10.class);
                    wrapper.write(Type.INT, entityTracker.getPlayerID()); // entity id
                    wrapper.write(Type.BYTE, (byte) 1); // animation
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.ENTITY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.INT); // entity id
                map(Type.VAR_INT, Type.BYTE, action -> (byte) (action + 1)); // action id
                map(Type.VAR_INT, Type.INT); // action parameter
            }
        });
        this.registerServerbound(ServerboundPackets1_8.STEER_VEHICLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // sideways
                map(Type.FLOAT); // forwards
                handler(wrapper -> {
                    final byte flags = wrapper.read(Type.BYTE); // flags
                    wrapper.write(Type.BOOLEAN, (flags & 1) > 0); // jumping
                    wrapper.write(Type.BOOLEAN, (flags & 2) > 0); // sneaking
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final short windowId = wrapper.read(Type.UNSIGNED_BYTE); // window id
                    wrapper.write(Type.BYTE, (byte) windowId); // actually wrong, should be BYTE but Via uses U_BYTE
                    final short slot = wrapper.passthrough(Type.SHORT); // slot

                    final short windowType = wrapper.user().get(WindowTracker.class).get(windowId);
                    if (windowType == 4/*enchanting_table*/) {
                        if (slot == 1) {
                            final PacketWrapper resetHandItem = PacketWrapper.create(ClientboundPackets1_8.SET_SLOT, wrapper.user());
                            resetHandItem.write(Type.UNSIGNED_BYTE, (short) -1); // window id
                            resetHandItem.write(Type.SHORT, (short) 0); // slot
                            resetHandItem.write(Type.ITEM, new DataItem(-1, (byte) 0, (short) 0, null));

                            resetHandItem.send(Protocol1_8to1_7_6_10.class);

                            final PacketWrapper setLapisSlot = PacketWrapper.create(ClientboundPackets1_8.SET_SLOT, wrapper.user());
                            setLapisSlot.write(Type.UNSIGNED_BYTE, windowId);
                            setLapisSlot.write(Type.SHORT, slot);
                            setLapisSlot.write(Type.ITEM, new DataItem(351/*lapis_lazuli*/, (byte) 3, (short) 4, null));

                            setLapisSlot.send(Protocol1_8to1_7_6_10.class);

                            wrapper.cancel();
                        } else if (slot > 1) {
                            wrapper.set(Type.SHORT, 0, (short) (slot - 1));
                        }
                    }
                });
                map(Type.BYTE); // button
                map(Type.SHORT); // transaction id
                map(Type.BYTE); // action
                map(Type.ITEM, Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteWrite(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerServerbound(ServerboundPackets1_8.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(Type.ITEM, Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteWrite(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerServerbound(ServerboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.POSITION, Type1_7_6_10.POSITION_SHORT); // position
                // TODO: Wait for mcstructs to have a json to legacy string method
                handler(PacketWrapper::cancel);
            }
        });
        this.registerServerbound(ServerboundPackets1_8.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String text = wrapper.read(Type.STRING); // text
                    wrapper.clearPacket(); // remove optional block pos
                    wrapper.write(Type.STRING, text);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_8.CLIENT_SETTINGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // language
                map(Type.BYTE); // view distance
                map(Type.BYTE); // chat visibility
                map(Type.BOOLEAN); // enable colors
                create(Type.BYTE, (byte) 2); // difficulty (unused)
                map(Type.UNSIGNED_BYTE, Type.BOOLEAN, flags -> (flags & 1) == 1); // skin flags -> show cape
            }
        });
        this.registerServerbound(ServerboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // channel
                handler(wrapper -> {
                    final String channel = wrapper.get(Type.STRING, 0);

                    if (ViaBeta.getConfig().isIgnoreLong1_8ChannelNames() && channel.length() > 16) {
                        if (!Via.getConfig().isSuppressConversionWarnings()) {
                            ViaBeta.getPlatform().getLogger().warning("Ignoring incoming plugin channel, as it is longer than 16 characters: '" + channel + "'");
                        }
                        wrapper.cancel();
                        return;
                    }

                    final PacketWrapper lengthPacketWrapper = PacketWrapper.create(null, wrapper.user());
                    final ByteBuf lengthBuffer = Unpooled.buffer();

                    switch (channel) {
                        case "MC|BEdit", "MC|BSign" -> {
                            final Item item = wrapper.read(Type.ITEM); // book
                            ITEM_REWRITER.rewriteWrite(item);
                            lengthPacketWrapper.write(Type1_7_6_10.COMPRESSED_ITEM, item);
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);
                            wrapper.write(Type.SHORT, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type1_7_6_10.COMPRESSED_ITEM, item); // book
                        }
                        case "MC|TrSel" -> {
                            final int selectedTrade = wrapper.read(Type.INT); // selected trade
                            lengthPacketWrapper.write(Type.INT, selectedTrade);
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);
                            wrapper.write(Type.SHORT, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type.INT, selectedTrade); // selected trade
                        }
                        case "MC|Brand", "MC|ItemName" -> {
                            final String content = wrapper.read(Type.STRING); // client brand or item name
                            lengthPacketWrapper.write(Type.REMAINING_BYTES, content.getBytes(StandardCharsets.UTF_8));
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);
                            wrapper.write(Type.SHORT, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type.REMAINING_BYTES, content.getBytes(StandardCharsets.UTF_8)); // client brand or item name
                        }
                        case "MC|AdvCdm" -> {
                            final byte type = wrapper.read(Type.BYTE); // command block type (0 = Block, 1 = Minecart)
                            final int posXOrEntityId;
                            final int posY;
                            final int posZ;
                            if (type == 0) {
                                posXOrEntityId = wrapper.read(Type.INT); // x
                                posY = wrapper.read(Type.INT); // y
                                posZ = wrapper.read(Type.INT); // z
                            } else if (type == 1) {
                                posXOrEntityId = wrapper.read(Type.INT); // entity id
                                posY = 0;
                                posZ = 0;
                            } else {
                                ViaBeta.getPlatform().getLogger().warning("Unknown 1.8 command block type: " + type);
                                wrapper.cancel();
                                lengthBuffer.release();
                                return;
                            }
                            final String command = wrapper.read(Type.STRING); // command
                            wrapper.read(Type.BOOLEAN); // track output
                            lengthPacketWrapper.write(Type.BYTE, type);
                            if (type == 0) {
                                lengthPacketWrapper.write(Type.INT, posXOrEntityId);
                                lengthPacketWrapper.write(Type.INT, posY);
                                lengthPacketWrapper.write(Type.INT, posZ);
                            } else {
                                lengthPacketWrapper.write(Type.INT, posXOrEntityId);
                            }
                            lengthPacketWrapper.write(Type.STRING, command);
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);
                            wrapper.write(Type.SHORT, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type.BYTE, type); // type
                            if (type == 0) {
                                wrapper.write(Type.INT, posXOrEntityId); // x
                                wrapper.write(Type.INT, posY); // y
                                wrapper.write(Type.INT, posZ); // z
                            } else {
                                wrapper.write(Type.INT, posXOrEntityId); // entity id
                            }
                            wrapper.write(Type.STRING, command); // command
                        }
                        case "MC|Beacon" -> {
                            final int primaryEffect = wrapper.read(Type.INT); // primary effect
                            final int secondaryEffect = wrapper.read(Type.INT); // secondary effect
                            lengthPacketWrapper.write(Type.INT, primaryEffect);
                            lengthPacketWrapper.write(Type.INT, secondaryEffect);
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);
                            wrapper.write(Type.SHORT, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type.INT, primaryEffect); // primary effect
                            wrapper.write(Type.INT, secondaryEffect); // secondary effect
                        }
                        case "REGISTER", "UNREGISTER" -> {
                            byte[] channels = wrapper.read(Type.REMAINING_BYTES);
                            if (ViaBeta.getConfig().isIgnoreLong1_8ChannelNames()) {
                                final String[] registeredChannels = new String(channels, StandardCharsets.UTF_8).split("\0");
                                final List<String> validChannels = new ArrayList<>(registeredChannels.length);
                                for (String registeredChannel : registeredChannels) {
                                    if (registeredChannel.length() > 16) {
                                        if (!Via.getConfig().isSuppressConversionWarnings()) {
                                            ViaBeta.getPlatform().getLogger().warning("Ignoring incoming plugin channel register of '" + registeredChannel + "', as it is longer than 16 characters");
                                        }
                                        continue;
                                    }
                                    validChannels.add(registeredChannel);
                                }
                                if (validChannels.isEmpty()) {
                                    wrapper.cancel();
                                    return;
                                }
                                channels = Joiner.on('\0').join(validChannels).getBytes(StandardCharsets.UTF_8);
                            }
                            wrapper.write(Type.SHORT, (short) channels.length); // data length
                            wrapper.write(Type.REMAINING_BYTES, channels); // data
                        }
                        default -> {
                            final byte[] data = wrapper.read(Type.REMAINING_BYTES);
                            wrapper.write(Type.SHORT, (short) data.length); // data length
                            wrapper.write(Type.REMAINING_BYTES, data); // data
                        }
                    }
                    lengthBuffer.release();
                });
            }
        });
        this.cancelServerbound(ServerboundPackets1_8.SPECTATE);
        this.cancelServerbound(ServerboundPackets1_8.RESOURCE_PACK_STATUS);
    }

    private void updateDimension(final UserConnection connection, final int dimensionId) {
        if (connection.has(DimensionTracker_1_7_6_10.class)) connection.get(DimensionTracker_1_7_6_10.class).setDimensionId(dimensionId);
        if (connection.has(ClientWorld.class)) connection.get(ClientWorld.class).setEnvironment(dimensionId);
    }

    private float randomFloatClamp(Random rnd, float min, float max) {
        return min >= max ? min : rnd.nextFloat() * (max - min) + min;
    }

    private int realignEntityY(final Entity1_10Types.EntityType type, final int y) {
        float yPos = y / 32F;
        float yOffset = 0F;

        if (type == Entity1_10Types.ObjectType.FALLING_BLOCK.getType()) yOffset = 0.98F / 2F;
        else if (type == Entity1_10Types.ObjectType.TNT_PRIMED.getType()) yOffset = 0.98F / 2F;
        else if (type == Entity1_10Types.ObjectType.ENDER_CRYSTAL.getType()) yOffset = 1F;
        else if (type == Entity1_10Types.ObjectType.MINECART.getType()) yOffset = 0.7F / 2F;
        else if (type == Entity1_10Types.ObjectType.BOAT.getType()) yOffset = 0.6F / 2F;
        else if (type == Entity1_10Types.ObjectType.ITEM.getType()) yOffset = 0.25F / 2F;
        else if (type == Entity1_10Types.ObjectType.LEASH.getType()) yOffset = 0.5F;
        else if (type == Entity1_10Types.EntityType.EXPERIENCE_ORB) yOffset = 0.5F / 2F;

        return (int) Math.floor((yPos - yOffset) * 32F);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new TabListStorage(userConnection));
        userConnection.put(new WindowTracker(userConnection));
        userConnection.put(new EntityTracker_1_7_6_10(userConnection));
        userConnection.put(new MapStorage(userConnection));
        userConnection.put(new DimensionTracker_1_7_6_10(userConnection));
        userConnection.put(new ChunkTracker_1_7_6_10(userConnection));
        userConnection.put(new TeleportTracker(userConnection));

        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
    }
}
