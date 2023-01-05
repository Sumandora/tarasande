package de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4;

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
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntMap;
import com.viaversion.viaversion.libs.fastutil.objects.Object2IntOpenHashMap;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.base.*;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.florianmichael.viabeta.protocol.protocol1_6_4.storage.HandshakeStorage;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.rewriter.*;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.*;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.ClientboundPackets1_7_2;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.ServerboundPackets1_7_2;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.Chunk_1_7_6_10Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.impl.ChunkBulk_1_7_6_10Type;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.metadata.MetaType1_7_6;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.type.Type1_7_6_10;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import de.florianmichael.viabeta.ViaBeta;
import de.florianmichael.viabeta.api.model.IdAndData;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.provider.EncryptionProvider;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.impl.MetaType1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.type.Type1_6_4;
import de.florianmichael.viabeta.protocol.protocol1_8to1_7_6_10.Protocol1_8to1_7_6_10;
import de.florianmichael.viabeta.protocol.protocol1_7_6_10to1_7_2_5.provider.GameProfileFetcher;
import de.florianmichael.viabeta.pre_netty.viaversion.PreNettySplitter;

import java.util.List;
import java.util.logging.Level;

@SuppressWarnings({"DataFlowIssue", "deprecation"})
public class Protocol1_7_2_5to1_6_4 extends AbstractProtocol<ClientboundPackets1_6_4, ClientboundPackets1_7_2, ServerboundPackets1_6_4, ServerboundPackets1_7_2> {

    public static ItemRewriter ITEM_REWRITER = new ItemRewriter();

    public Protocol1_7_2_5to1_6_4() {
        super(ClientboundPackets1_6_4.class, ClientboundPackets1_7_2.class, ServerboundPackets1_6_4.class, ServerboundPackets1_7_2.class);
    }

    @Override
    protected void registerPackets() {
        this.registerClientbound(State.STATUS, ClientboundPackets1_6_4.DISCONNECT.getId(), ClientboundStatusPackets.STATUS_RESPONSE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String reason = wrapper.read(Type1_6_4.STRING); // reason
                    try {
                        final String[] motdParts = reason.split("\0");
                        final JsonObject rootObject = new JsonObject();
                        final JsonObject descriptionObject = new JsonObject();
                        final JsonObject playersObject = new JsonObject();
                        final JsonObject versionObject = new JsonObject();

                        descriptionObject.addProperty("text", motdParts[3]);
                        playersObject.addProperty("max", Integer.parseInt(motdParts[5]));
                        playersObject.addProperty("online", Integer.parseInt(motdParts[4]));
                        versionObject.addProperty("name", motdParts[2]);
                        versionObject.addProperty("protocol", Integer.parseInt(motdParts[1]));
                        rootObject.add("description", descriptionObject);
                        rootObject.add("players", playersObject);
                        rootObject.add("version", versionObject);

                        wrapper.write(Type.STRING, rootObject.toString());
                    } catch (Throwable e) {
                        ViaBeta.getPlatform().getLogger().log(Level.WARNING, "Could not parse 1.6.4 ping: " + reason, e);
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.SHARED_KEY.getId(), ClientboundLoginPackets.GAME_PROFILE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final ProtocolInfo info = wrapper.user().getProtocolInfo();
                    final ProtocolMetadataStorage protocolMetadata = wrapper.user().get(ProtocolMetadataStorage.class);

                    wrapper.read(Type.SHORT_BYTE_ARRAY); // shared secret
                    wrapper.read(Type.SHORT_BYTE_ARRAY); // verify token
                    wrapper.write(Type.STRING, info.getUuid().toString().replace("-", "")); // uuid
                    wrapper.write(Type.STRING, info.getUsername()); // username

                    if (protocolMetadata != null) {
                        if (!protocolMetadata.isSkippingEncryption()) {
                            Via.getManager().getProviders().get(EncryptionProvider.class).enableDecryption(wrapper.user());
                        }
                        info.setState(State.PLAY);
                        Via.getManager().getConnectionManager().onLoginSuccess(wrapper.user());
                        if (info.getPipeline().pipes().stream().allMatch(Via.getManager().getProtocolManager()::isBaseProtocol)) {
                            wrapper.user().setActive(false);
                        }
                        if (Via.getManager().isDebug()) {
                            ViaBeta.getPlatform().getLogger().log(Level.INFO, "{0} logged in with protocol {1}, Route: {2}", new Object[]{info.getUsername(), info.getProtocolVersion(), Joiner.on(", ").join(info.getPipeline().pipes(), ", ")});
                        }
                    }

                    final PacketWrapper respawn = PacketWrapper.create(ServerboundPackets1_6_4.CLIENT_STATUS, wrapper.user());
                    respawn.write(Type.BYTE, (byte) 0); // force respawn
                    respawn.sendToServer(Protocol1_7_2_5to1_6_4.class);
                });
            }
        });
        this.cancelClientbound(ClientboundPackets1_6_4.SHARED_KEY);
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.SERVER_AUTH_DATA.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // server hash
                map(Type.SHORT_BYTE_ARRAY); // public key
                map(Type.SHORT_BYTE_ARRAY); // verify token
                handler(wrapper -> {
                    final ProtocolMetadataStorage protocolMetadata = wrapper.user().get(ProtocolMetadataStorage.class);
                    final String serverHash = wrapper.get(Type.STRING, 0);
                    protocolMetadata.authenticate(!serverHash.equals("-"));
                });
            }
        });
        this.cancelClientbound(ClientboundPackets1_6_4.SERVER_AUTH_DATA);
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.DISCONNECT.getId(), ClientboundLoginPackets.LOGIN_DISCONNECT.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING, ChatComponentRewriter::toClient); // reason
            }
        });
        this.cancelClientbound(State.LOGIN, ClientboundPackets1_6_4.PLUGIN_MESSAGE.getId());
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.JOIN_GAME.getId(), ClientboundPackets1_6_4.JOIN_GAME.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    ViaBeta.getPlatform().getLogger().warning("Server skipped LOGIN state");
                    final PacketWrapper sharedKey = PacketWrapper.create(ClientboundPackets1_6_4.SHARED_KEY, wrapper.user());
                    sharedKey.write(Type.SHORT_BYTE_ARRAY, new byte[0]);
                    sharedKey.write(Type.SHORT_BYTE_ARRAY, new byte[0]);
                    wrapper.user().get(ProtocolMetadataStorage.class).skipEncryption(true);
                    sharedKey.send(BaseProtocol.class); // switch to play state
                    wrapper.user().get(ProtocolMetadataStorage.class).skipEncryption(false);

                    wrapper.send(BaseProtocol.class);
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    wrapper.user().get(PlayerInfoStorage.class).entityId = wrapper.get(Type.INT, 0);
                    final String terrainType = wrapper.read(Type1_6_4.STRING); // level type
                    final short gameType = wrapper.read(Type.BYTE); // game mode
                    final byte dimension = wrapper.read(Type.BYTE); // dimension id
                    final short difficulty = wrapper.read(Type.BYTE); // difficulty
                    wrapper.read(Type.BYTE); // world height
                    final short maxPlayers = wrapper.read(Type.BYTE); // max players

                    wrapper.write(Type.UNSIGNED_BYTE, gameType);
                    wrapper.write(Type.BYTE, dimension);
                    wrapper.write(Type.UNSIGNED_BYTE, difficulty);
                    wrapper.write(Type.UNSIGNED_BYTE, maxPlayers);
                    wrapper.write(Type.STRING, terrainType);
                });
                handler(wrapper -> {
                    final byte dimensionId = wrapper.get(Type.BYTE, 0);
                    wrapper.user().get(DimensionTracker.class).setDimension(dimensionId);
                    wrapper.user().get(ClientWorld.class).setEnvironment(dimensionId);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING, msg -> TranslationRewriter.toClient(ChatComponentRewriter.toClient(msg))); // message
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteRead(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.RESPAWN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // dimension id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // difficulty
                map(Type.BYTE, Type.UNSIGNED_BYTE); // gamemode
                read(Type.SHORT); // world height
                map(Type1_6_4.STRING, Type.STRING); // worldType
                handler(wrapper -> {
                    final int oldDim = wrapper.user().get(DimensionTracker.class).getDimensionId();
                    final int newDim = wrapper.get(Type.INT, 0);
                    wrapper.user().get(DimensionTracker.class).setDimension(newDim);
                    wrapper.user().get(ClientWorld.class).setEnvironment(newDim);
                    if (oldDim != newDim) {
                        wrapper.user().get(ChunkTracker.class).clear();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION_ONLY_ONGROUND, ClientboundPackets1_7_2.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    final boolean supportsFlags = wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_8to1_7_6_10.class);

                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posX); // x
                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posY + 1.62F); // y
                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posZ); // z
                    wrapper.write(Type.FLOAT, supportsFlags ? 0F : playerInfoStorage.yaw); // yaw
                    wrapper.write(Type.FLOAT, supportsFlags ? 0F : playerInfoStorage.pitch); // pitch
                    if (supportsFlags) {
                        wrapper.read(Type.BOOLEAN); // onGround
                        wrapper.write(Type.BYTE, (byte) 0b11111); // flags

                        wrapper.setPacketType(ClientboundPackets1_8.PLAYER_POSITION);
                        wrapper.send(Protocol1_8to1_7_6_10.class);
                        wrapper.cancel();
                    } else {
                        wrapper.passthrough(Type.BOOLEAN); // onGround
                    }

                    final PacketWrapper setVelocityToZero = PacketWrapper.create(ClientboundPackets1_7_2.ENTITY_VELOCITY, wrapper.user());
                    setVelocityToZero.write(Type.INT, playerInfoStorage.entityId); // entity id
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity x
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity y
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity z

                    if (!wrapper.isCancelled()) wrapper.send(Protocol1_7_2_5to1_6_4.class);
                    setVelocityToZero.send(Protocol1_7_2_5to1_6_4.class);
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION_ONLY_POSITION, ClientboundPackets1_7_2.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    final boolean supportsFlags = wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_8to1_7_6_10.class);

                    wrapper.passthrough(Type.DOUBLE); // x
                    wrapper.passthrough(Type.DOUBLE); // stance
                    wrapper.read(Type.DOUBLE); // y
                    wrapper.passthrough(Type.DOUBLE); // z
                    wrapper.write(Type.FLOAT, supportsFlags ? 0F : playerInfoStorage.yaw); // yaw
                    wrapper.write(Type.FLOAT, supportsFlags ? 0F : playerInfoStorage.pitch); // pitch
                    if (supportsFlags) {
                        wrapper.read(Type.BOOLEAN); // onGround
                        wrapper.write(Type.BYTE, (byte) 0b11000); // flags

                        wrapper.setPacketType(ClientboundPackets1_8.PLAYER_POSITION);
                        wrapper.send(Protocol1_8to1_7_6_10.class);
                        wrapper.cancel();
                    } else {
                        wrapper.passthrough(Type.BOOLEAN); // onGround
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION_ONLY_LOOK, ClientboundPackets1_7_2.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    final boolean supportsFlags = wrapper.user().getProtocolInfo().getPipeline().contains(Protocol1_8to1_7_6_10.class);

                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posX); // x
                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posY + 1.62F); // y
                    wrapper.write(Type.DOUBLE, supportsFlags ? 0D : playerInfoStorage.posZ); // z
                    wrapper.passthrough(Type.FLOAT); // yaw
                    wrapper.passthrough(Type.FLOAT); // pitch
                    if (supportsFlags) {
                        wrapper.read(Type.BOOLEAN); // onGround
                        wrapper.write(Type.BYTE, (byte) 0b111); // flags

                        wrapper.setPacketType(ClientboundPackets1_8.PLAYER_POSITION);
                        wrapper.send(Protocol1_8to1_7_6_10.class);
                        wrapper.cancel();
                    } else {
                        wrapper.passthrough(Type.BOOLEAN); // onGround
                    }

                    final PacketWrapper setVelocityToZero = PacketWrapper.create(ClientboundPackets1_7_2.ENTITY_VELOCITY, wrapper.user());
                    setVelocityToZero.write(Type.INT, playerInfoStorage.entityId); // entity id
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity x
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity y
                    setVelocityToZero.write(Type.SHORT, (short) 0); // velocity z

                    if (!wrapper.isCancelled()) wrapper.send(Protocol1_7_2_5to1_6_4.class);
                    setVelocityToZero.send(Protocol1_7_2_5to1_6_4.class);
                    wrapper.cancel();
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // stance
                read(Type.DOUBLE); // y
                map(Type.DOUBLE); // z
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                map(Type.BOOLEAN); // onGround
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.HELD_ITEM_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT, Type.BYTE); // slot
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.USE_BED, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    if (wrapper.read(Type.BYTE) != 0) wrapper.cancel();
                });
                map(Type1_7_6_10.POSITION_BYTE); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                handler(wrapper -> {
                    short animate = wrapper.read(Type.BYTE); // animation
                    if (animate == 0 || animate == 4) wrapper.cancel();
                    if (animate >= 1 && animate <= 3) {
                        animate--;
                    } else {
                        animate -= 2;
                    }
                    wrapper.write(Type.UNSIGNED_BYTE, animate);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                handler(wrapper -> {
                    final String name = wrapper.read(Type1_6_4.STRING); // name
                    final GameProfileFetcher gameProfileFetcher = Via.getManager().getProviders().get(GameProfileFetcher.class);
                    wrapper.write(Type.STRING, (ViaBeta.getConfig().isLegacySkinLoading() ? gameProfileFetcher.getMojangUUID(name) : gameProfileFetcher.getOfflineUUID(name)).toString().replace("-", "")); // uuid
                    wrapper.write(Type.STRING, name);
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // yaw
                map(Type.BYTE); // pitch
                handler(wrapper -> {
                    final Item currentItem = new DataItem(wrapper.read(Type.UNSIGNED_SHORT), (byte) 1, (short) 0, null); // item
                    ITEM_REWRITER.rewriteRead(currentItem);
                    wrapper.write(Type.SHORT, (short) currentItem.identifier());
                });
                map(Type1_6_4.METADATA_LIST, Type1_7_6_10.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_7_6_10.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.BYTE); // pitch
                map(Type.BYTE); // yaw
                map(Type.INT); // data
                handler(wrapper -> {
                    int data = wrapper.get(Type.INT, 3);
                    if (Entity1_10Types.getTypeFromId(wrapper.get(Type.BYTE, 0), true) == Entity1_10Types.ObjectType.FALLING_BLOCK.getType()) {
                        final int id = data & 0xFFFF;
                        final int metadata = data >> 16;
                        final IdAndData block = new IdAndData(id, metadata);
                        wrapper.user().get(ChunkTracker.class).remapBlockParticle(block);
                        data = (block.id & 0xFFFF) | block.data << 16;
                    }
                    wrapper.set(Type.INT, 3, data);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_MOB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
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
                map(Type1_6_4.METADATA_LIST, Type1_7_6_10.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_7_6_10.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type1_6_4.STRING, Type.STRING); // motive
                map(Type1_7_6_10.POSITION_INT); // position
                map(Type.INT); // rotation
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.SHORT); // count
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_METADATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                map(Type1_6_4.METADATA_LIST, Type1_7_6_10.METADATA_LIST); // metadata
                handler(wrapper -> rewriteMetadata(wrapper.get(Type1_7_6_10.METADATA_LIST, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_PROPERTIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // entity id
                handler(wrapper -> {
                    final int amount = wrapper.passthrough(Type.INT); // count
                    for (int i = 0; i < amount; i++) {
                        wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // id
                        wrapper.passthrough(Type.DOUBLE); // baseValue
                        final int modifierLength = wrapper.passthrough(Type.SHORT); // modifier count
                        for (int x = 0; x < modifierLength; x++) {
                            wrapper.passthrough(Type.UUID); // modifier uuid
                            wrapper.passthrough(Type.DOUBLE); // modifier amount
                            wrapper.passthrough(Type.BYTE); // modifier operation
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.CHUNK_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk chunk = wrapper.passthrough(new Chunk_1_7_6_10Type(wrapper.user().get(ClientWorld.class)));
                    wrapper.user().get(ChunkTracker.class).trackAndRemap(chunk);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // chunkX
                map(Type.INT); // chunkZ
                map(Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY); // blockChangeRecords
                handler(wrapper -> {
                    final int chunkX = wrapper.get(Type.INT, 0);
                    final int chunkZ = wrapper.get(Type.INT, 1);
                    final BlockChangeRecord[] blockChangeRecords = wrapper.get(Type1_7_6_10.BLOCK_CHANGE_RECORD_ARRAY, 0);
                    for (BlockChangeRecord record : blockChangeRecords) {
                        final int targetX = record.getSectionX() + (chunkX << 4);
                        final int targetY = record.getY(-1);
                        final int targetZ = record.getSectionZ() + (chunkZ << 4);
                        final IdAndData block = IdAndData.fromCompressedData(record.getBlockId());
                        final Position pos = new Position(targetX, targetY, targetZ);
                        wrapper.user().get(ChunkTracker.class).trackAndRemap(pos, block);
                        record.setBlockId(block.toCompressedData());
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_SHORT, Type.VAR_INT); // block id
                map(Type.UNSIGNED_BYTE); // block data
                handler(wrapper -> {
                    final Position pos = wrapper.get(Type1_7_6_10.POSITION_UBYTE, 0); // position
                    final int blockId = wrapper.get(Type.VAR_INT, 0); // block id
                    final int data = wrapper.get(Type.UNSIGNED_BYTE, 0); // block data
                    final IdAndData block = new IdAndData(blockId, data);
                    wrapper.user().get(ChunkTracker.class).trackAndRemap(pos, block);
                    wrapper.set(Type.VAR_INT, 0, block.id); // block id
                    wrapper.set(Type.UNSIGNED_BYTE, 0, (short) block.data); // block data
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.BYTE, Type.UNSIGNED_BYTE); // type
                map(Type.BYTE, Type.UNSIGNED_BYTE); // data
                map(Type.SHORT, Type.VAR_INT); // block id
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_BREAK_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type1_7_6_10.POSITION_INT); // position
                map(Type.BYTE); // progress
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.MAP_BULK_CHUNK, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final Chunk[] chunks = wrapper.passthrough(new ChunkBulk_1_7_6_10Type(wrapper.user().get(ClientWorld.class)));
                    for (Chunk chunk : chunks) {
                        wrapper.user().get(ChunkTracker.class).trackAndRemap(chunk);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.EXPLOSION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE, Type.FLOAT); // x
                map(Type.DOUBLE, Type.FLOAT); // y
                map(Type.DOUBLE, Type.FLOAT); // z
                map(Type.FLOAT); // radius
                map(Type.INT); // record count
                handler(wrapper -> {
                    final int x = wrapper.get(Type.FLOAT, 0).intValue();
                    final int y = wrapper.get(Type.FLOAT, 1).intValue();
                    final int z = wrapper.get(Type.FLOAT, 2).intValue();
                    final int recordCount = wrapper.get(Type.INT, 0);
                    final ChunkTracker chunkTracker = wrapper.user().get(ChunkTracker.class);
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
        this.registerClientbound(ClientboundPackets1_6_4.NAMED_SOUND, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String oldSound = wrapper.read(Type1_6_4.STRING); // sound
                    String newSound = SoundRewriter.map(oldSound);
                    if (oldSound.isEmpty()) newSound = "";
                    if (newSound == null) {
                        ViaBeta.getPlatform().getLogger().warning("Unable to map 1.6.4 sound '" + oldSound + "'");
                        newSound = "";
                    }
                    if (newSound.isEmpty()) {
                        wrapper.cancel();
                        return;
                    }
                    wrapper.write(Type.STRING, newSound);
                });
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
                map(Type.FLOAT); // volume
                map(Type.UNSIGNED_BYTE); // pitch
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // effect id
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.INT); // data
                map(Type.BOOLEAN); // server wide
                handler(wrapper -> {
                    final int effectId = wrapper.get(Type.INT, 0); // effect id
                    int data = wrapper.get(Type.INT, 1); // data
                    final boolean disableRelativeVolume = wrapper.get(Type.BOOLEAN, 0); // server wide

                    if (!disableRelativeVolume && effectId == 2001) { // block break effect
                        final ChunkTracker chunkTracker = wrapper.user().get(ChunkTracker.class);
                        final int blockID = data & 4095;
                        final int blockData = data >> 12 & 255;
                        final IdAndData block = new IdAndData(blockID, blockData);
                        chunkTracker.remapBlockParticle(block);
                        data = (block.id & 4095) | block.data << 12;

                        wrapper.set(Type.INT, 1, data);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PARTICLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // particle
                map(Type.FLOAT); // x
                map(Type.FLOAT); // y
                map(Type.FLOAT); // z
                map(Type.FLOAT); // offset x
                map(Type.FLOAT); // offset y
                map(Type.FLOAT); // offset z
                map(Type.FLOAT); // speed
                map(Type.INT); // amount
                handler(wrapper -> {
                    final String[] parts = wrapper.get(Type.STRING, 0).split("_", 3);
                    if (parts[0].equals("tilecrack")) {
                        parts[0] = "blockcrack";
                    }
                    if (parts[0].equals("blockcrack") || parts[0].equals("blockdust")) {
                        final int id = Integer.parseInt(parts[1]);
                        final int metadata = Integer.parseInt(parts[2]);
                        final IdAndData block = new IdAndData(id, metadata);
                        wrapper.user().get(ChunkTracker.class).remapBlockParticle(block);
                        parts[1] = String.valueOf(block.id);
                        parts[2] = String.valueOf(block.data);
                    }

                    wrapper.set(Type.STRING, 0, String.join("_", parts));
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.GAME_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // reason
                map(Type.BYTE, Type.FLOAT); // value
                handler(wrapper -> {
                    final short gameState = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (gameState == 1) {
                        final PacketWrapper startRain = PacketWrapper.create(ClientboundPackets1_7_2.GAME_EVENT, wrapper.user());
                        startRain.write(Type.UNSIGNED_BYTE, (short) 7);
                        startRain.write(Type.FLOAT, 1.0f);

                        wrapper.send(Protocol1_7_2_5to1_6_4.class);
                        startRain.send(Protocol1_7_2_5to1_6_4.class);
                        wrapper.cancel();
                    } else if (gameState == 2) {
                        final PacketWrapper stopRain = PacketWrapper.create(ClientboundPackets1_7_2.GAME_EVENT, wrapper.user());
                        stopRain.write(Type.UNSIGNED_BYTE, (short) 7);
                        stopRain.write(Type.FLOAT, 0.0f);

                        wrapper.send(Protocol1_7_2_5to1_6_4.class);
                        stopRain.send(Protocol1_7_2_5to1_6_4.class);
                        wrapper.cancel();
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // entity id
                map(Type.BYTE); // type id
                map(Type.INT); // x
                map(Type.INT); // y
                map(Type.INT); // z
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE); // window id
                map(Type.UNSIGNED_BYTE); // window type
                map(Type1_6_4.STRING, Type.STRING); // title
                map(Type.UNSIGNED_BYTE); // slots
                map(Type.BOOLEAN); // use provided title
                // more conditional data
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.CLOSE_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // window id
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SET_SLOT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // window id
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteRead(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // window id
                handler(wrapper -> {
                    final Item[] items = wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM_ARRAY); // items
                    for (Item item : items) {
                        ITEM_REWRITER.rewriteRead(item);
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type1_6_4.STRING, Type.STRING); // line 1
                map(Type1_6_4.STRING, Type.STRING); // line 2
                map(Type1_6_4.STRING, Type.STRING); // line 3
                map(Type1_6_4.STRING, Type.STRING); // line 4
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.MAP_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                read(Type.SHORT); // item id
                map(Type.SHORT, Type.VAR_INT); // map id
                map(Type.SHORT_BYTE_ARRAY); // data
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.BYTE, Type.UNSIGNED_BYTE); // type
                map(Type1_7_6_10.COMPRESSED_NBT); // data
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.OPEN_SIGN_EDITOR, new PacketRemapper() {
            @Override
            public void registerMap() {
                read(Type.BYTE); // always 0
                map(Type1_7_6_10.POSITION_INT); // position
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.STATISTICS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final StatisticsStorage statisticsStorage = wrapper.user().get(StatisticsStorage.class);
                    final int statId = wrapper.read(Type.INT); // statistic id
                    final int increment = wrapper.read(Type.INT); // increment
                    statisticsStorage.values.put(statId, statisticsStorage.values.get(statId) + increment);
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_INFO, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // name
                map(Type.BOOLEAN); // online
                map(Type.SHORT); // ping
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String completions = wrapper.read(Type1_6_4.STRING); // completions
                    final String[] completionsArray = completions.split("\0");
                    wrapper.write(Type.VAR_INT, completionsArray.length); // completions count
                    for (String s : completionsArray) {
                        wrapper.write(Type.STRING, s); // completion
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // name
                map(Type1_6_4.STRING, Type.STRING); // value
                map(Type.BYTE); // mode
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.UPDATE_SCORE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // name
                    final byte mode = wrapper.passthrough(Type.BYTE); // mode
                    if (mode == 0) {
                        wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // objective
                        wrapper.passthrough(Type.INT); // score
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.DISPLAY_SCOREBOARD, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // position
                map(Type1_6_4.STRING, Type.STRING); // name
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.TEAMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // name
                handler(wrapper -> {
                    final byte mode = wrapper.passthrough(Type.BYTE); // mode
                    if (mode == 0 || mode == 2) {
                        wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // display name
                        wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // prefix
                        wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // suffix
                        wrapper.passthrough(Type.BYTE); // flags
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        final int count = wrapper.passthrough(Type.SHORT); // count
                        for (int i = 0; i < count; i++) {
                            wrapper.write(Type.STRING, wrapper.read(Type1_6_4.STRING)); // player name
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING); // channel
                handler(wrapper -> {
                    final String channel = wrapper.get(Type.STRING, 0);
                    wrapper.passthrough(Type.SHORT); // length
                    if (channel.equals("MC|TrList")) {
                        wrapper.passthrough(Type.INT); // window id
                        final int count = wrapper.passthrough(Type.UNSIGNED_BYTE); // count
                        for (int i = 0; i < count; i++) {
                            ITEM_REWRITER.rewriteRead(wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM)); // item 1
                            ITEM_REWRITER.rewriteRead(wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM)); // item 3
                            if (wrapper.passthrough(Type.BOOLEAN)) { // has 3 items
                                ITEM_REWRITER.rewriteRead(wrapper.passthrough(Type1_7_6_10.COMPRESSED_ITEM)); // item 2
                            }
                            wrapper.passthrough(Type.BOOLEAN); // unavailable
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundPackets1_6_4.DISCONNECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_6_4.STRING, Type.STRING, ChatComponentRewriter::toClient);
            }
        });
        this.cancelClientbound(ClientboundPackets1_6_4.CREATIVE_INVENTORY_ACTION);

        this.registerServerbound(State.STATUS, ServerboundPackets1_6_4.SERVER_PING.getId(), ServerboundStatusPackets.STATUS_REQUEST.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final HandshakeStorage handshakeStorage = wrapper.user().get(HandshakeStorage.class);
                    final String ip = handshakeStorage.getHostname();
                    final int port = handshakeStorage.getPort();
                    wrapper.write(Type.UNSIGNED_BYTE, (short) 1); // always 1
                    wrapper.write(Type.UNSIGNED_BYTE, (short) 250); // packet id
                    wrapper.write(Type1_6_4.STRING, "MC|PingHost"); // channel
                    wrapper.write(Type.UNSIGNED_SHORT, 3 + 2 * ip.length() + 4); // length
                    wrapper.write(Type.UNSIGNED_BYTE, (short) (-wrapper.user().getProtocolInfo().getServerProtocolVersion() >> 2)); // protocol Id
                    wrapper.write(Type1_6_4.STRING, ip); // hostname
                    wrapper.write(Type.INT, port); // port
                });
            }
        });
        this.registerServerbound(State.STATUS, -1, ServerboundStatusPackets.PING_REQUEST.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.cancel();
                    final PacketWrapper pong = PacketWrapper.create(ClientboundStatusPackets.PONG_RESPONSE, wrapper.user());
                    pong.write(Type.LONG, wrapper.read(Type.LONG)); // start time
                    pong.send(Protocol1_7_2_5to1_6_4.class);
                });
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundPackets1_6_4.CLIENT_PROTOCOL.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final String name = wrapper.read(Type.STRING); // user name
                    final ProtocolInfo info = wrapper.user().getProtocolInfo();
                    final HandshakeStorage handshakeStorage = wrapper.user().get(HandshakeStorage.class);
                    final GameProfileFetcher gameProfileFetcher = Via.getManager().getProviders().get(GameProfileFetcher.class);
                    info.setUsername(name);
                    info.setUuid(ViaBeta.getConfig().isLegacySkinLoading() ? gameProfileFetcher.getMojangUUID(info.getUsername()) : gameProfileFetcher.getOfflineUUID(info.getUsername()));

                    wrapper.write(Type.UNSIGNED_BYTE, (short) (-info.getServerProtocolVersion() >> 2)); // protocol id
                    wrapper.write(Type1_6_4.STRING, name); // username
                    wrapper.write(Type1_6_4.STRING, handshakeStorage.getHostname()); // hostname
                    wrapper.write(Type.INT, handshakeStorage.getPort()); // port
                });
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundPackets1_6_4.SHARED_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId());
        this.registerServerbound(ServerboundPackets1_7_2.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, Type1_6_4.STRING); // message
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.INTERACT_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> wrapper.write(Type.INT, wrapper.user().get(PlayerInfoStorage.class).entityId)); // player id
                map(Type.INT); // entity id
                map(Type.BYTE); // mode
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLAYER_MOVEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> wrapper.user().get(PlayerInfoStorage.class).onGround = wrapper.get(Type.BOOLEAN, 0));
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                map(Type.DOUBLE); // y
                map(Type.DOUBLE); // stance
                map(Type.DOUBLE); // z
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    playerInfoStorage.posX = wrapper.get(Type.DOUBLE, 0);
                    playerInfoStorage.posY = wrapper.get(Type.DOUBLE, 1);
                    playerInfoStorage.posZ = wrapper.get(Type.DOUBLE, 3);
                    playerInfoStorage.onGround = wrapper.get(Type.BOOLEAN, 0);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLAYER_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // yaw
                map(Type.FLOAT); // pitch
                map(Type.BOOLEAN); // onGround
                handler(wrapper -> {
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    playerInfoStorage.yaw = wrapper.get(Type.FLOAT, 0);
                    playerInfoStorage.pitch = wrapper.get(Type.FLOAT, 1);
                    playerInfoStorage.onGround = wrapper.get(Type.BOOLEAN, 0);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {
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
                    final PlayerInfoStorage playerInfoStorage = wrapper.user().get(PlayerInfoStorage.class);
                    playerInfoStorage.posX = wrapper.get(Type.DOUBLE, 0);
                    playerInfoStorage.posY = wrapper.get(Type.DOUBLE, 1);
                    playerInfoStorage.posZ = wrapper.get(Type.DOUBLE, 3);
                    playerInfoStorage.yaw = wrapper.get(Type.FLOAT, 0);
                    playerInfoStorage.pitch = wrapper.get(Type.FLOAT, 1);
                    playerInfoStorage.onGround = wrapper.get(Type.BOOLEAN, 0);
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_UBYTE); // position
                map(Type.UNSIGNED_BYTE); // direction
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteWrite(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
                map(Type.UNSIGNED_BYTE); // offset x
                map(Type.UNSIGNED_BYTE); // offset y
                map(Type.UNSIGNED_BYTE); // offset z
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE); // windowId
                map(Type.SHORT); // slot
                map(Type.BYTE); // button
                map(Type.SHORT); // action
                map(Type.BYTE); // mode
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteWrite(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT); // slot
                map(Type1_7_6_10.COMPRESSED_ITEM); // item
                handler(wrapper -> ITEM_REWRITER.rewriteWrite(wrapper.get(Type1_7_6_10.COMPRESSED_ITEM, 0)));
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type1_7_6_10.POSITION_SHORT); // position
                map(Type.STRING, Type1_6_4.STRING); // line 1
                map(Type.STRING, Type1_6_4.STRING); // line 2
                map(Type.STRING, Type1_6_4.STRING); // line 3
                map(Type.STRING, Type1_6_4.STRING); // line 4
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, Type1_6_4.STRING); // text
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.CLIENT_SETTINGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, Type1_6_4.STRING); // language
                handler(wrapper -> {
                    byte renderDistance = wrapper.read(Type.BYTE); // render distance

                    if (renderDistance <= 2) {
                        renderDistance = 3; // TINY
                    } else if (renderDistance <= 4) {
                        renderDistance = 2; // SHORT
                    } else if (renderDistance <= 8) {
                        renderDistance = 1; // NORMAL
                    } else { // >= 16
                        renderDistance = 0; // FAR
                    }

                    wrapper.write(Type.BYTE, renderDistance);
                });
                handler(wrapper -> {
                    final byte chatVisibility = wrapper.read(Type.BYTE); // chat visibility
                    final boolean enableColors = wrapper.read(Type.BOOLEAN); // enable colors
                    final byte mask = (byte) (chatVisibility | (enableColors ? 1 : 0) << 3);
                    wrapper.write(Type.BYTE, mask); // mask
                });
                map(Type.BYTE); // difficulty
                map(Type.BOOLEAN); // show cape
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.CLIENT_STATUS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final int action = wrapper.read(Type.VAR_INT); // action

                    if (action == 1) { // Request Statistics
                        final Object2IntMap<String> loadedStatistics = new Object2IntOpenHashMap<>();
                        for (Int2IntMap.Entry entry : wrapper.user().get(StatisticsStorage.class).values.int2IntEntrySet()) {
                            final String key = StatisticRewriter.map(entry.getIntKey());
                            if (key == null) continue;
                            loadedStatistics.put(key, entry.getIntValue());
                        }

                        final PacketWrapper statistics = PacketWrapper.create(ClientboundPackets1_8.STATISTICS, wrapper.user());
                        statistics.write(Type.VAR_INT, loadedStatistics.size()); // count
                        for (Object2IntMap.Entry<String> entry : loadedStatistics.object2IntEntrySet()) {
                            statistics.write(Type.STRING, entry.getKey()); // statistic name
                            statistics.write(Type.VAR_INT, entry.getIntValue()); // statistic value
                        }
                        statistics.send(Protocol1_7_2_5to1_6_4.class);
                    }
                    if (action != 0) {
                        wrapper.cancel();
                        return;
                    }
                    wrapper.write(Type.BYTE, (byte) 1); // force respawn
                });
            }
        });
        this.registerServerbound(ServerboundPackets1_7_2.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING, Type1_6_4.STRING); // channel
                map(Type.SHORT); // length
                handler(wrapper -> {
                    final String channel = wrapper.get(Type1_6_4.STRING, 0);
                    final PacketWrapper lengthPacketWrapper = PacketWrapper.create(null, wrapper.user());
                    final ByteBuf lengthBuffer = Unpooled.buffer();

                    switch (channel) {
                        case "MC|BEdit":
                        case "MC|BSign":
                            final Item item = wrapper.read(Type1_7_6_10.COMPRESSED_ITEM); // book
                            ITEM_REWRITER.rewriteWrite(item);

                            lengthPacketWrapper.write(Type1_7_6_10.COMPRESSED_ITEM, item);
                            lengthPacketWrapper.writeToBuffer(lengthBuffer);

                            wrapper.set(Type.SHORT, 0, (short) lengthBuffer.readableBytes()); // length
                            wrapper.write(Type1_7_6_10.COMPRESSED_ITEM, item); // book
                            break;
                        case "MC|AdvCdm":
                            final byte type = wrapper.read(Type.BYTE); // command block type
                            if (type == 0) {
                                final int posX = wrapper.read(Type.INT); // x
                                final int posY = wrapper.read(Type.INT); // y
                                final int posZ = wrapper.read(Type.INT); // z
                                final String command = wrapper.read(Type.STRING); // command

                                lengthPacketWrapper.write(Type.INT, posX);
                                lengthPacketWrapper.write(Type.INT, posY);
                                lengthPacketWrapper.write(Type.INT, posZ);
                                lengthPacketWrapper.write(Type1_6_4.STRING, command);
                                lengthPacketWrapper.writeToBuffer(lengthBuffer);

                                wrapper.set(Type.SHORT, 0, (short) lengthBuffer.readableBytes()); // length
                                wrapper.write(Type.INT, posX); // x
                                wrapper.write(Type.INT, posY); // y
                                wrapper.write(Type.INT, posZ); // z
                                wrapper.write(Type1_6_4.STRING, command); // command
                            } else {
                                wrapper.cancel();
                            }
                            break;
                    }
                    lengthBuffer.release();
                });
            }
        });
    }

    private void rewriteMetadata(final List<Metadata> metadataList) {
        for (Metadata metadata : metadataList) {
            if (metadata.metaType().equals(MetaType1_6_4.Slot)) {
                ITEM_REWRITER.rewriteRead(metadata.value());
            }
            metadata.setMetaType(MetaType1_7_6.byId(metadata.metaType().typeId()));
        }
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.require(EncryptionProvider.class);
    }

    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new PreNettySplitter(userConnection, Protocol1_7_2_5to1_6_4.class, ClientboundPackets1_6_4::getPacket));

        userConnection.put(new PlayerInfoStorage(userConnection));
        userConnection.put(new StatisticsStorage(userConnection));
        userConnection.put(new DimensionTracker(userConnection));
        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
        userConnection.put(new ChunkTracker(userConnection));

        userConnection.getChannel().pipeline().addFirst(new ChannelOutboundHandlerAdapter() {
            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                if (userConnection.getProtocolInfo().getState().equals(State.PLAY) && ctx.channel().isWritable() && userConnection.get(PlayerInfoStorage.class).entityId != -1) {
                    final PacketWrapper disconnect = PacketWrapper.create(ServerboundPackets1_6_4.DISCONNECT, userConnection);
                    disconnect.write(Type1_6_4.STRING, "Quitting"); // reason
                    disconnect.sendToServer(Protocol1_7_2_5to1_6_4.class);
                    Thread.sleep(50); // Wait for the packet to arrive at the server
                }

                super.close(ctx, promise);
            }
        });
    }

}
