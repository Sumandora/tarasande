/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord1_8;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.TypeRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueReader;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.HandItemProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.Chunk1_8Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.ChunkBulk1_8Type;
import com.viaversion.viaversion.util.ChatColorUtil;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.api.item.LegacyItemRewriter;
import de.florianmichael.vialegacy.api.material.MaterialReplacement;
import de.florianmichael.vialegacy.api.metadata.LegacyMetadataRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.entity.EntityOffsets;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.item.ItemRewriter1_8_0_9to1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.item.MaterialReplacement1_8_0_9To1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.metadata.MetadataRewriter1_8_0_9to1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.model.SkinProperty;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.model.TeamsEntry;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.particle.Particles1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.storage.*;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.Chunk1_7_6_10Type;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.ChunkBulk1_7_6_10Type;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.impl.CustomString1_7_6_10Type;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("RedundantSuppression")
public class Protocol1_8_0_9to1_7_6_10 extends EnZaProtocol<ClientboundPackets1_7_6_10, ClientboundPackets1_8, ServerboundPackets1_7_6_10, ServerboundPackets1_8> {

    private final MaterialReplacement materialReplacement = new MaterialReplacement1_8_0_9To1_7_6_10();
    private final LegacyItemRewriter<Protocol1_8_0_9to1_7_6_10> itemRewriter = new ItemRewriter1_8_0_9to1_7_6_10(this);
    private final LegacyMetadataRewriter<Protocol1_8_0_9to1_7_6_10> metadataRewriter = new MetadataRewriter1_8_0_9to1_7_6_10(this);

    public final ValueReader<Position> xyzToPosition = packetWrapper -> {
        final int x = packetWrapper.read(Type.INT);
        final int y = packetWrapper.read(Type.INT);
        final int z = packetWrapper.read(Type.INT);

        return new Position(x, y, z);
    };

    public Protocol1_8_0_9to1_7_6_10() {
        super(ClientboundPackets1_7_6_10.class, ClientboundPackets1_8.class, ServerboundPackets1_7_6_10.class, ServerboundPackets1_8.class);
    }

    @Override
    protected void registerPackets() {
        this.cancelServerbound(ServerboundPackets1_8.SPECTATE);
        this.cancelServerbound(ServerboundPackets1_8.RESOURCE_PACK_STATUS);

        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Server Hash

                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
            }
        });

        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.GAME_PROFILE.getId(), ClientboundLoginPackets.GAME_PROFILE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                map(Type.STRING);
                handler(pw -> {
                    final GameProfileTracker gameProfileTracker = pw.user().get(GameProfileTracker.class);

                    if (gameProfileTracker != null) {
                        gameProfileTracker.setUuid(pw.get(Type.STRING, 0));
                        gameProfileTracker.setName(pw.get(Type.STRING, 1));
                    }
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.KEEP_ALIVE, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Time
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity ID
                map(Type.UNSIGNED_BYTE); // GameMode
                map(Type.BYTE); // Dimension
                map(Type.UNSIGNED_BYTE); // Difficulty
                map(Type.UNSIGNED_BYTE); // Max Players
                map(Type.STRING); // Level Type

                handler((pw) -> {
                    final GameProfileTracker gameProfileTracker = pw.user().get(GameProfileTracker.class);

                    final PacketWrapper playerInfo = PacketWrapper.create(ClientboundPackets1_7_6_10.PLAYER_INFO, pw.user());

                    playerInfo.write(Type.VAR_INT, 0);
                    playerInfo.write(Type.VAR_INT, 1);
                    playerInfo.write(Type.UUID, UUID.fromString(gameProfileTracker.getUuid()));
                    playerInfo.write(Type.STRING, gameProfileTracker.getName());
                    playerInfo.write(Type.VAR_INT, 0);
                    playerInfo.write(Type.VAR_INT, (int) pw.get(Type.UNSIGNED_BYTE, 0)); // GameMode
                    playerInfo.write(Type.VAR_INT, 0); // Ping
                    playerInfo.write(Type.OPTIONAL_STRING, gameProfileTracker.getName());

                    playerInfo.send(Protocol1_8_0_9to1_7_6_10.class);
                    Objects.requireNonNull(pw.user().get(TabListTracker.class)).add(new TabListTracker.TabListEntry(gameProfileTracker.getName(), UUID.fromString(gameProfileTracker.getUuid())));

                    pw.write(Type.BOOLEAN, false); // Reduced Debug Info
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.CHUNK_DATA, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(pw -> {
                    final ClientWorld world = pw.user().get(ClientWorld.class);

                    final Chunk chunk = pw.read(new Chunk1_7_6_10Type(world));
                    pw.write(new Chunk1_8Type(world), chunk);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.MAP_BULK_CHUNK, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final ClientWorld world = wrapper.user().get(ClientWorld.class);

                    final Chunk[] chunk = wrapper.read(new ChunkBulk1_7_6_10Type(world));
                    wrapper.write(new ChunkBulk1_8Type(world), chunk);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.MULTI_BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int chunkX = pw.read(Type.INT);
                    final int chunkZ = pw.read(Type.INT);
                    final int size = pw.read(Type.SHORT);

                    pw.read(Type.INT);
                    final BlockChangeRecord1_8[] records = new BlockChangeRecord1_8[size];

                    for (int i = 0; i < size; i++) {
                        final short pos = pw.read(Type.SHORT);

                        records[i] = new BlockChangeRecord1_8(
                                pos >>> 12 & 0xF,
                                pos & 0xFF,
                                pos >>> 8 & 0xF,
                                pw.read(Type.SHORT)
                        );
                    }

                    pw.write(Type.INT, chunkX);
                    pw.write(Type.INT, chunkZ);
                    pw.write(Type.BLOCK_CHANGE_RECORD_ARRAY, records);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler((pw) -> pw.write(Type.BYTE, (byte) 0)); // Chat Box position
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity ID
                map(Type.SHORT); // Slot
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM); // Item
                handler(pw -> pw.set(Type.ITEM, 0, Objects.requireNonNull(getItemRewriter()).handleItemToClient(pw.get(Type.ITEM, 0))));
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_POSITION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                xyzToPosition(); // Position
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.UPDATE_HEALTH, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // Health
                map(Type.SHORT, Type.VAR_INT); // Food
                map(Type.FLOAT); // Saturation of Food
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    packetWrapper.passthrough(Type.VAR_INT); // Entity ID
                    final String uuid = packetWrapper.read(Type.STRING);
                    packetWrapper.write(Type.UUID, UUID.fromString(uuid));

                    final String name = ChatColorUtil.stripColor(packetWrapper.read(Type.STRING));
                    final int dataCount = packetWrapper.read(Type.VAR_INT);

                    final List<SkinProperty> properties = new ArrayList<>();

                    for (int i = 0; i < dataCount; i++) {
                        final String key = packetWrapper.read(Type.STRING); // Name
                        final String value = packetWrapper.read(Type.STRING); // Value
                        final String signature = packetWrapper.read(Type.STRING); // Signature

                        properties.add(new SkinProperty(key, value, signature));
                    }

                    final GameProfileTracker gameProfileTracker = packetWrapper.user().get(GameProfileTracker.class);
                    if (gameProfileTracker != null) {
                        if (Objects.equals(gameProfileTracker.getUuid(), uuid)) {
                            gameProfileTracker.getSkinProperties().clear();
                            gameProfileTracker.getSkinProperties().addAll(properties);
                        }
                    }

                    packetWrapper.passthrough(Type.INT); // X
                    packetWrapper.passthrough(Type.INT); // Y
                    packetWrapper.passthrough(Type.INT); // Z
                    packetWrapper.passthrough(Type.BYTE); // Yaw
                    packetWrapper.passthrough(Type.BYTE); // Pitch
                    packetWrapper.passthrough(Type.SHORT); // Item in hand

                    List<Metadata> metadata = packetWrapper.read(Types1_7_6_10.METADATA_LIST); // Metadata
                    metadataRewriter().rewrite(Entity1_10Types.EntityType.PLAYER, false, metadata);
                    packetWrapper.write(Types1_8.METADATA_LIST, metadata);

                    final PacketWrapper addPlayerInfo = PacketWrapper.create(ClientboundPackets1_7_6_10.PLAYER_INFO, packetWrapper.user());
                    {
                        addPlayerInfo.write(Type.VAR_INT, 0); // ADD
                        addPlayerInfo.write(Type.VAR_INT, 1);
                        addPlayerInfo.write(Type.UUID, UUID.fromString(uuid));
                        addPlayerInfo.write(Type.STRING, name);
                        addPlayerInfo.write(Type.VAR_INT, dataCount);

                        for (SkinProperty property : properties) {
                            addPlayerInfo.write(Type.STRING, property.name);
                            addPlayerInfo.write(Type.STRING, property.value);
                            addPlayerInfo.write(Type.OPTIONAL_STRING, property.signature);
                        }

                        addPlayerInfo.write(Type.VAR_INT, 0);
                        addPlayerInfo.write(Type.VAR_INT, 0);
                        addPlayerInfo.write(Type.OPTIONAL_STRING, name);
                    }

                    final PacketWrapper removePlayerInfo = PacketWrapper.create(ClientboundPackets1_7_6_10.PLAYER_INFO, packetWrapper.user());
                    {
                        removePlayerInfo.write(Type.VAR_INT, 4); // REMOVE
                        removePlayerInfo.write(Type.VAR_INT, 1);
                        removePlayerInfo.write(Type.UUID, UUID.fromString(uuid));
                    }

                    addPlayerInfo.send(Protocol1_8_0_9to1_7_6_10.class);
                    packetWrapper.send(Protocol1_8_0_9to1_7_6_10.class);
                    removePlayerInfo.send(Protocol1_8_0_9to1_7_6_10.class);

                    packetWrapper.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.PLAYER_POSITION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                handler((pw) -> {
                    double y = pw.read(Type.DOUBLE); // original y
                    pw.write(Type.DOUBLE, y - 1.62D); // fixed y
                });
                map(Type.DOUBLE); // z

                map(Type.FLOAT, 2); // Pitch and Yaw

                handler((pw) -> {
                    final boolean onGround = pw.read(Type.BOOLEAN); // On Ground
                    final TeleportTracker teleportTracker = pw.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        teleportTracker.setPending(onGround);
                    }

                    pw.write(Type.BYTE, (byte) 0); // Bit Mask
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.USE_BED, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                handler(pw -> {
                    pw.write(Type.POSITION, new Position(
                            pw.read(Type.INT), // X
                            (int) pw.read(Type.BYTE), // Y
                            pw.read(Type.INT) // Z
                    ));
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int entityID = pw.read(Type.VAR_INT);
                    final short animation = pw.read(Type.UNSIGNED_BYTE);

                    pw.clearInputBuffer();

                    if (animation == 104 || animation == 105) {
                        pw.setPacketType(ClientboundPackets1_7_6_10.ENTITY_METADATA);

                        pw.write(Type.VAR_INT, entityID);
                        pw.write(Type.UNSIGNED_BYTE, (short) 0); // Index
                        pw.write(Type.UNSIGNED_BYTE, (short) 0); // Type
                        pw.write(Type.BYTE, (byte) (animation == 104 ? 0x02 : 0x00)); // Value (sneaking / not sneaking)
                    } else {
                        pw.write(Type.VAR_INT, entityID);
                        pw.write(Type.UNSIGNED_BYTE, animation);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.COLLECT_ITEM, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Collected Entity ID
                intToVarInt(); // Collector Entity ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_VELOCITY, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.SHORT, 3); // Velocity
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_MOVEMENT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_POSITION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE, 3); // Position
                handler((pw) -> {
                    final int entityId = pw.get(Type.VAR_INT, 0);
                    final double y = pw.get(Type.BYTE, 1) / 32.0;

                    final boolean isGround = !(y < 0.0D);

                    final EntityTracker1_7_6_10 entityTracker1_7_6_10 = pw.user().get(EntityTracker1_7_6_10.class);

                    if (entityTracker1_7_6_10 != null) {
                        entityTracker1_7_6_10.getGroundTracker().put(entityId, isGround);
                    }
                    pw.write(Type.BOOLEAN, isGround);
                }); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_ROTATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE, 2); // Yaw and Pitch
                handler((pw) -> {
                    final EntityTracker1_7_6_10 entityTracker1_7_6_10 = pw.user().get(EntityTracker1_7_6_10.class);

                    if (entityTracker1_7_6_10 != null) {
                        pw.write(Type.BOOLEAN, entityTracker1_7_6_10.isGround(pw.get(Type.VAR_INT, 0)));
                    }
                }); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_POSITION_AND_ROTATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.BYTE, 5); // Position and Yaw, Pitch

                handler((pw) -> {
                    final int entityId = pw.get(Type.VAR_INT, 0);
                    final double y = pw.get(Type.BYTE, 1) / 32.0;

                    final boolean isGround = !(y < 0.0D);
                    final EntityTracker1_7_6_10 entityTracker1_7_6_10 = pw.user().get(EntityTracker1_7_6_10.class);

                    if (entityTracker1_7_6_10 != null) {
                        entityTracker1_7_6_10.getGroundTracker().put(entityId, isGround);
                    }
                    pw.write(Type.BOOLEAN, isGround);
                }); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_TELEPORT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.INT, 3); // Position
                map(Type.BYTE, 2); // Rotation

                handler((pw) -> {
                    final int entityId = pw.get(Type.VAR_INT, 0);
                    final EntityTracker1_7_6_10 entityTracker1_7_6_10 = pw.user().get(EntityTracker1_7_6_10.class);

                    if (entityTracker1_7_6_10 != null) {
                        pw.write(Type.BOOLEAN, entityTracker1_7_6_10.isGround(entityId));
                    }
                }); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_HEAD_LOOK, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE); // Head Yaw
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_EFFECT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.BYTE); // Effect ID
                map(Type.BYTE); // Amplifier

                map(Type.SHORT, Type.VAR_INT); // Duration

                handler((pw) -> pw.write(Type.BOOLEAN, false)); // Hide Particles
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.REMOVE_ENTITY_EFFECT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE); // Effect ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SET_EXPERIENCE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // Experience Bar
                map(Type.SHORT, Type.VAR_INT); // Level
                map(Type.SHORT, Type.VAR_INT); // Total Experience
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_PROPERTIES, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                handler((pw) -> {
                    final int amount = pw.read(Type.INT);
                    pw.write(Type.INT, amount);

                    for (int i = 0; i < amount; i++) {
                        pw.passthrough(Type.STRING);
                        pw.passthrough(Type.DOUBLE);

                        final int modifierLength = pw.read(Type.SHORT);
                        pw.write(Type.VAR_INT, modifierLength);

                        for (int j = 0; j < modifierLength; j++) {
                            pw.passthrough(Type.UUID);
                            pw.passthrough(Type.DOUBLE);
                            pw.passthrough(Type.BYTE);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.BLOCK_CHANGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    int y = packetWrapper.read(Type.UNSIGNED_BYTE);
                    int z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION)); // Position

                handler((pw) -> {
                    final int blockID = pw.read(Type.VAR_INT);
                    final int meta = pw.read(Type.UNSIGNED_BYTE);

                    pw.write(Type.VAR_INT, blockID << 4 | (meta & 15));
                }); // Block State
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.BLOCK_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    int y = packetWrapper.read(Type.SHORT);
                    int z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION)); // Position

                map(Type.UNSIGNED_BYTE); // Block Data
                map(Type.UNSIGNED_BYTE); // <
                map(Type.VAR_INT); // <
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.BLOCK_BREAK_ANIMATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Entity ID
                xyzToPosition(); // Position
                map(Type.BYTE); // Progress
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int effectID = pw.read(Type.INT);

                    final int x = pw.read(Type.INT);
                    final int y = pw.read(Type.UNSIGNED_BYTE);
                    final int z = pw.read(Type.INT);

                    final int data = pw.read(Type.INT);

                    final boolean disableRelativeVolume = pw.read(Type.BOOLEAN);

                    if (effectID == 2006) { // Falling particles
                        double d0 = Math.min(0.2F + data / 15.0F, 10.0F);

                        if (d0 > 2.5D) {
                            d0 = 2.5D;
                        }

                        int i = (int) (150.0D * d0);
                        pw.setPacketType(ClientboundPackets1_7_6_10.SPAWN_PARTICLE);
                        pw.write(Type.INT, Particles1_7_6_10.BLOCK_DUST.ordinal()); // BLOCK_DUST
                        pw.write(Type.BOOLEAN, false); // Long Distance
                        pw.write(Type.FLOAT, (float) x + 0.5f); // X
                        pw.write(Type.FLOAT, (float) y + 1.8f / 2f + 0.2f); // Y
                        pw.write(Type.FLOAT, (float) z + 0.5f); // Z
                        pw.write(Type.FLOAT, 0f); // Offset X
                        pw.write(Type.FLOAT, 0f); // Offset Y
                        pw.write(Type.FLOAT, 0f); // Offset Z
                        pw.write(Type.FLOAT, 0.15000000596046448f); // Speed
                        pw.write(Type.INT, i); // Number of particles
                        pw.write(Type.VAR_INT, 1); // force stone particles TODO: Track the world and set a proper id here
                    } else {
                        pw.write(Type.INT, effectID);
                        pw.write(Type.POSITION, new Position(x, y, z));
                        pw.write(Type.INT, data);
                        pw.write(Type.BOOLEAN, disableRelativeVolume);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_PARTICLE, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final String[] parts = pw.read(Type.STRING).split("_");
                    Particles1_7_6_10 particle = Particles1_7_6_10.find(parts[0]);
                    if (particle == null) particle = Particles1_7_6_10.CRIT;

                    pw.write(Type.INT, particle.ordinal()); // Type
                    pw.write(Type.BOOLEAN, false); // Long Distance

                    pw.passthrough(Type.FLOAT); // X
                    pw.passthrough(Type.FLOAT); // Y
                    pw.passthrough(Type.FLOAT); // Z

                    pw.passthrough(Type.FLOAT); // Offset X
                    pw.passthrough(Type.FLOAT); // Offset Y
                    pw.passthrough(Type.FLOAT); // Offset Z

                    pw.passthrough(Type.FLOAT); // Speed

                    pw.passthrough(Type.INT); // Number of particles

                    for (int i = 0; i < particle.extra; ++i) {
                        int toWrite = 0;
                        if (parts.length - 1 > i) {
                            try {
                                toWrite = Integer.parseInt(parts[i + 1]);
                                if (particle.extra == 1 && parts.length == 3) {
                                    ++i;
                                    toWrite |= Integer.parseInt(parts[i + 1]) << 12;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                        pw.write(Type.VAR_INT, toWrite);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final WindowIDTracker tracker = packetWrapper.user().get(WindowIDTracker.class);

                    short windowId = packetWrapper.read(Type.UNSIGNED_BYTE);
                    packetWrapper.write(Type.UNSIGNED_BYTE, windowId);

                    short windowType = packetWrapper.read(Type.UNSIGNED_BYTE);
                    if (tracker != null) {
                        tracker.put(windowId, windowType);
                    }
                    packetWrapper.write(Type.STRING, tracker.getInventoryString(windowType));  // Inventory Type

                    String title = packetWrapper.read(Type.STRING);  // Title
                    final short slots = packetWrapper.read(Type.UNSIGNED_BYTE);
                    final boolean useProvidedWindowTitle = packetWrapper.read(Type.BOOLEAN);  // Use provided window title

                    if (useProvidedWindowTitle) {
                        title = "{\"text\": \"" + title + "\"}";
                    } else {
                        title = "{\"translate\": \"" + title + "\"}";
                    }

                    packetWrapper.write(Type.STRING, title);  // Window title
                    packetWrapper.write(Type.UNSIGNED_BYTE, slots);

                    if (packetWrapper.get(Type.UNSIGNED_BYTE, 0) == 11) {
                        packetWrapper.passthrough(Type.INT);  // Entity ID
                    }
                });
            }
        });

        // Set Slot
        registerClientbound(ClientboundPackets1_7_6_10.SET_SLOT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE);
                map(Type.SHORT);
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);
                handler(pw -> pw.set(Type.ITEM, 0, Objects.requireNonNull(getItemRewriter()).handleItemToClient(pw.get(Type.ITEM, 0))));
                handler(pw -> {
                    final short windowId = pw.get(Type.UNSIGNED_BYTE, 0);
                    short slot = pw.get(Type.SHORT, 0);
                    final Item item = pw.get(Type.ITEM, 0);

                    if (item == null) {
                        return;
                    }

                    final WindowIDTracker windowIDTracker = pw.user().get(WindowIDTracker.class);
                    if (windowIDTracker != null) {
                        if (windowIDTracker.get(windowId) == 4) { // enchantment table
                            if (slot >= 1) {
                                pw.set(Type.SHORT, 0, ++slot);
                            }
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final WindowIDTracker windowIDTracker = packetWrapper.user().get(WindowIDTracker.class);

                    if (windowIDTracker != null) {
                        final short windowId = packetWrapper.passthrough(Type.UNSIGNED_BYTE);  // Window ID
                        final short windowType = windowIDTracker.get(windowId);
                        Item[] items = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY);

                        if (windowType == 4) {
                            Item[] old = items;
                            items = new Item[old.length + 1];
                            items[1] = new DataItem((short) 351, (byte) 3, (short) 4, null); // Lapis

                            for (int oldIndex = 0; oldIndex < old.length; oldIndex++) {
                                int newIndex = oldIndex > 0 ? oldIndex + 1 : oldIndex;
                                items[newIndex] = old[oldIndex];
                            }
                        }
                        packetWrapper.write(Type.ITEM_ARRAY, items);  // Items
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION)); // Position

                handler(packetWrapper -> {
                    for (int i = 0; i < 4; i++)
                        packetWrapper.write(Type.STRING, "{\"text\": \"" + packetWrapper.read(Type.STRING) + "\"}");
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(xyzToPosition, new TypeRemapper<>(Type.POSITION)); // Position

                map(Type.UNSIGNED_BYTE);  // Action
                map(Types1_7_6_10.COMPRESSED_NBT, Type.NBT); // The Item
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.OPEN_SIGN_EDITOR, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                xyzToPosition();
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    pw.passthrough(Type.STRING); // name
                    final String value = pw.read(Type.STRING);
                    final byte mode = pw.read(Type.BYTE);

                    pw.write(Type.BYTE, mode);

                    if (mode == 0 || mode == 2) {
                        pw.write(Type.STRING, value);
                        pw.write(Type.STRING, "integer");
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.UPDATE_SCORE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final ScoreboardLineTracker scoreboardLineTracker = pw.user().get(ScoreboardLineTracker.class);

                    final String name = pw.passthrough(Type.STRING);
                    final byte mode = pw.passthrough(Type.BYTE);

                    if (scoreboardLineTracker != null) {
                        if (mode != 1) {
                            final String objective = pw.passthrough(Type.STRING);
                            scoreboardLineTracker.lines.put(name, objective);
                            pw.write(Type.VAR_INT, pw.read(Type.INT));
                        } else {
                            final String objective = scoreboardLineTracker.lines.get(name);
                            pw.write(Type.STRING, objective);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.TEAMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(packetWrapper -> {
                    final String uniqueName = packetWrapper.get(Type.STRING, 0);
                    final byte mode = packetWrapper.read(Type.BYTE); // unique name
                    packetWrapper.write(Type.BYTE, mode);

                    if (mode == 0 || mode == 2) {
                        packetWrapper.passthrough(Type.STRING); // display name
                        packetWrapper.passthrough(Type.STRING); // team prefix
                        packetWrapper.passthrough(Type.STRING); // team suffix
                        packetWrapper.passthrough(Type.BYTE); // friendly fire
                        packetWrapper.write(Type.STRING, "always"); // name tag visibility
                        packetWrapper.write(Type.BYTE, (byte) 0); // color
                    }
                    String[] entries = null;
                    if (mode == 0 || mode == 3 || mode == 4) {
                        final int count = packetWrapper.read(Type.SHORT); // player count
                        final CustomString1_7_6_10Type type = new CustomString1_7_6_10Type(count);
                        entries = packetWrapper.read(type); // players

                        packetWrapper.write(Type.STRING_ARRAY, entries);
                    }

                    final TeamsTracker teamsTracker = packetWrapper.user().get(TeamsTracker.class);

                    if (teamsTracker != null) {
                        if (mode == 1) {
                            teamsTracker.removeTeamsEntryIf(e -> Objects.equals(e.getKey().uniqueName, uniqueName));
                        } else {
                            TeamsEntry teamsEntry = null;
                            if (mode == 0) {
                                teamsEntry = new TeamsEntry(
                                        uniqueName,
                                        // nice order lmao
                                        packetWrapper.get(Type.STRING, 2),
                                        packetWrapper.get(Type.STRING, 1),
                                        packetWrapper.get(Type.STRING, 3)
                                );

                            } else if (mode == 2) {
                                teamsEntry = teamsTracker.getByUniqueName(uniqueName);
                                teamsEntry.prefix = packetWrapper.get(Type.STRING, 2);
                                teamsEntry.name = packetWrapper.get(Type.STRING, 1);
                                teamsEntry.suffix = packetWrapper.get(Type.STRING, 3);
                            } else if (mode == 3 || mode == 4) {
                                teamsEntry = teamsTracker.getByUniqueName(uniqueName);
                            }
                            if (teamsEntry == null) {
                                return;
                            }

                            final List<String> prePlayers = teamsTracker.getPlayers(teamsEntry);

                            if (entries != null) {
                                for (String entry : entries) {
                                    if (!prePlayers.contains(entry)) {
                                        prePlayers.add(entry);
                                    }
                                }
                            }
                            teamsTracker.putTeamsEntry(teamsEntry, prePlayers);
                            final TabListTracker tablistTracker = packetWrapper.user().get(TabListTracker.class);

                            if (tablistTracker != null) {
                                for (String player : prePlayers) {
                                    final TabListTracker.TabListEntry tabListEntry = tablistTracker.getTabListEntry(player);

                                    if (tabListEntry != null) {
                                        PacketWrapper playerInfo = PacketWrapper.create(ClientboundPackets1_7_6_10.PLAYER_INFO, packetWrapper.user());
                                        playerInfo.write(Type.VAR_INT, 3); // UPDATE DISPLAY NAME
                                        playerInfo.write(Type.VAR_INT, 1);
                                        playerInfo.write(Type.UUID, tabListEntry.uuid);
                                        playerInfo.write(Type.OPTIONAL_STRING, tabListEntry.displayName = teamsEntry.concat(player));

                                        playerInfo.send(Protocol1_8_0_9to1_7_6_10.class);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(packetWrapper -> {
                    final String channel = packetWrapper.get(Type.STRING, 0);
                    switch (channel) {
                        case "MC|Brand" -> {
                            byte[] data = packetWrapper.read(Types1_7_6_10.BYTEARRAY);
                            packetWrapper.write(Type.STRING, new String(data));
                        }
                        case "MC|TrList" -> {
                            packetWrapper.read(Type.UNSIGNED_SHORT); // Length of ByteArray

                            packetWrapper.passthrough(Type.INT);
                            final int size = packetWrapper.passthrough(Type.UNSIGNED_BYTE);

                            for (int i = 0; i < size; ++i) {
                                Item item = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                                packetWrapper.write(Type.ITEM, item);
                                item = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                                packetWrapper.write(Type.ITEM, item);

                                final boolean thirdItem = packetWrapper.passthrough(Type.BOOLEAN);

                                if (thirdItem) {
                                    item = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                                    packetWrapper.write(Type.ITEM, item);
                                }

                                packetWrapper.passthrough(Type.BOOLEAN); // Unavailable
                                packetWrapper.write(Type.INT, 0); // Max uses
                                packetWrapper.write(Type.INT, 0); // Max trades
                            }
                        }
                        case "MC|RPack" -> {
                            final byte[] data = packetWrapper.read(Types1_7_6_10.BYTEARRAY);
                            packetWrapper.clearPacket();
                            packetWrapper.setPacketType(ClientboundPackets1_8.RESOURCE_PACK);
                            packetWrapper.write(Type.STRING, new String(data)); // url
                            packetWrapper.write(Type.STRING, ""); // hash
                        }
                    }
                    packetWrapper.clearInputBuffer(); // 1.7.x servers are sending garbage after the packet, and the via codebase doesn't allow it to skip all readable bytes
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.PLAYER_INFO, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    String name = packetWrapper.read(Type.STRING);
                    final boolean add = packetWrapper.read(Type.BOOLEAN);
                    final short ping = packetWrapper.read(Type.SHORT);

                    String normalizedName = ChatColorUtil.stripColor(name);

                    final TabListTracker tablistTracker = packetWrapper.user().get(TabListTracker.class);
                    if (tablistTracker != null) {
                        TabListTracker.TabListEntry entry = tablistTracker.getTabListEntry(name);

                        if (entry == null && add) {
                            entry = new TabListTracker.TabListEntry(name, UUID.randomUUID());

                            tablistTracker.add(entry);

                            entry.displayName = !Objects.equals(name, normalizedName) ? name : null;

                            final TeamsTracker teamsTracker = packetWrapper.user().get(TeamsTracker.class);
                            if (teamsTracker != null) {
                                final TeamsEntry teamsEntry = teamsTracker.getTeamsEntry(name);

                                if (teamsEntry != null) {
                                    entry.displayName = teamsEntry.concat(name);
                                }
                            }

                            packetWrapper.write(Type.VAR_INT, 0); // ADD
                            packetWrapper.write(Type.VAR_INT, 1);

                            packetWrapper.write(Type.UUID, entry.uuid);
                            packetWrapper.write(Type.STRING, entry.name);
                            packetWrapper.write(Type.VAR_INT, entry.properties.size());
                            for (SkinProperty property : entry.properties) {
                                packetWrapper.write(Type.STRING, property.name);
                                packetWrapper.write(Type.STRING, property.value);
                                packetWrapper.write(Type.OPTIONAL_STRING, property.signature);
                            }
                            packetWrapper.write(Type.VAR_INT, 0);
                            packetWrapper.write(Type.VAR_INT, entry.ping = ping);
                            packetWrapper.write(Type.OPTIONAL_STRING, entry.displayName);
                        } else if (entry != null && !add) {
                            packetWrapper.write(Type.VAR_INT, 4); // REMOVE
                            packetWrapper.write(Type.VAR_INT, 1);
                            packetWrapper.write(Type.UUID, entry.uuid);
                            tablistTracker.remove(entry);
                        } else if (entry != null) {
                            packetWrapper.write(Type.VAR_INT, 2); // UPDATE LATENCY
                            packetWrapper.write(Type.VAR_INT, 1);
                            packetWrapper.write(Type.UUID, entry.uuid);
                            packetWrapper.write(Type.VAR_INT, entry.ping = ping);
                        } else
                            packetWrapper.cancel();
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    final EntityTracker1_7_6_10 tracker = packetWrapper.user().get(EntityTracker1_7_6_10.class);

                    if (tracker != null) {
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.LIGHTNING);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.ENTITY_METADATA, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Types1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST); // Metadata Type
                handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);

                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    final EntityTracker1_7_6_10 tracker = wrapper.user().get(EntityTracker1_7_6_10.class);
                    if (tracker != null) {
                        if (tracker.getClientEntityTypes().containsKey(entityID)) {
                            metadataRewriter().rewrite(tracker.getClientEntityTypes().get(entityID), true, metadataList);
                        }
                    }
                    wrapper.set(Types1_8.METADATA_LIST, 0, metadataList);
                });
            }
        });

        //Spawn Object
        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    packetWrapper.passthrough(Type.VAR_INT); // Entity Id
                    byte type = packetWrapper.passthrough(Type.BYTE);
                    int x = packetWrapper.passthrough(Type.INT);
                    int y = packetWrapper.passthrough(Type.INT);
                    int z = packetWrapper.passthrough(Type.INT);

                    byte yaw = packetWrapper.passthrough(Type.BYTE);
                    packetWrapper.passthrough(Type.BYTE); // Pitch

                    y -= EntityOffsets.getOffset(type);

                    int throwerId = packetWrapper.passthrough(Type.INT);

                    if (throwerId > 0) {
                        packetWrapper.passthrough(Type.SHORT); // Motion X
                        packetWrapper.passthrough(Type.SHORT); // Motion Y
                        packetWrapper.passthrough(Type.SHORT); // Motion Z

                        if (type == Entity1_10Types.ObjectType.FALLING_BLOCK.getId()) {
                            y -= 16;

                            final int itemData = throwerId >> 16;
                            throwerId = throwerId | itemData << 12;
                        }

                        if (type == Entity1_10Types.ObjectType.ITEM_FRAME.getId()) {
                            if (throwerId == 0) {
                                z += 32;
                                yaw = 0;
                            } else if (throwerId == 1) {
                                x -= 32;
                                yaw = 64;
                            } else if (throwerId == 2) {
                                z -= 32;
                                yaw = -128;
                            } else if (throwerId == 3) {
                                x += 32;
                                yaw -= -64;
                            }
                        }
                    }

                    packetWrapper.set(Type.INT, 0, x);
                    packetWrapper.set(Type.INT, 1, y);
                    packetWrapper.set(Type.INT, 2, z);
                    packetWrapper.set(Type.BYTE, 2, yaw);
                });
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    final int typeID = packetWrapper.get(Type.BYTE, 0);

                    final Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeID, true);

                    final EntityTracker1_7_6_10 tracker = packetWrapper.user().get(EntityTracker1_7_6_10.class);
                    if (tracker != null) {
                        tracker.getClientEntityTypes().put(entityID, type);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_MOB, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.UNSIGNED_BYTE);
                map(Type.INT, 3);
                map(Type.BYTE, 3);
                map(Type.SHORT, 3);
                map(Types1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST);
                handler(wrapper -> {
                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    final int typeID = wrapper.get(Type.UNSIGNED_BYTE, 0);

                    final EntityTracker1_7_6_10 tracker = wrapper.user().get(EntityTracker1_7_6_10.class);

                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);

                    if (tracker != null) {
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.getTypeFromId(typeID, false));
                    }
                    //noinspection unchecked
                    metadataRewriter().rewrite(tracker.getClientEntityTypes().get(entityID), false, metadataList);
                    wrapper.set(Types1_8.METADATA_LIST, 0, metadataList);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.SHORT);
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);

                    final EntityTracker1_7_6_10 tracker = packetWrapper.user().get(EntityTracker1_7_6_10.class);
                    if (tracker != null) {
                        tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.DESTROY_ENTITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    int amount = packetWrapper.read(Type.UNSIGNED_BYTE);
                    int[] entityIds = new int[amount];

                    for (int i = 0; i < amount; i++) {
                        entityIds[i] = packetWrapper.read(Type.INT);
                    }

                    packetWrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, entityIds);
                }); // Entity ID Array
                handler(packetWrapper -> {
                    final EntityTracker1_7_6_10 tracker = packetWrapper.user().get(EntityTracker1_7_6_10.class);

                    for (int entityId : packetWrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0)) {
                        if (tracker != null) {
                            tracker.removeEntity(entityId);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.GAME_EVENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE);
                map(Type.FLOAT);
                handler(pw -> {
                    final GameProfileTracker gameProfileTracker = pw.user().get(GameProfileTracker.class);

                    final int type = pw.get(Type.UNSIGNED_BYTE, 0);
                    final float value = pw.get(Type.FLOAT, 0);

                    if (gameProfileTracker != null) {
                        if (type == 3) { // Update GameMode
                            final PacketWrapper playerInfo = PacketWrapper.create(ClientboundPackets1_7_6_10.PLAYER_INFO, pw.user());
                            playerInfo.write(Type.VAR_INT, 1);
                            playerInfo.write(Type.VAR_INT, 1);
                            playerInfo.write(Type.UUID, UUID.fromString(gameProfileTracker.getUuid()));
                            playerInfo.write(Type.VAR_INT, (int) value);

                            playerInfo.send(Protocol1_8_0_9to1_7_6_10.class);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.SPAWN_PAINTING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.STRING);
                handler(pw -> {
                    int x = pw.read(Type.INT);
                    int y = pw.read(Type.INT);
                    int z = pw.read(Type.INT);

                    int direction = pw.read(Type.INT);

                    switch (direction) {
                        case 0 -> z += 1;
                        case 1 -> x -= 1;
                        case 2 -> z -= 1;
                        case 3 -> x += 1;
                    }

                    pw.write(Type.POSITION, new Position(x, y, z));
                    pw.write(Type.BYTE, (byte) direction);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_6_10.WINDOW_PROPERTY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE);
                map(Type.SHORT);
                map(Type.SHORT);
            }
        });

        this.registerServerbound(ServerboundPackets1_8.KEEP_ALIVE, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                varIntToInt(); // Time
            }
        });

        this.registerServerbound(ServerboundPackets1_8.INTERACT_ENTITY, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                varIntToInt();

                handler((pw) -> {
                    final int mode = pw.read(Type.VAR_INT);

                    if (mode == 2) {
                        pw.write(Type.BYTE, (byte) 0);

                        pw.read(Type.FLOAT);
                        pw.read(Type.FLOAT);
                        pw.read(Type.FLOAT);
                    } else {
                        pw.write(Type.BYTE, (byte) mode);
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLAYER_POSITION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                handler((pw) -> {
                    final double feetY = pw.passthrough(Type.DOUBLE);
                    pw.write(Type.DOUBLE, feetY + 1.62D); // head Y
                });
                map(Type.DOUBLE); // z

                map(Type.BOOLEAN); // On Ground
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.DOUBLE); // x
                handler((pw) -> {
                    final double feetY = pw.passthrough(Type.DOUBLE);
                    pw.write(Type.DOUBLE, feetY + 1.62D); // head Y
                });
                map(Type.DOUBLE); // z

                map(Type.FLOAT); // pitch
                map(Type.FLOAT); // yaw

                map(Type.BOOLEAN); // On Ground
                handler(pw -> {
                    final TeleportTracker teleportTracker = pw.user().get(TeleportTracker.class);
                    if (teleportTracker != null) {
                        Boolean pendingTeleport = teleportTracker.getPending();
                        if (pendingTeleport != null) {
                            pw.set(Type.BOOLEAN, 0, pendingTeleport);
                            teleportTracker.setPending(null);
                        }
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLAYER_DIGGING, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT, Type.UNSIGNED_BYTE); // Status

                handler((pw) -> {
                    final Position position = pw.read(Type.POSITION);

                    pw.write(Type.INT, position.x());
                    pw.write(Type.UNSIGNED_BYTE, (short) position.y());
                    pw.write(Type.INT, position.z());
                });

                map(Type.UNSIGNED_BYTE); // Face
            }
        });

        this.registerServerbound(ServerboundPackets1_8.CLIENT_SETTINGS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.BOOLEAN);
                handler((pw) -> pw.write(Type.BYTE, (byte) 0));
                handler(packetWrapper -> {
                    final short flags = packetWrapper.read(Type.UNSIGNED_BYTE);
                    packetWrapper.write(Type.BOOLEAN, (flags & 1) == 1);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    pw.cancel();
                    final String channel = pw.read(Type.STRING);
                    final ByteBuf buf = Unpooled.buffer();
                    switch (channel) {
                        case "MC|ItemName" -> {
                            final byte[] name = pw.read(Type.STRING).getBytes(StandardCharsets.UTF_8);
                            Type.REMAINING_BYTES.write(buf, name);
                        }
                        case "MC|BEdit", "MC|BSign" -> {
                            final Item item = pw.read(Type.ITEM);
                            final CompoundTag tag = item.tag();

                            if (tag != null && tag.contains("pages")) {
                                final ListTag pages = tag.get("pages");

                                if (pages != null) {
                                    for (int i = 0; i < pages.size(); i++) {
                                        final StringTag page = pages.get(i);
                                        page.setValue(page.getValue());
                                    }
                                }
                            }
                            Types1_7_6_10.COMPRESSED_NBT_ITEM.write(buf, item);
                        }
                    }

                    final PacketWrapper wrapper = PacketWrapper.create(ServerboundPackets1_8.PLUGIN_MESSAGE, buf, pw.user());

                    wrapper.write(Type.STRING, channel);
                    wrapper.write(Type.SHORT, (short) buf.readableBytes());
                    wrapper.passthrough(Type.REMAINING_BYTES);
                    wrapper.sendToServer(Protocol1_8_0_9to1_7_6_10.class);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    pw.write(Type.INT, 0); // Entity ID
                    pw.write(Type.BYTE, (byte) 1); // Animation
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.ENTITY_ACTION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                varIntToInt(); // Entity ID
                handler(packetWrapper -> packetWrapper.write(Type.BYTE, (byte) (packetWrapper.read(Type.VAR_INT) + 1)));  //Action ID
                map(Type.VAR_INT, Type.INT); // Action Parameter
            }
        });

        this.registerServerbound(ServerboundPackets1_8.STEER_VEHICLE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // Sideways
                map(Type.FLOAT); // Forwards
                handler(packetWrapper -> {
                    final short flags = packetWrapper.read(Type.UNSIGNED_BYTE);

                    packetWrapper.write(Type.BOOLEAN, (flags & 1) == 1); // Jump
                    packetWrapper.write(Type.BOOLEAN, (flags & 2) == 2); // Unmount
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.CLICK_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final WindowIDTracker windowIDTracker = packetWrapper.user().get(WindowIDTracker.class);
                    final short windowId = packetWrapper.read(Type.UNSIGNED_BYTE); // Window ID

                    packetWrapper.write(Type.BYTE, (byte) windowId);

                    if (windowIDTracker != null) {
                        final short windowType = windowIDTracker.get(windowId);
                        short slot = packetWrapper.read(Type.SHORT);

                        if (windowType == 4) {
                            if (slot == 1) {
                                packetWrapper.cancel(); // Lapis
                            } else if (slot > 1) {
                                slot -= 1;
                            }
                        }
                        packetWrapper.write(Type.SHORT, slot); // Slot
                    }
                });
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Button
                map(Type.SHORT); // Action Number
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Mode
                map(Type.ITEM, Types1_7_6_10.COMPRESSED_NBT_ITEM);
            }
        });

        this.registerServerbound(ServerboundPackets1_8.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT);  //Slot
                map(Type.ITEM, Types1_7_6_10.COMPRESSED_NBT_ITEM);  //Item
                handler(pw -> pw.set(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0, Objects.requireNonNull(getItemRewriter()).handleItemToServer(pw.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0))));
            }
        });

        this.registerServerbound(ServerboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final Position pos = packetWrapper.read(Type.POSITION);

                    packetWrapper.write(Type.INT, pos.x());
                    packetWrapper.write(Type.SHORT, (short) pos.y());
                    packetWrapper.write(Type.INT, pos.z());

                    for (int i = 0; i < 4; i++) {
                        String text = "";
                        final JsonElement jsonElement = packetWrapper.read(Type.COMPONENT);

                        if (jsonElement != null && jsonElement.isJsonObject()) {
                            final JsonElement textComponent = jsonElement.getAsJsonObject().get("text");

                            if (textComponent != null) {
                                text = textComponent.getAsString();
                            }
                        }
                        if (text.length() > 15) {
                            text = text.substring(0, 15); // Notchian server kicks if the string is longer than this
                        }
                        packetWrapper.write(Type.STRING, text);
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    { // Open Books when right clicking
                        final HandItemProvider handItemProvider = Via.getManager().getProviders().get(HandItemProvider.class);

                        if (handItemProvider != null) {
                            final Item item = handItemProvider.getHandItem(packetWrapper.user());

                            if (item != null && item.identifier() == 387) { // Book
                                final PacketWrapper pluginMessage = PacketWrapper.create(ClientboundPackets1_8.PLUGIN_MESSAGE, packetWrapper.user());
                                pluginMessage.write(Type.STRING, "MC|BOpen");
                                pluginMessage.write(Type.REMAINING_BYTES, new byte[0]);

                                pluginMessage.send(Protocol1_8_0_9to1_7_6_10.class);
                            }
                        }
                    }

                    Position pos = packetWrapper.read(Type.POSITION); // Position
                    packetWrapper.write(Type.INT, pos.x()); // X
                    packetWrapper.write(Type.UNSIGNED_BYTE, (short) pos.y()); // Y
                    packetWrapper.write(Type.INT, pos.z()); // Z

                    packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Direction

                    final Item item = packetWrapper.read(Type.ITEM);
                    packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item); // Item

                    packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Facing X
                    packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Facing Y
                    packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Facing Z
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final String text = packetWrapper.read(Type.STRING);

                    packetWrapper.clearPacket();
                    packetWrapper.write(Type.STRING, text);
                });
            }
        });
    }

    @Override
    public MaterialReplacement materialReplacement() {
        return this.materialReplacement;
    }

    @Override
    public @Nullable ItemRewriter<Protocol1_8_0_9to1_7_6_10> getItemRewriter() {
        return this.itemRewriter;
    }

    @Override
    public LegacyMetadataRewriter<Protocol1_8_0_9to1_7_6_10> metadataRewriter() {
        return this.metadataRewriter;
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new WindowIDTracker(userConnection));
        userConnection.put(new ScoreboardLineTracker(userConnection));
        userConnection.put(new TabListTracker(userConnection));
        userConnection.put(new EntityTracker1_7_6_10(userConnection));
        userConnection.put(new TeamsTracker(userConnection));
        userConnection.put(new TeleportTracker(userConnection));
        userConnection.put(new GameProfileTracker(userConnection));

        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
    }
}
