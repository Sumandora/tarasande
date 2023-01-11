package de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2;

import com.google.common.collect.Lists;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.rewriter.BlockDataRewriter;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.storage.EntityFlagStorage;
import de.florianmichael.viabeta.protocol.beta.protocolb1_2_0_2tob1_1_2.type.Typeb1_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.ClientboundPacketsb1_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.ServerboundPacketsb1_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type.Typeb1_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.type.impl.MetaTypeb1_2;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.Type1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl.Chunk_1_1Type;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.ArrayList;

@SuppressWarnings("DataFlowIssue")
public class Protocolb1_2_0_2tob1_1_2 extends AbstractProtocol<ClientboundPacketsb1_1, ClientboundPacketsb1_2, ServerboundPacketsb1_1, ServerboundPacketsb1_2> {

    private final BlockDataRewriter BLOCK_DATA_REWRITER = new BlockDataRewriter();

    public Protocolb1_2_0_2tob1_1_2() {
        super(ClientboundPacketsb1_1.class, ClientboundPacketsb1_2.class, ServerboundPacketsb1_1.class, ServerboundPacketsb1_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsb1_1.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.SHORT); // slot
                map(Type.SHORT); // item id
                create(Type.SHORT, (short) 0); // item damage
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.ENTITY_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // animation id
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final byte animationId = wrapper.get(Type.BYTE, 0);
                    if (animationId <= 2) return; // 1 - Swing | 2 - Hurt

                    wrapper.cancel();
                    final EntityFlagStorage entityFlagStorage = wrapper.user().get(EntityFlagStorage.class);
                    final int oldMask = entityFlagStorage.getFlagMask(entityId);
                    switch (animationId) {
                        case 100 -> // start riding
                                entityFlagStorage.setFlag(entityId, 2, true);
                        case 101 -> // stop riding
                                entityFlagStorage.setFlag(entityId, 2, false);
                        case 102 -> // start burning
                                entityFlagStorage.setFlag(entityId, 0, true);
                        case 103 -> // stop burning
                                entityFlagStorage.setFlag(entityId, 0, false);
                        case 104 -> // start sneaking
                                entityFlagStorage.setFlag(entityId, 1, true);
                        case 105 -> // stop sneaking
                                entityFlagStorage.setFlag(entityId, 1, false);
                    }

                    if (oldMask != entityFlagStorage.getFlagMask(entityId)) {
                        final PacketWrapper metadata = PacketWrapper.create(ClientboundPacketsb1_2.ENTITY_METADATA, wrapper.user());
                        metadata.write(Type.INT, wrapper.get(Type.INT, 0)); // entity id
                        metadata.write(Typeb1_2.METADATA_LIST, Lists.newArrayList(new Metadata(0, MetaTypeb1_2.Byte, (byte) entityFlagStorage.getFlagMask(entityId)))); // metadata
                        metadata.send(Protocolb1_2_0_2tob1_1_2.class);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.SPAWN_ITEM, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    final short itemId = wrapper.read(Type.SHORT); // item id
                    final byte itemCount = wrapper.read(Type.BYTE); // item count
                    wrapper.write(Type1_3_1_2.NBTLESS_ITEM, new DataItem(itemId, itemCount, (short) 0, null)); // item
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // velocity x
                map(Type.BYTE); // velocity y
                map(Type.BYTE); // velocity z
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                create(Typeb1_2.METADATA_LIST, new ArrayList<>()); // metadata
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final ClientWorld clientWorld = new ClientWorld(wrapper.user());
                    clientWorld.setEnvironment(0);
                    BLOCK_DATA_REWRITER.remapChunk(wrapper.passthrough(new Chunk_1_1Type(clientWorld)));
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // chunkX
                map(Type.INT); // chunkZ
                map(Type1_1.BLOCK_CHANGE_RECORD_ARRAY); // blockChangeRecords
                handler(wrapper -> BLOCK_DATA_REWRITER.remapBlockChangeRecords(wrapper.get(Type1_1.BLOCK_CHANGE_RECORD_ARRAY, 0)));
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // block id
                map(Type.UNSIGNED_BYTE); // block data
                handler(wrapper -> {
                    final IdAndData block = new IdAndData(wrapper.get(Type.UNSIGNED_BYTE, 0), wrapper.get(Type.UNSIGNED_BYTE, 1));
                    BLOCK_DATA_REWRITER.remapBlock(block);
                    wrapper.set(Type.UNSIGNED_BYTE, 0, (short) block.id);
                    wrapper.set(Type.UNSIGNED_BYTE, 1, (short) block.data);
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_1.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Typeb1_1.NBTLESS_ITEM, Type1_4_2.NBTLESS_ITEM); // item
            }
        });

        this.registerServerbound(ServerboundPacketsb1_2.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_4_2.NBTLESS_ITEM, Typeb1_1.NBTLESS_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPacketsb1_2.ENTITY_ACTION, ServerboundPacketsb1_1.ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE, Type.UNSIGNED_BYTE, i -> (short) (i + 103)); // action id | start/stop sneaking (1/2) -> 104/105
            }
        });
        this.registerServerbound(ServerboundPacketsb1_2.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                map(Type1_4_2.NBTLESS_ITEM, Typeb1_1.NBTLESS_ITEM); // item
            }
        });
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolb1_2_0_2tob1_1_2.class, ClientboundPacketsb1_1::getPacket));

        userConnection.put(new EntityFlagStorage(userConnection));
    }
}
