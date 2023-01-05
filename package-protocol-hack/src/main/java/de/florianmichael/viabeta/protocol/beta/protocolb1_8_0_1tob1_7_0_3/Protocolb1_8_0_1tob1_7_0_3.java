package de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.AlphaInventoryTracker;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.ServerboundPacketsb1_8;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.type.Typeb1_8_0_1;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.storage.PlayerHealthTracker;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.storage.PlayerNameTracker;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NibbleArray1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.SeedStorage;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl.Chunk_1_1Type;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_4_4_5to1_4_2.type.Type1_4_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("DataFlowIssue")
public class Protocolb1_8_0_1tob1_7_0_3 extends AbstractProtocol<ClientboundPacketsb1_7, ClientboundPacketsb1_8, ServerboundPacketsb1_7, ServerboundPacketsb1_8> {

    public Protocolb1_8_0_1tob1_7_0_3() {
        super(ClientboundPacketsb1_7.class, ClientboundPacketsb1_8.class, ServerboundPacketsb1_7.class, ServerboundPacketsb1_8.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundPacketsb1_7.KEEP_ALIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                create(Type.INT, ThreadLocalRandom.current().nextInt(Short.MAX_VALUE)); // key
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // username
                map(Type.LONG); // seed
                create(Type.INT, 0); // game mode
                map(Type.BYTE); // dimension id
                create(Type.BYTE, (byte) 1); // difficulty
                create(Type.BYTE, (byte) -128); // world height
                create(Type.BYTE, (byte) 100); // max players
                handler(wrapper -> {
                    final PacketWrapper playerListEntry = PacketWrapper.create(ClientboundPacketsb1_8.PLAYER_INFO, wrapper.user());
                    playerListEntry.write(Type1_6_4.STRING, wrapper.user().getProtocolInfo().getUsername()); // name
                    playerListEntry.write(Type.BOOLEAN, true); // online
                    playerListEntry.write(Type.SHORT, (short) 0); // ping

                    wrapper.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    playerListEntry.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.UPDATE_HEALTH, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // health
                create(Type.SHORT, (short) 6); // food
                create(Type.FLOAT, 0F); // saturation
                handler(wrapper -> wrapper.user().get(PlayerHealthTracker.class).setHealth(wrapper.get(Type.SHORT, 0)));
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // dimension id
                create(Type.BYTE, (byte) 1); // difficulty
                create(Type.BYTE, (byte) 0); // game mode
                create(Type.SHORT, (short) 128); // world height
                handler(wrapper -> wrapper.write(Type.LONG, wrapper.user().get(SeedStorage.class).seed)); // seed
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // username
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type.UNSIGNED_SHORT); // item
                handler(wrapper -> {
                    final int entityId = wrapper.get(Type.INT, 0);
                    final PlayerNameTracker playerNameTracker = wrapper.user().get(PlayerNameTracker.class);
                    playerNameTracker.names.put(entityId, wrapper.get(Type1_6_4.STRING, 0));

                    final PacketWrapper playerListEntry = PacketWrapper.create(ClientboundPacketsb1_8.PLAYER_INFO, wrapper.user());
                    playerListEntry.write(Type1_6_4.STRING, playerNameTracker.names.get(entityId)); // name
                    playerListEntry.write(Type.BOOLEAN, true); // online
                    playerListEntry.write(Type.SHORT, (short) 0); // ping
                    playerListEntry.send(Protocolb1_8_0_1tob1_7_0_3.class);
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                map(Type1_3_1_2.METADATA_LIST); // metadata
                handler(wrapper -> {
                    final short entityType = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (entityType == 49) { // monster
                        final PacketWrapper spawnMonster = PacketWrapper.create(ClientboundPacketsb1_8.SPAWN_PLAYER, wrapper.user());
                        spawnMonster.write(Type.INT, wrapper.get(Type.INT, 0)); // entity id
                        spawnMonster.write(Type1_6_4.STRING, "Monster"); // username
                        spawnMonster.write(Type.INT, wrapper.get(Type.INT, 1)); // x
                        spawnMonster.write(Type.INT, wrapper.get(Type.INT, 2)); // y
                        spawnMonster.write(Type.INT, wrapper.get(Type.INT, 3)); // z
                        spawnMonster.write(Type.BYTE, wrapper.get(Type.BYTE, 0)); // yaw
                        spawnMonster.write(Type.BYTE, wrapper.get(Type.BYTE, 1)); // pitch
                        spawnMonster.write(Type.UNSIGNED_SHORT, 0); // item

                        final PacketWrapper entityMetadata = PacketWrapper.create(ClientboundPacketsb1_8.ENTITY_METADATA, wrapper.user());
                        entityMetadata.write(Type.INT, wrapper.get(Type.INT, 0)); // entity id
                        entityMetadata.write(Type1_3_1_2.METADATA_LIST, wrapper.get(Type1_3_1_2.METADATA_LIST, 0)); // metadata

                        wrapper.cancel();
                        spawnMonster.send(Protocolb1_8_0_1tob1_7_0_3.class);
                        entityMetadata.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.DESTROY_ENTITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    final PlayerNameTracker playerNameTracker = wrapper.user().get(PlayerNameTracker.class);
                    final String name = playerNameTracker.names.get(wrapper.get(Type.INT, 0));
                    if (name != null) {
                        final PacketWrapper playerListEntry = PacketWrapper.create(ClientboundPacketsb1_8.PLAYER_INFO, wrapper.user());
                        playerListEntry.write(Type1_6_4.STRING, name); // name
                        playerListEntry.write(Type.BOOLEAN, false); // online
                        playerListEntry.write(Type.SHORT, (short) 0); // ping
                        playerListEntry.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk chunk = wrapper.passthrough(new Chunk_1_1Type(wrapper.user().get(ClientWorld.class)));

                    boolean hasChest = false;
                    for (ChunkSection section : chunk.getSections()) {
                        if (section == null || !section.getLight().hasSkyLight()) continue;
                        for (int i = 0; i < section.palette(PaletteType.BLOCKS).size(); i++) {
                            if (section.palette(PaletteType.BLOCKS).idByIndex(i) >> 4 == BlockList1_6.chest.blockID) {
                                hasChest = true;
                                break;
                            }
                        }
                        if (!hasChest) continue;

                        final NibbleArray1_1 sectionSkyLight = new NibbleArray1_1(section.getLight().getSkyLight(), 4);
                        for (int y = 0; y < 16; y++)
                            for (int x = 0; x < 16; x++)
                                for (int z = 0; z < 16; z++)
                                    if (section.palette(PaletteType.BLOCKS).idAt(x, y, z) >> 4 == BlockList1_6.chest.blockID)
                                        sectionSkyLight.set(x, y, z, 15);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.GAME_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // reason
                create(Type.BYTE, (byte) 0); // value
            }
        });
        this.registerClientbound(ClientboundPacketsb1_7.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // window id
                map(Type.UNSIGNED_BYTE); // window type
                map(Typeb1_7_0_3.STRING, Type1_6_4.STRING); // title
                map(Type.UNSIGNED_BYTE); // slots
            }
        });

        this.registerServerbound(State.STATUS, -1, ServerboundPacketsb1_8.SERVER_PING.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final PacketWrapper pingResponse = PacketWrapper.create(ClientboundPacketsb1_8.DISCONNECT, wrapper.user());
                    pingResponse.write(Type1_6_4.STRING, "The server seems to be running!\nWait 5 seconds between each connection§0§1");
                    pingResponse.send(Protocolb1_8_0_1tob1_7_0_3.class);
                });
            }
        });
        this.cancelServerbound(ServerboundPacketsb1_8.SERVER_PING);
        this.registerServerbound(State.LOGIN, ServerboundPacketsb1_7.LOGIN.getId(), ServerboundPacketsb1_8.LOGIN.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // protocol id
                map(Type1_6_4.STRING); // username
                map(Type.LONG); // seed
                read(Type.INT); // game mode
                map(Type.BYTE); // dimension id
                read(Type.BYTE); // difficulty
                read(Type.BYTE); // world height
                read(Type.BYTE); // max players
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // dimension id
                read(Type.BYTE); // difficulty
                read(Type.BYTE); // game mode
                read(Type.SHORT); // world height
                read(Type.LONG); // seed
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.PLAYER_DIGGING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // status
                handler(wrapper -> {
                    final short status = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (status == 5) wrapper.cancel(); // Stop using item
                });
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_4_2.NBTLESS_ITEM); // item
                handler(wrapper -> {
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0);
                    if (wrapper.user().get(ChunkTracker.class).getBlockNotNull(pos).id == BlockList1_6.cake.blockID) {
                        final PacketWrapper updateHealth = PacketWrapper.create(ClientboundPacketsb1_8.UPDATE_HEALTH, wrapper.user());
                        updateHealth.write(Type.SHORT, wrapper.user().get(PlayerHealthTracker.class).getHealth()); // health
                        updateHealth.write(Type.SHORT, (short) 6); // food
                        updateHealth.write(Type.FLOAT, 0F); // saturation
                        updateHealth.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.ENTITY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // action id
                handler(wrapper -> {
                    if (wrapper.get(Type.BYTE, 0) > 3) wrapper.cancel();
                });
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                handler(wrapper -> {
                    if (wrapper.passthrough(Type.SHORT) /*slot*/ == -1) wrapper.cancel();
                });
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                map(Type.BYTE); // mode
                map(Type1_4_2.NBTLESS_ITEM); // item
            }
        });
        this.registerServerbound(ServerboundPacketsb1_8.CREATIVE_INVENTORY_ACTION, null, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    // Track the item for later use in classic protocols
                    final AlphaInventoryTracker inventoryTracker = wrapper.user().get(AlphaInventoryTracker.class);
                    if (inventoryTracker != null) inventoryTracker.handleCreativeSetSlot(wrapper.read(Type.SHORT), wrapper.read(Typeb1_8_0_1.CREATIVE_ITEM));
                });
            }
        });
        this.cancelServerbound(ServerboundPacketsb1_8.KEEP_ALIVE); // beta client only sends this packet every second if in downloading terrain screen
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new PreNettySplitter(userConnection, Protocolb1_8_0_1tob1_7_0_3.class, ClientboundPacketsb1_7::getPacket));

        userConnection.put(new PlayerNameTracker(userConnection));
        userConnection.put(new PlayerHealthTracker(userConnection));
    }
}
