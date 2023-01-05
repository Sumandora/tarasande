package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.LegacyVersionEnum;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.EndBiomeGenerator;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.NetherBiomeGenerator;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.PlainsBiomeGenerator;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.beta.WorldChunkManager_b1_7;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.WorldChunkManager_r1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NibbleArray1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.model.NonFullChunk1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.DimensionTracker_1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.PendingBlocksTracker;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.storage.SeedStorage;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.task.BlockReceiveInvalidatorTask;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.Type1_1;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.type.impl.Chunk_1_1Type;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.ClientboundPackets1_2_1;
import de.florianmichael.viabeta.protocol.protocol1_2_4_5to1_2_1_3.ServerboundPackets1_2_1;
import de.florianmichael.viabeta.protocol.protocol1_3_1_2to1_2_4_5.type.impl.Chunk_1_2_4_5Type;
import de.florianmichael.viabeta.protocol.protocol1_4_2to1_3_1_2.types.Type1_3_1_2;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ChunkTracker;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

import java.util.Arrays;

@SuppressWarnings("DataFlowIssue")
public class Protocol1_2_1_3to1_1 extends AbstractProtocol<ClientboundPackets1_1, ClientboundPackets1_2_1, ServerboundPackets1_1, ServerboundPackets1_2_1> {

    public Protocol1_2_1_3to1_1() {
        super(ClientboundPackets1_1.class, ClientboundPackets1_2_1.class, ServerboundPackets1_1.class, ServerboundPackets1_2_1.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerClientbound(ClientboundPackets1_1.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.STRING); // username
                handler(wrapper -> wrapper.user().get(SeedStorage.class).seed = wrapper.read(Type.LONG)); // seed
                map(Type1_6_4.STRING); // level type
                map(Type.INT); // game mode
                map(Type.BYTE, Type.INT); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
                handler(wrapper -> handleRespawn(wrapper.get(Type.INT, 2), wrapper.user()));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.INT); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                handler(wrapper -> wrapper.user().get(SeedStorage.class).seed = wrapper.read(Type.LONG)); // seed
                map(Type1_6_4.STRING); // level type
                handler(wrapper -> handleRespawn(wrapper.get(Type.INT, 0), wrapper.user()));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.UNSIGNED_BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> wrapper.write(Type.BYTE, wrapper.get(Type.BYTE, 0))); // head yaw
                map(Type1_3_1_2.METADATA_LIST); // metadata
            }
        });
        this.registerClientbound(ClientboundPackets1_1.ENTITY_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> sendEntityHeadLook(wrapper.get(Type.INT, 0), wrapper.get(Type.BYTE, 0), wrapper));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> sendEntityHeadLook(wrapper.get(Type.INT, 0), wrapper.get(Type.BYTE, 3), wrapper));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.ENTITY_TELEPORT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> sendEntityHeadLook(wrapper.get(Type.INT, 0), wrapper.get(Type.BYTE, 0), wrapper));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    final ChunkTracker chunkTracker = wrapper.user().get(ChunkTracker.class);
                    final SeedStorage seedStorage = wrapper.user().get(SeedStorage.class);
                    final PendingBlocksTracker pendingBlocksTracker = wrapper.user().get(PendingBlocksTracker.class);
                    Chunk chunk = wrapper.read(new Chunk_1_1Type(clientWorld));

                    if (chunk instanceof final NonFullChunk1_1 nonFullChunk) {
                        if (!chunkTracker.isChunkLoaded(chunk.getX(), chunk.getZ())) { // Cancel because update in unloaded area is ignored by mc
                            wrapper.cancel();
                            return;
                        }

                        wrapper.setPacketType(ClientboundPackets1_2_1.MULTI_BLOCK_CHANGE);
                        wrapper.write(Type.INT, nonFullChunk.getX());
                        wrapper.write(Type.INT, nonFullChunk.getZ());
                        wrapper.write(Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY, nonFullChunk.asBlockChangeRecords().toArray(new BlockChangeRecord[0]));

                        pendingBlocksTracker.markReceived(new Position((nonFullChunk.getX() << 4) + nonFullChunk.getStartPos().x(), nonFullChunk.getStartPos().y(), (nonFullChunk.getZ() << 4) + nonFullChunk.getStartPos().z()), new Position((nonFullChunk.getX() << 4) + nonFullChunk.getEndPos().x() - 1, nonFullChunk.getEndPos().y() - 1, (nonFullChunk.getZ() << 4) + nonFullChunk.getEndPos().z() - 1));
                        return;
                    }
                    pendingBlocksTracker.markReceived(new Position(chunk.getX() << 4, 0, chunk.getZ() << 4), new Position((chunk.getX() << 4) + 15, chunk.getSections().length * 16, (chunk.getZ() << 4) + 15));

                    int[] newBiomeData;
                    if (seedStorage.worldChunkManager != null) {
                        final byte[] oldBiomeData = seedStorage.worldChunkManager.getBiomeDataAt(chunk.getX(), chunk.getZ());
                        newBiomeData = new int[oldBiomeData.length];
                        for (int i = 0; i < oldBiomeData.length; i++) {
                            newBiomeData[i] = oldBiomeData[i] & 255;
                        }
                    } else {
                        newBiomeData = new int[256];
                        Arrays.fill(newBiomeData, 1); // plains
                    }
                    chunk.setBiomeData(newBiomeData);

                    for (ChunkSection section : chunk.getSections()) {
                        if (section == null) continue;
                        final NibbleArray1_1 oldBlockLight = new NibbleArray1_1(section.getLight().getBlockLight(), 4);
                        final NibbleArray newBlockLight = new NibbleArray(oldBlockLight.size());
                        NibbleArray1_1 oldSkyLight = null;
                        NibbleArray newSkyLight = null;
                        if (section.getLight().hasSkyLight()) {
                            oldSkyLight = new NibbleArray1_1(section.getLight().getSkyLight(), 4);
                            newSkyLight = new NibbleArray(oldSkyLight.size());
                        }

                        for (int x = 0; x < 16; x++) {
                            for (int y = 0; y < 16; y++) {
                                for (int z = 0; z < 16; z++) {
                                    newBlockLight.set(x, y, z, oldBlockLight.get(x, y, z));
                                    if (oldSkyLight != null) newSkyLight.set(x, y, z, oldSkyLight.get(x, y, z));
                                }
                            }
                        }
                        section.getLight().setBlockLight(newBlockLight.getHandle());
                        if (newSkyLight != null) section.getLight().setSkyLight(newSkyLight.getHandle());
                    }

                    if (chunk.getSections().length < 16) { // Increase available sections to match new world height
                        final ChunkSection[] newArray = new ChunkSection[16];
                        System.arraycopy(chunk.getSections(), 0, newArray, 0, chunk.getSections().length);
                        chunk.setSections(newArray);
                    }

                    wrapper.write(new Chunk_1_2_4_5Type(clientWorld), chunk);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_1.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // chunkX
                map(Type.INT); // chunkZ
                map(Type1_1.BLOCK_CHANGE_RECORD_ARRAY, Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY); // blockChangeRecords
                handler(wrapper -> {
                    final PendingBlocksTracker pendingBlocksTracker = wrapper.user().get(PendingBlocksTracker.class);
                    final int chunkX = wrapper.get(Type.INT, 0);
                    final int chunkZ = wrapper.get(Type.INT, 1);
                    final BlockChangeRecord[] blockChangeRecords = wrapper.get(Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY, 0);
                    for (BlockChangeRecord record : blockChangeRecords) {
                        final int targetX = record.getSectionX() + (chunkX << 4);
                        final int targetY = record.getY(-1);
                        final int targetZ = record.getSectionZ() + (chunkZ << 4);
                        pendingBlocksTracker.markReceived(new Position(targetX, targetY, targetZ));
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_1.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // block id
                map(Type.UNSIGNED_BYTE); // block data
                handler(wrapper -> wrapper.user().get(PendingBlocksTracker.class).markReceived(wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_1.EXPLOSION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // radius
                map(Type.INT); // record count
                handler(wrapper -> {
                    final PendingBlocksTracker pendingBlocksTracker = wrapper.user().get(PendingBlocksTracker.class);
                    final ChunkTracker chunkTracker = wrapper.user().get(ChunkTracker.class);
                    final int x = wrapper.get(Type.DOUBLE, 0).intValue();
                    final int y = wrapper.get(Type.DOUBLE, 1).intValue();
                    final int z = wrapper.get(Type.DOUBLE, 2).intValue();
                    final int recordCount = wrapper.get(Type.INT, 0);
                    for (int i = 0; i < recordCount; i++) {
                        final Position pos = new Position(x + wrapper.passthrough(Type.BYTE), y + wrapper.passthrough(Type.BYTE), z + wrapper.passthrough(Type.BYTE));
                        final IdAndData block = chunkTracker.getBlockNotNull(pos);
                        if (block.id != 0) {
                            pendingBlocksTracker.addPending(pos, block);
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_1.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // effect id
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.INT); // data
                handler(wrapper -> {
                    final int sfxId = wrapper.get(Type.INT, 0);
                    final int sfxData = wrapper.get(Type.INT, 1);
                    if (sfxId == 2001) { // Block Break effect
                        final int blockID = sfxData & 255;
                        final int blockData = sfxData >> 8 & 255;
                        wrapper.set(Type.INT, 1, blockID + (blockData << 12));
                    } else if (sfxId == 1009) { // Ghast fireball effect (volume 1) (sound packet would be a better replacement but changing the id is easier and the difference is minimal)
                        wrapper.set(Type.INT, 0, 1008); // Ghast fireball effect (volume 10)
                    }
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPackets1_1.HANDSHAKE.getId(), ServerboundPackets1_2_1.HANDSHAKE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type1_6_4.STRING, s -> s.split(";")[0]); // info
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundPackets1_1.LOGIN.getId(), ServerboundPackets1_2_1.LOGIN.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // protocol id
                map(Type1_6_4.STRING); // username
                create(Type.LONG, 0L); // seed
                map(Type1_6_4.STRING); // level type
                map(Type.INT); // game mode
                map(Type.INT, Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // world height
                map(Type.BYTE); // max players
            }
        });
        this.registerServerbound(ServerboundPackets1_2_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.BYTE); // dimension id
                map(Type.BYTE); // difficulty
                map(Type.BYTE); // game mode
                map(Type.SHORT); // world height
                create(Type.LONG, 0L); // seed
                map(Type1_6_4.STRING); // level type
            }
        });
    }

    private void handleRespawn(final int dimensionId, final UserConnection user) {
        user.get(ClientWorld.class).setEnvironment(dimensionId);
        if (user.get(DimensionTracker_1_1.class).getDimensionId() != dimensionId) {
            user.get(DimensionTracker_1_1.class).setDimension(dimensionId);
            user.get(PendingBlocksTracker.class).clear();
        }

        if (ViaBeta.getConfig().isOldBiomes()) {
            final SeedStorage seedStorage = user.get(SeedStorage.class);
            if (dimensionId == -1) { // Nether
                seedStorage.worldChunkManager = new NetherBiomeGenerator();
            } else if (dimensionId == 1) { // End
                seedStorage.worldChunkManager = new EndBiomeGenerator();
            } else if (dimensionId == 0) { // Overworld
                if (LegacyVersionEnum.fromUserConnection(user).isNewerThanOrEqualTo(LegacyVersionEnum.b1_8tob1_8_1)) {
                    seedStorage.worldChunkManager = new WorldChunkManager_r1_1(LegacyVersionEnum.fromUserConnection(user), seedStorage.seed);
                } else if (LegacyVersionEnum.fromUserConnection(user).isNewerThanOrEqualTo(LegacyVersionEnum.a1_0_15)) {
                    seedStorage.worldChunkManager = new WorldChunkManager_b1_7(seedStorage.seed);
                } else {
                    seedStorage.worldChunkManager = new PlainsBiomeGenerator();
                }
            } else {
                seedStorage.worldChunkManager = null;
            }
        }
    }

    private void sendEntityHeadLook(final int entityId, final byte headYaw, final PacketWrapper wrapper) throws Exception {
        final PacketWrapper entityHeadLook = PacketWrapper.create(ClientboundPackets1_2_1.ENTITY_HEAD_LOOK, wrapper.user());
        entityHeadLook.write(Type.INT, entityId); // entity id
        entityHeadLook.write(Type.BYTE, headYaw); // head yaw

        wrapper.send(Protocol1_2_1_3to1_1.class);
        entityHeadLook.send(Protocol1_2_1_3to1_1.class);
        wrapper.cancel();
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        Via.getPlatform().runRepeatingSync(new BlockReceiveInvalidatorTask(), 1L);
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new PreNettySplitter(userConnection, Protocol1_2_1_3to1_1.class, ClientboundPackets1_1::getPacket));

        userConnection.put(new SeedStorage(userConnection));
        userConnection.put(new PendingBlocksTracker(userConnection));
        userConnection.put(new DimensionTracker_1_1(userConnection));
        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
    }

}
