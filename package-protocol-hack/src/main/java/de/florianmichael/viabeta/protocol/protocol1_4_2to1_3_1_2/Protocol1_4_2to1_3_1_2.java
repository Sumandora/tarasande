package de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2;

import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.data.ItemList1_6;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.rewriter.SoundRewriter;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.ClientboundPackets1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.impl.MetaType1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.ServerboundPackets1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_6_1to1_5_2.storage.EntityTracker_1_5_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_4_2to1_3_1_2 extends AbstractProtocol<ClientboundPackets1_3_1, ClientboundPackets1_4_2, ServerboundPackets1_3_1, ServerboundPackets1_5_2> {

    public Protocol1_4_2to1_3_1_2() {
        super(ClientboundPackets1_3_1.class, ClientboundPackets1_4_2.class, ServerboundPackets1_3_1.class, ServerboundPackets1_5_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.STATUS, ClientboundPackets1_3_1.DISCONNECT.getId(), ClientboundPackets1_4_2.DISCONNECT.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String reason = wrapper.read(Type1_6_4.STRING); // reason
                    try {
                        final ProtocolInfo info = wrapper.user().getProtocolInfo();
                        final String[] pingParts = reason.split("§");
                        final String out = "§1\0" + (-info.getServerProtocolVersion() >> 2) + "\0" + ProtocolVersion.getProtocol(info.getServerProtocolVersion()).getName() + "\0" + pingParts[0] + "\0" + pingParts[1] + "\0" + pingParts[2];
                        wrapper.write(Type1_6_4.STRING, out);
                    } catch (Throwable e) {
                        ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not parse 1.3.1 ping: " + reason, e);
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.TIME_UPDATE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final long time = wrapper.passthrough(Type.LONG); // time
                    wrapper.write(Type.LONG, time % 24_000); // time of day
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                map(Type1_6_4.STRING); // worldType
                handler(wrapper -> {
                    final EntityTracker_1_5_2 entityTracker = wrapper.user().get(EntityTracker_1_5_2.class);
                    final Integer[] entityIds = entityTracker.getTrackedEntities().keySet().stream().filter(i -> i != entityTracker.getPlayerID()).toArray(Integer[]::new);
                    final int[] primitiveInts = new int[entityIds.length];
                    for (int i = 0; i < entityIds.length; i++) primitiveInts[i] = entityIds[i];

                    final PacketWrapper destroyEntities = PacketWrapper.create(ClientboundPackets1_4_2.DESTROY_ENTITIES, wrapper.user());
                    destroyEntities.write(Type1_7_6_10.INT_ARRAY, primitiveInts);
                    destroyEntities.send(Protocol1_4_2to1_3_1_2.class);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.SPAWN_PLAYER, new PacketRemapper() {
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
                map(Type1_3_1_2.METADATA_LIST, Type1_4_2.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_4_2.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.SPAWN_ITEM, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_3_1_2.NBTLESS_ITEM, Type1_7_6_10.COMPRESSED_ITEM);
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // velocity x
                map(Type.BYTE); // velocity y
                map(Type.BYTE); // velocity z
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.SPAWN_MOB, new PacketRemapper() {
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
                map(Type.SHORT); // velocity x
                map(Type.SHORT); // velocity y
                map(Type.SHORT); // velocity z
                map(Type1_3_1_2.METADATA_LIST, Type1_4_2.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_4_2.METADATA_LIST, 0)));
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final short typeId = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (typeId == Entity1_10Types.EntityType.SKELETON.getId()) {
                        setMobHandItem(entityId, new DataItem(ItemList1_6.bow.itemID, (byte) 1, (short) 0, null), wrapper);
                    } else if (typeId == Entity1_10Types.EntityType.PIG_ZOMBIE.getId()) {
                        setMobHandItem(entityId, new DataItem(ItemList1_6.swordGold.itemID, (byte) 1, (short) 0, null), wrapper);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // motive
                map(Type1_7_6_10.POSITION_INT); // position
                map(Type.INT); // rotation
                handler(wrapper -> {
                    int direction = wrapper.get(Type.INT, 1);
                    switch (direction) {
                        case 0:
                            direction = 2;
                            break;
                        case 2:
                            direction = 0;
                            break;
                    }
                    wrapper.set(Type.INT, 1, direction);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_3_1_2.METADATA_LIST, Type1_4_2.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_4_2.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // effect id
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.INT); // data
                create(Type.BOOLEAN, false); // server wide
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.NAMED_SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String oldSound = wrapper.read(Type1_6_4.STRING); // sound
                    String newSound = SoundRewriter.map(oldSound);
                    if (oldSound.isEmpty()) newSound = "";
                    if (newSound == null) {
                        ViaBeta.getPlatform().getLogger().warning("Unable to map 1.3.2 sound '" + oldSound + "'");
                        newSound = "";
                    }
                    if (newSound.isEmpty()) {
                        wrapper.cancel();
                        return;
                    }
                    wrapper.write(Type1_6_4.STRING, newSound);
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.FLOAT); // volume
                map(Type.UNSIGNED_BYTE); // pitch
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.MAP_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // item id
                map(Type.SHORT); // map id
                map(Type1_4_2.UNSIGNED_BYTE_BYTE_ARRAY); // data
                handler(wrapper -> {
                    final byte[] data = wrapper.get(Type1_4_2.UNSIGNED_BYTE_BYTE_ARRAY, 0);
                    if (data[0] == 1) {
                        for (int i = 0; i < (data.length - 1) / 3; i++) {
                            final byte icon = (byte) (data[i * 3 + 1] % 16);
                            final byte centerX = data[i * 3 + 2];
                            final byte centerZ = data[i * 3 + 3];
                            final byte iconRotation = (byte) (data[i * 3 + 1] / 16);
                            data[i * 3 + 1] = (byte) (icon << 4 | iconRotation & 15);
                            data[i * 3 + 2] = centerX;
                            data[i * 3 + 3] = centerZ;
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_3_1.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING); // channel
                handler(wrapper -> {
                    final String channel = wrapper.get(Type1_6_4.STRING, 0);
                    wrapper.passthrough(Type.SHORT); // length
                    if (channel.equalsIgnoreCase("MC|TrList")) {
                        wrapper.passthrough(Type.INT); // window Id
                        final int count = wrapper.passthrough(Type.UNSIGNED_BYTE); // count
                        for (int i = 0; i < count; i++) {
                            wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM); // item 1
                            wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM); // item 3
                            if (wrapper.passthrough(Type.BOOLEAN)) { // has 3 items
                                wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM); // item 2
                            }
                            wrapper.write(Type.BOOLEAN, false); // unavailable
                        }
                    }
                });
            }
        });

        this.registerServerbound(State.STATUS, ServerboundPackets1_3_1.SERVER_PING.getId(), ServerboundPackets1_5_2.SERVER_PING.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(PacketWrapper::clearPacket);
            }
        });
        this.registerServerbound(ServerboundPackets1_5_2.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> {
                    final Item itm = wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0);
                    if (itm != null && itm.identifier() == ItemList1_6.emptyMap.itemID) {
                        itm.setIdentifier(ItemList1_6.map.itemID);
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_5_2.CLIENT_SETTINGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING); // language
                map(Type.BYTE); // view distance
                map(Type.BYTE); // mask
                map(Type.BYTE); // difficulty
                read(Type.BOOLEAN); // show cape
            }
        });
    }

    private void rewriteMetadata(final List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            metadata.setMetaType(MetaType1_4_2.byId(metadata.metaType().typeId()));
        }
    }

    private void setMobHandItem(final int entityId, final Item item, final PacketWrapper wrapper) throws Exception {
        final PacketWrapper handItem = PacketWrapper.create(ClientboundPackets1_4_2.ENTITY_EQUIPMENT, wrapper.user());
        handItem.write(Type.INT, entityId); // entity id
        handItem.write(Type.SHORT, (short) 0); // slot
        handItem.write(Type1_7_6_10.COMPRESSED_ITEM, item); // item

        wrapper.send(Protocol1_4_2to1_3_1_2.class);
        handItem.send(Protocol1_4_2to1_3_1_2.class);

        wrapper.cancel();
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocol1_4_2to1_3_1_2.class, ClientboundPackets1_3_1::getPacket));
    }
}
