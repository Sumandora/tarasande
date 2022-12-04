/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.chunk.ChunkData;
import de.florianmichael.vialegacy.protocols.protocol1_2_1_3to1_1.chunk.ChunkTracker;
import de.florianmichael.vialegacy.protocols.protocol1_2_4_5to1_2_1_3.ClientboundPackets1_2_1_3;
import de.florianmichael.vialegacy.protocols.protocol1_2_4_5to1_2_1_3.ServerboundPackets1_2_1_3;
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.ClientboundLoginPackets1_2_4_5;
import de.florianmichael.vialegacy.protocols.protocol1_3_1_2to1_2_4_5.type.Types1_2_5;
import de.florianmichael.vialegacy.protocols.protocol1_4_6_7to1_4_4_5.storage.DimensionStorage;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;

import java.util.zip.Inflater;

public class Protocol1_2_1_3to1_1 extends EnZaProtocol<ClientboundPackets1_1, ClientboundPackets1_2_1_3, ServerboundPackets1_1, ServerboundPackets1_2_1_3> {

    public Protocol1_2_1_3to1_1() {
        super(ClientboundPackets1_1.class, ClientboundPackets1_2_1_3.class, ServerboundPackets1_1.class, ServerboundPackets1_2_1_3.class);
    }

    @Override
    protected void registerPackets() {
        super.registerPackets();

        this.registerServerbound(ServerboundPackets1_2_1_3.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Types1_6_4.STRING); // Username
                handler((pw) -> pw.write(Type.LONG, (long) 0)); // Seed
                map(Types1_6_4.STRING); // Level-Type
                map(Type.INT); // Game mode
                map(Type.INT, Type.BYTE); // Dimension
                map(Type.BYTE); // Difficulty
                map(Type.BYTE); // World-Height
                map(Type.BYTE); // Max. Players
            }
        });

        this.registerServerbound(ServerboundPackets1_2_1_3.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.BYTE); // Dimension
                map(Type.BYTE); // Difficulty
                map(Type.BYTE); // Game mode
                map(Type.SHORT); // World-Height
                handler((pw) -> pw.write(Type.LONG, (long) 0)); // Seed
                map(Types1_6_4.STRING); // Level-Type
            }
        });

        this.registerClientbound(ClientboundPackets1_1.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Types1_6_4.STRING); // Username
                map(Type.LONG, Type.NOTHING); // Seed
                map(Types1_6_4.STRING); // Level-Type
                map(Type.INT); // Game mode
                map(Type.BYTE, Type.INT); // Dimension
                map(Type.BYTE); // Difficulty
                map(Type.BYTE); // World-Height
                map(Type.BYTE); // Max. Players

                handler((pw) -> {
                    final DimensionStorage dimensionStorage = pw.user().get(DimensionStorage.class);

                    if (dimensionStorage != null) {
                        dimensionStorage.setDimension(pw.get(Type.INT, 2));
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Type.BYTE); // Entity-Type
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Yaw
                handler((pw) -> pw.write(Type.BYTE, (byte) 0)); // Head pitch
                map(Type.BYTE); // Pitch
                map(Types1_2_5.METADATA_LIST); // Metadata list
            }
        });

        this.registerClientbound(ClientboundPackets1_1.ENTITY_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Type.BYTE); // Yaw
                map(Type.BYTE); // Pitch

                handler((pw) -> {
                    final PacketWrapper entityHeadLook = PacketWrapper.create(ClientboundPackets1_1.ENTITY_HEAD_LOOK, pw.user());
                    entityHeadLook.write(Type.INT, pw.get(Type.INT, 0));
                    entityHeadLook.write(Type.BYTE, pw.get(Type.BYTE, 0));

                    entityHeadLook.send(Protocol1_2_1_3to1_1.class);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.ENTITY_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Type.BYTE); // X-Position
                map(Type.BYTE); // Y-Position
                map(Type.BYTE); // Z-Position
                map(Type.BYTE); // Yaw
                map(Type.BYTE); // Pitch

                handler((pw) -> {
                    final PacketWrapper entityHeadLook = PacketWrapper.create(ClientboundPackets1_1.ENTITY_HEAD_LOOK, pw.user());
                    entityHeadLook.write(Type.INT, pw.get(Type.INT, 0));
                    entityHeadLook.write(Type.BYTE, pw.get(Type.BYTE, 3));

                    entityHeadLook.send(Protocol1_2_1_3to1_1.class);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.INT); // Dimension
                map(Type.BYTE); // Difficulty
                map(Type.BYTE); // Game mode
                map(Type.SHORT); // World-Height
                map(Type.LONG, Type.NOTHING); // Seed
                map(Types1_6_4.STRING); // Level-Type

                handler((pw) -> {
                    final DimensionStorage dimensionStorage = pw.user().get(DimensionStorage.class);

                    if (dimensionStorage != null) {
                        dimensionStorage.setDimension(pw.get(Type.INT, 0));
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.PRE_CHUNK, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Chunk-X
                map(Type.INT); // Chunk-Z
                map(Type.UNSIGNED_BYTE); // Load chunk

                handler((pw) -> {
                    final ChunkTracker tracker = pw.user().get(ChunkTracker.class);
                    int x = pw.get(Type.INT, 0);
                    int z = pw.get(Type.INT, 1);
                    boolean loadChunk = pw.get(Type.UNSIGNED_BYTE, 0) != 0;
                    if(loadChunk) {
                        tracker.loadChunk(x, z, new ChunkData(x, z));
                    } else {
                        tracker.unloadChunk(x, z);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    int absX = pw.read(Type.INT);
                    short absY = pw.read(Type.SHORT);
                    int absZ = pw.read(Type.INT);
                    int xSize = pw.read(Type.UNSIGNED_BYTE) + 1;
                    int ySize = pw.read(Type.UNSIGNED_BYTE) + 1;
                    int zSize = pw.read(Type.UNSIGNED_BYTE) + 1;

                    int chunkSize = pw.read(Type.INT);
                    byte[] compressed = pw.read(new CustomByteType(chunkSize));
                    byte[] data = new byte[(xSize * ySize * zSize * 5) / 2];

                    Inflater inflater = new Inflater();
                    inflater.setInput(compressed);
                    chunkSize = inflater.inflate(data);
                    inflater.end();

                    ChunkTracker tracker = pw.user().get(ChunkTracker.class);
                    tracker.updateBlocks(absX, absY, absZ, xSize, ySize, zSize, data, chunkSize);
                    pw.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    int x = pw.read(Type.INT);
                    int z = pw.read(Type.INT);
                    int size = pw.read(Type.UNSIGNED_SHORT);

                    short[] coords = new short[size];
                    for(int i = 0; i < size; i++) {
                        coords[i] = pw.read(Type.SHORT);
                    }
                    byte[] types = pw.read(new CustomByteType(size));
                    byte[] metadatas = pw.read(new CustomByteType(size));
                    pw.clearPacket();

                    pw.write(Type.INT, x);
                    pw.write(Type.INT, z);

                    pw.write(Type.SHORT, (short) size);
                    pw.write(Type.INT, size * 4);

                    ChunkTracker tracker = pw.user().get(ChunkTracker.class);
                    for(int i = 0; i < size; i++) {
                        short coord = coords[i];
                        int id = types[i] & 0xff;
                        byte metadata = metadatas[i];

                        int relX = coord >> 12 & 0xf;
                        int relZ = coord >> 8 & 0xf;
                        int relY = coord & 0xff;

                        int targetX = relX + (x << 4);
                        int targetY = relY;
                        int targetZ = relZ + (z << 4);
                        if(tracker.isChunkLoaded(x, z)) {
                            ChunkData chunk = tracker.getChunkAt(x, z);
                            chunk.setBlockId(relX, relY, relZ, id);
                            chunk.setMetadata(relX, relY, relZ, metadata);
                        }

                        pw.write(Type.SHORT, coord);
                        pw.write(Type.SHORT, (short) ((id << 4) | (metadata & 0xF)));
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // X-Position
                map(Type.UNSIGNED_BYTE); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Block-Type
                map(Type.BYTE); // Block-Metadata

                handler((pw) -> {
                    int x = pw.get(Type.INT, 0);
                    int y = pw.get(Type.UNSIGNED_BYTE, 0);
                    int z = pw.get(Type.INT, 1);
                    int id = Byte.toUnsignedInt(pw.get(Type.BYTE, 0));
                    int meta = pw.get(Type.BYTE, 1);

                    final ChunkTracker tracker = pw.user().get(ChunkTracker.class);
                    if(!tracker.isChunkLoaded(x >> 4, z >> 4)) {
                        return;
                    }
                    tracker.setBlockId(x, y, z, id);
                    tracker.setBlockMetadata(x, y, z, meta);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_1.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Effect-ID

                map(Type.INT); // X-Position
                map(Type.BYTE); // Y-Position
                map(Type.INT); // Z-Position

                map(Type.INT); // Data

                handler(wrapper -> {
                    final int effectId = wrapper.get(Type.INT, 0);
                    int effectData = wrapper.get(Type.INT, 3);

                    if (effectId == 2001) {
                        final int blockId = effectId & 255;
                        final int blockData = effectData >> 8 & 255;

                        effectData = blockId + (blockData << 12);
                    }

                    wrapper.set(Type.INT, 3, effectData);
                });
            }
        });
    }

    @Override
    public void init(UserConnection connection) {
        super.init(connection);

        connection.put(new SplitterTracker(connection, ClientboundPackets1_1.values(), ClientboundLoginPackets1_2_4_5.values()));
    }
}
