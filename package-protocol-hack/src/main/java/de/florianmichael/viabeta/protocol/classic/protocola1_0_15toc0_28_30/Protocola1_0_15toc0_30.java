package de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandlers;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.data.BlockList1_6;
import de.florianmichael.viabeta.api.model.ChunkCoord;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_16_2toa1_0_15.ClientboundPacketsa1_0_15;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_16_2toa1_0_15.ServerboundPacketsa1_0_15;
import de.florianmichael.viabeta.protocol.alpha.protocola1_0_17_1_0_17_4toa1_0_16_2.storage.TimeLockStorage;
import de.florianmichael.viabeta.protocol.alpha.protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4;
import de.florianmichael.viabeta.protocol.alpha.protocola1_2_0_1_2_1_1toa1_1_0_1_1_2_1.ClientboundPacketsa1_1_0;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.provider.AlphaInventoryProvider;
import de.florianmichael.viabeta.protocol.alpha.protocolb1_0_1_1_1toa1_2_3_5_1_2_6.storage.AlphaInventoryTracker;
import de.florianmichael.viabeta.protocol.beta.protocol1_0_0_1tob1_8_0_1.ClientboundPacketsb1_8;
import de.florianmichael.viabeta.protocol.beta.protocolb1_3_0_1tob1_2_0_2.storage.BlockDigStorage;
import de.florianmichael.viabeta.protocol.beta.protocolb1_6_0_6tob1_5_0_2.Protocolb1_6_0_6tob1_5_0_2;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.ClientboundPacketsb1_7;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.Protocolb1_8_0_1tob1_7_0_3;
import de.florianmichael.viabeta.protocol.beta.protocolb1_8_0_1tob1_7_0_3.type.Typeb1_7_0_3;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.data.ClassicBlocks;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.model.ClassicLevel;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicMPPassProvider;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.provider.ClassicWorldHeightProvider;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.storage.*;
import de.florianmichael.viabeta.protocol.classic.protocola1_0_15toc0_28_30.type.Typec0_30;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.Protocolc0_30toc0_30cpe;
import de.florianmichael.viabeta.protocol.classic.protocolc0_28_30toc0_28_30cpe.storage.ExtBlockPermissionsStorage;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;

@SuppressWarnings("DataFlowIssue")
public class Protocola1_0_15toc0_30 extends AbstractProtocol<ClientboundPacketsc0_28, ClientboundPacketsa1_0_15, ServerboundPacketsc0_28, ServerboundPacketsa1_0_15> {

    public Protocola1_0_15toc0_30() {
        super(ClientboundPacketsc0_28.class, ClientboundPacketsa1_0_15.class, ServerboundPacketsc0_28.class, ServerboundPacketsa1_0_15.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.LOGIN, ClientboundPacketsc0_28.DISCONNECT.getId(), ClientboundPacketsa1_0_15.DISCONNECT.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Typec0_30.STRING, Typeb1_7_0_3.STRING, s -> s.replace("&", "§")); // reason
            }
        });

        this.registerClientbound(ClientboundPacketsc0_28.JOIN_GAME, new PacketHandlers() {
            @Override
            public void register() {
                read(Type.BYTE); // protocol id
                handler(wrapper -> {
                    final String title = wrapper.read(Typec0_30.STRING).replace("&", "§"); // title
                    final String motd = wrapper.read(Typec0_30.STRING).replace("&", "§"); // motd
                    final byte opLevel = wrapper.read(Type.BYTE); // op level
                    wrapper.user().put(new ClassicServerTitleStorage(wrapper.user(), title, motd));
                    wrapper.user().get(ClassicOpLevelStorage.class).setOpLevel(opLevel);

                    wrapper.write(Type.INT, wrapper.user().getProtocolInfo().getUsername().hashCode()); // entity id
                    wrapper.write(Typeb1_7_0_3.STRING, wrapper.user().getProtocolInfo().getUsername()); // username
                    wrapper.write(Typeb1_7_0_3.STRING, ""); // password

                    if (wrapper.user().has(ClassicLevelStorage.class)) { // World already loaded
                        wrapper.cancel();
                    }

                    if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_8to1_7_6_10.class)) {
                        final PacketWrapper tabList = PacketWrapper.create(ClientboundPackets1_8.TAB_LIST, wrapper.user());
                        tabList.write(Type.STRING, Protocol1_8to1_7_6_10.LEGACY_TO_JSON.transform(wrapper, "§6" + title + "\n")); // header
                        tabList.write(Type.STRING, Protocol1_8to1_7_6_10.LEGACY_TO_JSON.transform(wrapper, "\n§b" + motd)); // footer
                        tabList.send(Protocol1_8to1_7_6_10.class);
                    }

                    final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                    classicProgressStorage.progress = 1;
                    classicProgressStorage.upperBound = 2;
                    classicProgressStorage.status = "Waiting for server...";
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.LEVEL_INIT, null, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.cancel();

                    if (wrapper.user().has(ClassicLevelStorage.class)) { // Switch world
                        if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocolb1_6_0_6tob1_5_0_2.class)) {
                            final PacketWrapper fakeRespawn = PacketWrapper.create(ClientboundPacketsb1_7.RESPAWN, wrapper.user());
                            fakeRespawn.write(Type.BYTE, (byte) -1); // dimension id
                            fakeRespawn.send(Protocolb1_6_0_6tob1_5_0_2.class);
                            final PacketWrapper respawn = PacketWrapper.create(ClientboundPacketsb1_7.RESPAWN, wrapper.user());
                            respawn.write(Type.BYTE, (byte) 0); // dimension id
                            respawn.send(Protocolb1_6_0_6tob1_5_0_2.class);
                            wrapper.user().get(ClassicPositionTracker.class).spawned = false;
                        }
                    }
                    if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocolb1_8_0_1tob1_7_0_3.class)) {
                        final PacketWrapper gameEvent = PacketWrapper.create(ClientboundPacketsb1_8.GAME_EVENT, wrapper.user());
                        gameEvent.write(Type.BYTE, (byte) 3); // reason (game mode)
                        gameEvent.write(Type.BYTE, (byte) 1); // value (creative)
                        gameEvent.send(Protocolb1_8_0_1tob1_7_0_3.class);
                    }

                    wrapper.user().put(new ClassicLevelStorage(wrapper.user()));

                    final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                    classicProgressStorage.progress = 2;
                    classicProgressStorage.upperBound = 2;
                    classicProgressStorage.status = "Waiting for server...";
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.LEVEL_DATA, null, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final short partSize = wrapper.read(Type.SHORT); // part size
                    final byte[] data = wrapper.read(Typec0_30.BYTE_ARRAY); // data
                    final byte progress = wrapper.read(Type.BYTE); // progress

                    wrapper.user().get(ClassicLevelStorage.class).addDataPart(data, partSize);

                    final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                    classicProgressStorage.upperBound = 100;
                    classicProgressStorage.progress = progress;
                    classicProgressStorage.status = "Receiving level... §7" + progress + "%";
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.LEVEL_FINALIZE, null, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final short sizeX = wrapper.read(Type.SHORT);
                    final short sizeY = wrapper.read(Type.SHORT);
                    final short sizeZ = wrapper.read(Type.SHORT);

                    final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    final short maxChunkSectionCount = Via.getManager().getProviders().get(ClassicWorldHeightProvider.class).getMaxChunkSectionCount(wrapper.user());

                    classicProgressStorage.upperBound = 2;
                    classicProgressStorage.progress = 0;
                    classicProgressStorage.status = "Finishing level... §7Decompressing";
                    levelStorage.finish(sizeX, sizeY, sizeZ);
                    levelStorage.sendChunk(new ChunkCoord(0, 0));

                    if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_8to1_7_6_10.class)) {
                        final PacketWrapper worldBorder = PacketWrapper.create(ClientboundPackets1_8.WORLD_BORDER, wrapper.user());
                        worldBorder.write(Type.VAR_INT, 3); // action (INITIALIZE)
                        worldBorder.write(Type.DOUBLE, sizeX / 2D); // centerX
                        worldBorder.write(Type.DOUBLE, sizeZ / 2D); // centerZ
                        worldBorder.write(Type.DOUBLE, 0.0D); // diameter
                        worldBorder.write(Type.DOUBLE, (double) Math.max(sizeX, sizeZ)); // target size
                        worldBorder.write(Type.VAR_LONG, 0L); // time until target size
                        worldBorder.write(Type.VAR_INT, Math.max(sizeX, sizeZ)); // size
                        worldBorder.write(Type.VAR_INT, 0); // warning distance
                        worldBorder.write(Type.VAR_INT, 0); // warning time
                        worldBorder.send(Protocol1_8to1_7_6_10.class);
                    }

                    sendChatMessage(wrapper.user(), "§aWorld dimensions: §6" + sizeX + "§ax§6" + sizeY + "§ax§6" + sizeZ);
                    if (sizeY > (maxChunkSectionCount << 4)) {
                        sendChatMessage(wrapper.user(), "§cThis server has a world higher than " + (maxChunkSectionCount << 4) + " blocks! Expect world errors");
                    }

                    classicProgressStorage.progress = 1;
                    classicProgressStorage.status = "Finishing level... §7Waiting for server";
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.BLOCK_CHANGE, new PacketHandlers() {
            @Override
            public void register() {
                map(Typec0_30.POSITION, Type1_7_6_10.POSITION_UBYTE); // position
                handler(wrapper -> {
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    if (levelStorage == null || !levelStorage.hasReceivedLevel()) {
                        wrapper.cancel();
                        return;
                    }
                    final ClassicBlockRemapper remapper = wrapper.user().get(ClassicBlockRemapper.class);
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0);
                    final byte blockId = wrapper.read(Type.BYTE); // block id
                    levelStorage.getClassicLevel().setBlock(pos, blockId);
                    if (!levelStorage.isChunkLoaded(pos)) {
                        wrapper.cancel();
                        return;
                    }
                    final IdAndData mappedBlock = remapper.getMapper().get(blockId);
                    wrapper.write(Type.UNSIGNED_BYTE, (short) mappedBlock.id); // block id
                    wrapper.write(Type.UNSIGNED_BYTE, (short) mappedBlock.data); // block data
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.SPAWN_PLAYER, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
                map(Typec0_30.STRING, Typeb1_7_0_3.STRING, n -> n.replace("&", "§")); // username
                map(Type.SHORT, Type.INT); // x
                map(Type.SHORT, Type.INT); // y
                map(Type.SHORT, Type.INT); // z
                map(Type.BYTE, Type.BYTE, yaw -> (byte) (yaw + 128)); // yaw
                map(Type.BYTE); // pitch
                create(Type.UNSIGNED_SHORT, 0); // item
                handler(wrapper -> {
                    if (wrapper.get(Type.INT, 0) < 0) { // client player
                        wrapper.cancel();
                        final int x = wrapper.get(Type.INT, 1);
                        final int y = wrapper.get(Type.INT, 2);
                        final int z = wrapper.get(Type.INT, 3);
                        final byte yaw = wrapper.get(Type.BYTE, 0);
                        final byte pitch = wrapper.get(Type.BYTE, 1);

                        final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                        classicProgressStorage.progress = 2;
                        classicProgressStorage.status = "Finishing level... §7Loading spawn chunks";

                        final ClassicPositionTracker classicPositionTracker = wrapper.user().get(ClassicPositionTracker.class);
                        classicPositionTracker.posX = (x / 32.0F);
                        classicPositionTracker.stance = (y / 32.0F) + 0.714F;
                        classicPositionTracker.posZ = (z / 32.0F);
                        classicPositionTracker.yaw = yaw * 360 / 256.0F;
                        classicPositionTracker.pitch = pitch * 360 / 256.0F;
                        wrapper.user().get(ClassicLevelStorage.class).sendChunks(classicPositionTracker.getChunkPosition(), 1);

                        if (wrapper.user().getProtocolInfo().getPipeline().contains(Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.class)) {
                            final PacketWrapper spawnPosition = PacketWrapper.create(ClientboundPacketsa1_1_0.SPAWN_POSITION, wrapper.user());
                            spawnPosition.write(Type1_7_6_10.POSITION_INT, new Position((int) classicPositionTracker.posX, (int) (classicPositionTracker.stance), (int) classicPositionTracker.posZ));
                            spawnPosition.send(Protocola1_1_0_1_1_2_1toa1_0_17_1_0_17_4.class);
                        }

                        final PacketWrapper playerPosition = PacketWrapper.create(ClientboundPacketsa1_0_15.PLAYER_POSITION, wrapper.user());
                        playerPosition.write(Type.DOUBLE, classicPositionTracker.posX); // x
                        playerPosition.write(Type.DOUBLE, classicPositionTracker.stance); // stance
                        playerPosition.write(Type.DOUBLE, classicPositionTracker.stance - 1.62F); // y
                        playerPosition.write(Type.DOUBLE, classicPositionTracker.posZ); // z
                        playerPosition.write(Type.FLOAT, classicPositionTracker.yaw); // yaw
                        playerPosition.write(Type.FLOAT, classicPositionTracker.pitch); // pitch
                        playerPosition.write(Type.BOOLEAN, true); // onGround
                        playerPosition.send(Protocola1_0_15toc0_30.class);
                        classicPositionTracker.spawned = true;
                    } else {
                        wrapper.set(Type.INT, 2, wrapper.get(Type.INT, 2) - Float.valueOf(1.62F * 32).intValue());
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.ENTITY_TELEPORT, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
                map(Type.SHORT, Type.INT); // x
                map(Type.SHORT, Type.INT); // y
                map(Type.SHORT, Type.INT); // z
                map(Type.BYTE, Type.BYTE, yaw -> (byte) (yaw + 128)); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    if (wrapper.get(Type.INT, 0) < 0) { // client player
                        wrapper.set(Type.INT, 2, wrapper.get(Type.INT, 2) - 29);
                        wrapper.set(Type.INT, 0, wrapper.user().getProtocolInfo().getUsername().hashCode());
                    } else {
                        wrapper.set(Type.INT, 2, wrapper.get(Type.INT, 2) - Float.valueOf(1.62F * 32).intValue());
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.ENTITY_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
                map(Type.BYTE, Type.BYTE, yaw -> (byte) (yaw + 128)); // yaw
                map(Type.BYTE); // pitch
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.ENTITY_POSITION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
                map(Type.BYTE); // x
                map(Type.BYTE); // y
                map(Type.BYTE); // z
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.ENTITY_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
                map(Type.BYTE, Type.BYTE, yaw -> (byte) (yaw + 128)); // yaw
                map(Type.BYTE); // pitch
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.DESTROY_ENTITIES, new PacketHandlers() {
            @Override
            public void register() {
                map(Type.BYTE, Type.INT); // entity id
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                handler(packetWrapper -> {
                    final byte senderId = packetWrapper.read(Type.BYTE); // sender id
                    String message = packetWrapper.read(Typec0_30.STRING).replace("&", "§"); // message
                    if (senderId < 0) {
                        message = "§e" + message;
                    }
                    packetWrapper.write(Typeb1_7_0_3.STRING, message);
                });
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.DISCONNECT, new PacketHandlers() {
            @Override
            public void register() {
                map(Typec0_30.STRING, Typeb1_7_0_3.STRING, s -> s.replace("&", "§")); // reason
            }
        });
        this.registerClientbound(ClientboundPacketsc0_28.OP_LEVEL_UPDATE, null, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final byte opLevel = wrapper.read(Type.BYTE); // op level
                    wrapper.user().get(ClassicOpLevelStorage.class).setOpLevel(opLevel);
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundPacketsc0_28.LOGIN.getId(), ServerboundPacketsa1_0_15.LOGIN.getId(), new PacketHandlers() {
            @Override
            public void register() {
                map(Type.INT, Type.BYTE); // protocol id
                map(Typeb1_7_0_3.STRING, Typec0_30.STRING); // username
                read(Typeb1_7_0_3.STRING); // password
                handler(wrapper -> {
                    wrapper.write(Typec0_30.STRING, Via.getManager().getProviders().get(ClassicMPPassProvider.class).getMpPass(wrapper.user())); // mp pass
                    wrapper.write(Type.BYTE, (byte) 0); // op level

                    final ClassicProgressStorage classicProgressStorage = wrapper.user().get(ClassicProgressStorage.class);
                    classicProgressStorage.upperBound = 2;
                    classicProgressStorage.status = "Logging in...";
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.CHAT_MESSAGE, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final String message = wrapper.read(Typeb1_7_0_3.STRING); // message
                    wrapper.write(Type.BYTE, (byte) 0); // sender id
                    wrapper.write(Typec0_30.STRING, message); // message
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_MOVEMENT, ServerboundPacketsc0_28.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                read(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final ClassicPositionTracker positionTracker = wrapper.user().get(ClassicPositionTracker.class);
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    positionTracker.writeToPacket(wrapper);
                    if (levelStorage != null) levelStorage.tickChunks(positionTracker.getChunkPosition());
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_POSITION, ServerboundPacketsc0_28.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final ClassicPositionTracker positionTracker = wrapper.user().get(ClassicPositionTracker.class);
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    positionTracker.posX = wrapper.read(Type.DOUBLE); // x
                    wrapper.read(Type.DOUBLE); // y
                    positionTracker.stance = wrapper.read(Type.DOUBLE); // stance
                    positionTracker.posZ = wrapper.read(Type.DOUBLE); // z
                    wrapper.read(Type.BOOLEAN); // onGround

                    positionTracker.writeToPacket(wrapper);
                    if (levelStorage != null) levelStorage.tickChunks(positionTracker.getChunkPosition());
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_ROTATION, ServerboundPacketsc0_28.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final ClassicPositionTracker positionTracker = wrapper.user().get(ClassicPositionTracker.class);
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    positionTracker.yaw = wrapper.read(Type.FLOAT); // yaw
                    positionTracker.pitch = wrapper.read(Type.FLOAT); // pitch
                    wrapper.read(Type.BOOLEAN); // onGround

                    positionTracker.writeToPacket(wrapper);
                    if (levelStorage != null) levelStorage.tickChunks(positionTracker.getChunkPosition());
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_POSITION_AND_ROTATION, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final ClassicPositionTracker positionTracker = wrapper.user().get(ClassicPositionTracker.class);
                    final ClassicLevelStorage levelStorage = wrapper.user().get(ClassicLevelStorage.class);
                    positionTracker.posX = wrapper.read(Type.DOUBLE); // x
                    wrapper.read(Type.DOUBLE); // y
                    positionTracker.stance = wrapper.read(Type.DOUBLE); // stance
                    positionTracker.posZ = wrapper.read(Type.DOUBLE); // z
                    positionTracker.yaw = wrapper.read(Type.FLOAT); // yaw
                    positionTracker.pitch = wrapper.read(Type.FLOAT); // pitch
                    wrapper.read(Type.BOOLEAN); // onGround

                    positionTracker.writeToPacket(wrapper);
                    if (levelStorage != null) levelStorage.tickChunks(positionTracker.getChunkPosition());
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_DIGGING, ServerboundPacketsc0_28.PLAYER_BLOCK_PLACEMENT, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    wrapper.user().getStoredObjects().remove(BlockDigStorage.class);
                    final ClassicLevel level = wrapper.user().get(ClassicLevelStorage.class).getClassicLevel();
                    final ClassicOpLevelStorage opTracker = wrapper.user().get(ClassicOpLevelStorage.class);
                    final boolean extendedVerification = wrapper.user().has(ExtBlockPermissionsStorage.class);

                    final short status = wrapper.read(Type.UNSIGNED_BYTE); // status
                    final Position pos = wrapper.read(Type1_7_6_10.POSITION_UBYTE); // position
                    wrapper.read(Type.UNSIGNED_BYTE); // direction
                    final int blockId = level.getBlock(pos);

                    final boolean hasCreative = wrapper.user().getProtocolInfo().getPipeline().contains(Protocolb1_8_0_1tob1_7_0_3.class);
                    if ((status == 0 && hasCreative) || (status == 2 && !hasCreative)) {
                        if (!extendedVerification && blockId == ClassicBlocks.BEDROCK && opTracker.getOpLevel() < 100) {
                            wrapper.cancel();
                            sendChatMessage(wrapper.user(), "§cOnly op players can break bedrock!");
                            sendBlockChange(wrapper.user(), pos, new IdAndData(BlockList1_6.bedrock.blockID, 0));
                            return;
                        }
                        if (!extendedVerification) {
                            level.setBlock(pos, ClassicBlocks.AIR);
                            sendBlockChange(wrapper.user(), pos, new IdAndData(0, 0));
                        }

                        wrapper.write(Typec0_30.POSITION, pos); // position
                        wrapper.write(Type.BOOLEAN, false); // place block
                        wrapper.write(Type.BYTE, (byte) ClassicBlocks.STONE); // block id
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerServerbound(ServerboundPacketsa1_0_15.PLAYER_BLOCK_PLACEMENT, new PacketHandlers() {
            @Override
            public void register() {
                handler(wrapper -> {
                    final ClassicLevel level = wrapper.user().get(ClassicLevelStorage.class).getClassicLevel();
                    final ClassicBlockRemapper remapper = wrapper.user().get(ClassicBlockRemapper.class);
                    final boolean extendedVerification = wrapper.user().has(ExtBlockPermissionsStorage.class);

                    wrapper.read(Type.SHORT); // item id (is useless because it has no item damage)
                    Position pos = wrapper.read(Type1_7_6_10.POSITION_UBYTE); // position
                    final short direction = wrapper.read(Type.UNSIGNED_BYTE); // direction

                    final Item item = Via.getManager().getProviders().get(AlphaInventoryProvider.class).getHandItem(wrapper.user());
                    if (item == null || direction == 255) {
                        wrapper.cancel();
                        return;
                    }
                    BlockFace face;
                    switch (direction) {
                        case 0 -> face = BlockFace.BOTTOM;
                        case 2 -> face = BlockFace.NORTH;
                        case 3 -> face = BlockFace.SOUTH;
                        case 4 -> face = BlockFace.WEST;
                        case 5 -> face = BlockFace.EAST;
                        default -> face = BlockFace.TOP;
                    }
                    pos = pos.getRelative(face);

                    if (pos.y() >= level.getSizeY()) {
                        wrapper.cancel();
                        sendChatMessage(wrapper.user(), "§cHeight limit for building is " + level.getSizeY() + " blocks");
                        sendBlockChange(wrapper.user(), pos, new IdAndData(0, 0));
                        return;
                    }

                    final byte classicBlock = (byte) remapper.getReverseMapper().getInt(new IdAndData(item.identifier(), item.data() & 15));

                    if (!extendedVerification) {
                        level.setBlock(pos, classicBlock);
                        sendBlockChange(wrapper.user(), pos, remapper.getMapper().get(classicBlock));
                    }

                    wrapper.write(Typec0_30.POSITION, pos); // position
                    wrapper.write(Type.BOOLEAN, true); // place block
                    wrapper.write(Type.BYTE, classicBlock); // block id
                });
            }
        });
        this.cancelServerbound(ServerboundPacketsa1_0_15.KEEP_ALIVE);
        this.cancelServerbound(ServerboundPacketsa1_0_15.HELD_ITEM_CHANGE);
        this.cancelServerbound(ServerboundPacketsa1_0_15.ANIMATION);
        this.cancelServerbound(ServerboundPacketsa1_0_15.SPAWN_ITEM);
        this.cancelServerbound(ServerboundPacketsa1_0_15.DISCONNECT);
    }

    private void sendChatMessage(final UserConnection user, final String msg) throws Exception {
        final PacketWrapper message = PacketWrapper.create(ClientboundPacketsa1_0_15.CHAT_MESSAGE, user);
        message.write(Typeb1_7_0_3.STRING, ViaBeta.PREFIX_1_7 + msg); // message
        message.send(Protocola1_0_15toc0_30.class);
    }

    private void sendBlockChange(final UserConnection user, final Position pos, final IdAndData block) throws Exception {
        final PacketWrapper blockChange = PacketWrapper.create(ClientboundPacketsa1_0_15.BLOCK_CHANGE, user);
        blockChange.write(Type1_7_6_10.POSITION_UBYTE, pos); // position
        blockChange.write(Type.UNSIGNED_BYTE, (short) block.id); // block id
        blockChange.write(Type.UNSIGNED_BYTE, (short) block.data); // block data
        blockChange.send(Protocola1_0_15toc0_30.class);
    }

    @Override
    public void register(ViaProviders providers) {
        providers.register(ClassicWorldHeightProvider.class, new ClassicWorldHeightProvider());
        providers.register(ClassicMPPassProvider.class, new ClassicMPPassProvider());
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new PreNettySplitter(userConnection, Protocola1_0_15toc0_30.class, ClientboundPacketsc0_28::getPacket));

        userConnection.put(new ClassicPositionTracker(userConnection));
        userConnection.put(new ClassicOpLevelStorage(userConnection));
        userConnection.put(new ClassicProgressStorage(userConnection));
        userConnection.put(new ClassicBlockRemapper(userConnection, i -> ClassicBlocks.MAPPING.get(i), o -> {
            int block = ClassicBlocks.REVERSE_MAPPING.getInt(o);

            if (!userConnection.getProtocolInfo().getPipeline().contains(Protocolc0_30toc0_30cpe.class)) {
                if (block == ClassicBlocks.GRASS) block = ClassicBlocks.DIRT;
                else if (block == ClassicBlocks.BEDROCK) block = ClassicBlocks.STONE;
                else if (block == ClassicBlocks.STATIONARY_WATER) block = ClassicBlocks.BLUE_WOOL;
                else if (block == ClassicBlocks.STATIONARY_LAVA) block = ClassicBlocks.ORANGE_WOOL;
            }

            return block;
        }));

        if (userConnection.has(AlphaInventoryTracker.class)) userConnection.get(AlphaInventoryTracker.class).setCreativeMode(true);
        if (userConnection.has(TimeLockStorage.class)) userConnection.get(TimeLockStorage.class).setTime(6000L);
    }
}
