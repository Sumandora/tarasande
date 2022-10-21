/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 08.04.22, 14:41
 *
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.0--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license.
 */

package de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.TypeRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.api.type.types.VoidType;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.GsonUtil;
import de.florianmichael.vialegacy.ViaLegacy;
import de.florianmichael.vialegacy.api.minecraft_util.ChatUtil;
import de.florianmichael.vialegacy.api.profile.GameProfile;
import de.florianmichael.vialegacy.api.profile.property.Property;
import de.florianmichael.vialegacy.api.type.TypeRegistry1_7_6_10;
import de.florianmichael.vialegacy.api.type._1_7_6_10.CustomStringType_1_7_6_10;
import de.florianmichael.vialegacy.api.via.EnZaProtocol;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.chunk.Chunk1_8to1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.item.ItemRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.metadata.MetadataRewriter;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.particle.ParticleRegistry;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage.EntityTracker;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage.ScoreboardLineTracker;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage.TablistTracker;
import de.florianmichael.vialegacy.protocols.protocol1_8to1_7_10.storage.WindowIDTracker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * #####################################################################################################################
 * # Notes:                                                                                                            #
 * # The upcoming Protocols are self-written and do not belong to ViaVersion, they are partly dependent on Minecraft   #
 * # code and are subject to the FMPL v1.0 License.                                                                    #
 * # Made by Florian Michael                                                                                           #
 * #####################################################################################################################
 */
public class Protocol1_8to1_7_10 extends EnZaProtocol<ClientboundPackets1_7_10, ClientboundPackets1_8, ServerboundPackets1_7_10, ServerboundPackets1_8> {

    public Protocol1_8to1_7_10() {
        super(ClientboundPackets1_7_10.class, ClientboundPackets1_8.class, ServerboundPackets1_7_10.class, ServerboundPackets1_8.class);
    }

    @Override
    protected void registerPackets() {
        this.cancelServerbound(ServerboundPackets1_8.SPECTATE);
        this.cancelServerbound(ServerboundPackets1_8.RESOURCE_PACK_STATUS);

        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_7_2.ENCRYPTION_REQUEST.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); //Server Hash

                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
                map(Type.SHORT_BYTE_ARRAY, Type.BYTE_ARRAY_PRIMITIVE);
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets1_7_2.ENCRYPTION_RESPONSE.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
                map(Type.BYTE_ARRAY_PRIMITIVE, Type.SHORT_BYTE_ARRAY);
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.KEEP_ALIVE, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Time
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.JOIN_GAME, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity ID
                map(Type.UNSIGNED_BYTE); // GameMode
                map(Type.BYTE); // Dimension
                map(Type.UNSIGNED_BYTE); // Difficulty
                map(Type.UNSIGNED_BYTE); // Max Players
                map(Type.STRING); // Level Type

                handler((pw) -> {
                    tablistTracker(pw.user()).setGameMode(pw.get(Type.UNSIGNED_BYTE, 0)); // Keep gamemode for tablist
                    pw.write(Type.BOOLEAN, false); // Reduced Debug Info
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.CHUNK_DATA, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(pw -> {
                    int chunkX = pw.read(Type.INT);
                    int chunkZ = pw.read(Type.INT);
                    boolean groundUp = pw.read(Type.BOOLEAN);
                    int primaryBitMask = pw.read(Type.SHORT);
                    int addBitMask = pw.read(Type.SHORT);
                    int compressedSize = pw.read(Type.INT);
                    CustomByteType customByteType = new CustomByteType(compressedSize);
                    byte[] data = pw.read(customByteType);

                    int k = 0;
                    int l = 0;

                    for (int j = 0; j < 16; ++j) {
                        k += primaryBitMask >> j & 1;
                        l += addBitMask >> j & 1;
                    }

                    int uncompressedSize = 12288 * k;
                    uncompressedSize += 2048 * l;
                    if (groundUp) {
                        uncompressedSize += 256;
                    }

                    byte[] uncompressedData = new byte[uncompressedSize];
                    Inflater inflater = new Inflater();
                    inflater.setInput(data, 0, compressedSize);
                    try {
                        inflater.inflate(uncompressedData);
                    } catch (DataFormatException ex) {
                        throw new IOException("Bad compressed data format");
                    } finally {
                        inflater.end();
                    }

                    Chunk1_8to1_7_6_10 chunk = new Chunk1_8to1_7_6_10(uncompressedData, primaryBitMask, addBitMask, true, groundUp);

                    Field field = PacketWrapperImpl.class.getDeclaredField("packetValues");
                    field.setAccessible(true);
                    ((List) field.get(pw)).clear();
                    field = PacketWrapperImpl.class.getDeclaredField("readableObjects");
                    field.setAccessible(true);
                    ((ArrayDeque) field.get(pw)).clear();
                    field = PacketWrapperImpl.class.getDeclaredField("inputBuffer");
                    field.setAccessible(true);
                    ByteBuf buffer = (ByteBuf) field.get(pw);
                    buffer.clear();

                    buffer.writeInt(chunkX);
                    buffer.writeInt(chunkZ);
                    buffer.writeBoolean(groundUp);
                    buffer.writeShort(primaryBitMask);
                    byte[] finaldata = chunk.get1_8Data();
                    Type.VAR_INT.writePrimitive(buffer, finaldata.length);
                    buffer.writeBytes(finaldata);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.MAP_BULK_CHUNK, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    transformChunkBulk(pw);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Message

                handler((pw) -> pw.write(Type.BYTE, (byte) 0)); // Chat Box position
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity ID
                map(Type.SHORT); // Slot
                map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM); // Item
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_POSITION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                xyzToPosition(); // Position
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.UPDATE_HEALTH, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // Health
                map(Type.SHORT, Type.VAR_INT); // Food
                map(Type.FLOAT); // Saturation of Food
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_PLAYER, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final TablistTracker tablistTracker = tablistTracker(packetWrapper.user());

                    final int entityId = packetWrapper.passthrough(Type.VAR_INT);

                    UUID uuid = UUID.fromString(packetWrapper.read(Type.STRING));
                    packetWrapper.write(Type.UUID, uuid);

                    final String name = ChatColorUtil.stripColor(packetWrapper.read(Type.STRING));
                    final int dataCount = packetWrapper.read(Type.VAR_INT);

                    final List<TablistTracker.Property> properties = new ArrayList<>();

                    for (int i = 0; i < dataCount; i++) {
                        String key = packetWrapper.read(Type.STRING);  //Name
                        String value = packetWrapper.read(Type.STRING);  //Value
                        String signature = packetWrapper.read(Type.STRING);  //Signature
                        properties.add(new TablistTracker.Property(key, value, signature));
                    }
                    int x = packetWrapper.passthrough(Type.INT);  //x
                    int y = packetWrapper.passthrough(Type.INT);  //y
                    int z = packetWrapper.passthrough(Type.INT);  //z
                    byte yaw = packetWrapper.passthrough(Type.BYTE);  //yaw
                    byte pitch = packetWrapper.passthrough(Type.BYTE);  //pitch
                    short item = packetWrapper.passthrough(Type.SHORT);  //Item in hand
                    List<Metadata> metadata = packetWrapper.read(TypeRegistry1_7_6_10.METADATA_LIST);  //Metadata
                    MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadata);
                    packetWrapper.write(Types1_8.METADATA_LIST, metadata);

                    TablistTracker.TabListEntry entryByName = tablistTracker.getTabListEntry(name);

                    if (entryByName == null && name.length() > 14)
                        entryByName = tablistTracker.getTabListEntry(name.substring(0, 14));

                    TablistTracker.TabListEntry entryByUUID = tablistTracker.getTabListEntry(uuid);

                    if (entryByName == null || entryByUUID == null) {
                        if (entryByName != null || entryByUUID != null) {
                            PacketWrapper remove = PacketWrapper.create(ClientboundPackets1_7_10.PLAYER_INFO, packetWrapper.user());
                            remove.write(Type.VAR_INT, 4);
                            remove.write(Type.VAR_INT, 1);
                            remove.write(Type.UUID, entryByName == null ? entryByUUID.uuid : entryByName.uuid);
                            tablistTracker.remove(entryByName == null ? entryByUUID : entryByName);
                            remove.send(Protocol1_8to1_7_10.class);
                        }

                        PacketWrapper packetPlayerListItem = PacketWrapper.create(ClientboundPackets1_7_10.PLAYER_INFO, packetWrapper.user());
                        TablistTracker.TabListEntry newentry = new TablistTracker.TabListEntry(name, uuid);
                        if (entryByName != null || entryByUUID != null) {
                            newentry.displayName = entryByUUID != null ? entryByUUID.displayName : entryByName.displayName;
                        }
                        newentry.properties = properties;
                        tablistTracker.add(newentry);
                        packetPlayerListItem.write(Type.VAR_INT, 0);
                        packetPlayerListItem.write(Type.VAR_INT, 1);
                        packetPlayerListItem.write(Type.UUID, newentry.uuid);
                        packetPlayerListItem.write(Type.STRING, newentry.name);
                        packetPlayerListItem.write(Type.VAR_INT, dataCount);

                        for (TablistTracker.Property property : newentry.properties) {
                            packetPlayerListItem.write(Type.STRING, property.name);
                            packetPlayerListItem.write(Type.STRING, property.value);
                            packetPlayerListItem.write(Type.BOOLEAN, property.signature != null);

                            if (property.signature != null) packetPlayerListItem.write(Type.STRING, property.signature);
                        }
                        packetPlayerListItem.write(Type.VAR_INT, 0);
                        packetPlayerListItem.write(Type.VAR_INT, 0);
                        packetPlayerListItem.write(Type.BOOLEAN, newentry.displayName != null);
                        if (newentry.displayName != null) {
                            packetPlayerListItem.write(Type.STRING, newentry.displayName);
                        }
                        packetPlayerListItem.send(Protocol1_8to1_7_10.class);

                        packetWrapper.cancel();

                        final PacketWrapper delayedPacket = PacketWrapper.create(ClientboundPackets1_7_10.SPAWN_PLAYER, packetWrapper.user());

                        delayedPacket.write(Type.VAR_INT, entityId);

                        delayedPacket.write(Type.UUID, uuid);

                        delayedPacket.write(Type.INT, x);
                        delayedPacket.write(Type.INT, y);
                        delayedPacket.write(Type.INT, z);

                        delayedPacket.write(Type.BYTE, yaw);
                        delayedPacket.write(Type.BYTE, pitch);

                        delayedPacket.write(Type.SHORT, item);

                        delayedPacket.write(Types1_8.METADATA_LIST, metadata);

                        Via.getPlatform().runSync(() -> {
                            try {
                                delayedPacket.send(Protocol1_8to1_7_10.class);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }, 1L);
                    } else entryByUUID.properties = properties;

                });
                handler(packetWrapper -> {
                    final EntityTracker entityTracker = entityTracker(packetWrapper.user());
                    int entityID = packetWrapper.get(Type.VAR_INT, 0);

                    entityTracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.PLAYER);
                    entityTracker.sendMetadataBuffer(entityID);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.PLAYER_POSITION, new CustomPacketRemapper() {
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
                    pw.read(Type.BOOLEAN); // On Ground
                    pw.write(Type.BYTE, (byte) 0); // Bit Mask
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.USE_BED, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                xyzToPosition(); // Position
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_ANIMATION, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int entityID = pw.read(Type.VAR_INT);
                    final short animation = pw.read(Type.UNSIGNED_BYTE);

                    pw.clearInputBuffer();

                    if (animation == 104 || animation == 105) {
                        pw.setId(ClientboundPackets1_7_10.ENTITY_METADATA);

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

        this.registerClientbound(ClientboundPackets1_7_10.COLLECT_ITEM, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Collected Entity ID
                intToVarInt(); // Collector Entity ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_VELOCITY, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.SHORT, 3); // Velocity
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_MOVEMENT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_POSITION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE, 3); // Position
                handler((pw) -> pw.write(Type.BOOLEAN, true)); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_ROTATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE, 2); // Yaw and Pitch
                handler((pw) -> pw.write(Type.BOOLEAN, true)); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_POSITION_AND_ROTATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.BYTE, 5); // Position and Yaw, Pitch

                handler((pw) -> pw.write(Type.BOOLEAN, true)); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_TELEPORT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.INT, 3); // Position
                map(Type.BYTE, 2); // Rotation

                handler((pw) -> pw.write(Type.BOOLEAN, true)); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_HEAD_LOOK, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE); // Head Yaw
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_EFFECT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID

                map(Type.BYTE); // Effect ID
                map(Type.BYTE); // Amplifier

                map(Type.SHORT, Type.VAR_INT); // Duration

                handler((pw) -> pw.write(Type.BOOLEAN, false)); // Hide Particles
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.REMOVE_ENTITY_EFFECT, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(Type.BYTE); // Effect ID
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SET_EXPERIENCE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.FLOAT); // Experience Bar
                map(Type.SHORT, Type.VAR_INT); // Level
                map(Type.SHORT, Type.VAR_INT); // Total Experience
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_PROPERTIES, new CustomPacketRemapper() {
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

        this.registerClientbound(ClientboundPackets1_7_10.BLOCK_CHANGE, new PacketRemapper() {
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

        this.registerClientbound(ClientboundPackets1_7_10.BLOCK_ACTION, new PacketRemapper() {
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

        this.registerClientbound(ClientboundPackets1_7_10.BLOCK_BREAK_ANIMATION, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Entity ID
                xyzToPosition(); // Position
                map(Type.BYTE); // Progress
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.EFFECT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int effectID = pw.read(Type.INT);

                    final int x = pw.read(Type.INT);
                    final int y = pw.read(Type.UNSIGNED_BYTE);
                    final int z = pw.read(Type.INT);

                    final int data = pw.read(Type.INT);

                    final boolean disableRelativeVolume = pw.read(Type.BOOLEAN);

                    if (effectID == 2006) pw.cancel();
                    else {
                        pw.write(Type.INT, effectID);
                        pw.write(Type.POSITION, new Position(x, y, z));
                        pw.write(Type.INT, data);
                        pw.write(Type.BOOLEAN, disableRelativeVolume);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_PARTICLE, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final String[] parts = pw.read(Type.STRING).split("_");
                    ParticleRegistry particle = ParticleRegistry.find(parts[0]);
                    if (particle == null) particle = ParticleRegistry.CRIT;

                    pw.write(Type.INT, particle.ordinal());
                    pw.write(Type.BOOLEAN, false);

                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);
                    pw.passthrough(Type.FLOAT);

                    pw.passthrough(Type.INT);

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

        this.registerClientbound(ClientboundPackets1_7_10.OPEN_WINDOW, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final WindowIDTracker tracker = windowTracker(packetWrapper.user());

                    short windowId = packetWrapper.read(Type.UNSIGNED_BYTE);
                    packetWrapper.write(Type.UNSIGNED_BYTE, windowId);

                    short windowType = packetWrapper.read(Type.UNSIGNED_BYTE);
                    tracker.put(windowId, windowType);
                    packetWrapper.write(Type.STRING, tracker.getInventoryString(windowType));  // Inventory Type

                    String title = packetWrapper.read(Type.STRING);  // Title
                    short slots = packetWrapper.read(Type.UNSIGNED_BYTE);
                    boolean useProvidedWindowTitle = packetWrapper.read(Type.BOOLEAN);  // Use provided window title

                    if (useProvidedWindowTitle) {
                        title = "{\"text\": \"" + title + "\"}";
                    } else {
                        title = "{\"translate\": \"" + title + "\"}";
                    }

                    packetWrapper.write(Type.STRING, title);  // Window title
                    packetWrapper.write(Type.UNSIGNED_BYTE, slots);

                    if (packetWrapper.get(Type.UNSIGNED_BYTE, 0) == 11)
                        packetWrapper.passthrough(Type.INT);  // Entity ID
                });
            }
        });

        // Set Slot
        registerClientbound(ClientboundPackets1_7_10.SET_SLOT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE);
                map(Type.SHORT);
                map(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, Type.ITEM);
                handler(pw -> pw.set(Type.ITEM, 0, ItemRewriter.toClient(pw.get(Type.ITEM, 0))));
                handler(pw -> {
                    short windowId = pw.get(Type.UNSIGNED_BYTE, 0);
                    if (windowId != 0) return;
                    short slot = pw.get(Type.SHORT, 0);
                    if (slot < 5 || slot > 8) return;
                    Item item = pw.get(Type.ITEM, 0);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    final short windowId = packetWrapper.passthrough(Type.UNSIGNED_BYTE);  // Window ID
                    final short windowType = windowTracker(packetWrapper.user()).get(windowId);
                    Item[] items = packetWrapper.read(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY);

                    if (windowType == 4) {
                        Item[] old = items;
                        items = new Item[old.length + 1];
                        items[0] = old[0];
                        System.arraycopy(old, 1, items, 2, old.length - 1);
                        items[1] = new DataItem((short) 351, (byte) 3, (short) 4, null);
                    }
                    packetWrapper.write(Type.ITEM_ARRAY, items);  // Items
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    int y = packetWrapper.read(Type.SHORT);
                    int z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION)); // Position

                handler(packetWrapper -> {
                    for (int i = 0; i < 4; i++)
                        packetWrapper.write(Type.STRING, "{\"text\": \"" + packetWrapper.read(Type.STRING) + "\"}");
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.BLOCK_ENTITY_DATA, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    int y = packetWrapper.read(Type.SHORT);
                    int z = packetWrapper.read(Type.INT);
                    return new Position(x, y, z);
                }, new TypeRemapper<>(Type.POSITION));  // Position
                map(Type.UNSIGNED_BYTE);  // Action
                map(TypeRegistry1_7_6_10.COMPRESSED_NBT, Type.NBT); // The Item
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.OPEN_SIGN_EDITOR, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                xyzToPosition();
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SCOREBOARD_OBJECTIVE, new PacketRemapper() {
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

        this.registerClientbound(ClientboundPackets1_7_10.UPDATE_SCORE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler((pw) -> {
                    final ScoreboardLineTracker scoreboardLineTracker = scoreboardTracker(pw.user());

                    final String name = pw.passthrough(Type.STRING);
                    final byte mode = pw.passthrough(Type.BYTE);

                    if (mode != 1) {
                        final String objective = pw.passthrough(Type.STRING);
                        scoreboardLineTracker.lines.put(name, objective);
                        pw.write(Type.VAR_INT, pw.read(Type.INT));
                    } else {
                        final String objective = scoreboardLineTracker.lines.get(name);
                        pw.write(Type.STRING, objective);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.TEAMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler(packetWrapper -> {
                    final byte mode = packetWrapper.read(Type.BYTE);
                    packetWrapper.write(Type.BYTE, mode);
                    if (mode == 0 || mode == 2) {
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.BYTE);
                        packetWrapper.write(Type.STRING, "always");
                        packetWrapper.write(Type.BYTE, (byte) 0);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        final int count = packetWrapper.read(Type.SHORT);
                        final CustomStringType_1_7_6_10 type = new CustomStringType_1_7_6_10(count);
                        final String[] entries = packetWrapper.read(type);

                        packetWrapper.write(Type.STRING_ARRAY, entries);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(TypeRegistry1_7_6_10.PLUGIN_MESSAGE_CHANNEL_STRING);
                handler(packetWrapper -> {
                    final String channel = packetWrapper.get(TypeRegistry1_7_6_10.PLUGIN_MESSAGE_CHANNEL_STRING, 0);
                    switch (channel) {
                        case "MC|Brand" -> {
                            byte[] data = packetWrapper.read(TypeRegistry1_7_6_10.BYTEARRAY);
                            packetWrapper.write(Type.STRING, new String(data));
                        }
                        case "MC|TrList" -> {
                            packetWrapper.read(Type.UNSIGNED_SHORT); // Length of ByteArray

                            packetWrapper.passthrough(Type.INT);
                            final int size = packetWrapper.passthrough(Type.UNSIGNED_BYTE);

                            for (int i = 0; i < size; ++i) {
                                Item item = packetWrapper.read(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM);
                                packetWrapper.write(Type.ITEM, item);
                                item = packetWrapper.read(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM);
                                packetWrapper.write(Type.ITEM, item);

                                final boolean thirdItem = packetWrapper.passthrough(Type.BOOLEAN);

                                if (thirdItem) {
                                    item = packetWrapper.read(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM);
                                    packetWrapper.write(Type.ITEM, item);
                                }

                                packetWrapper.passthrough(Type.BOOLEAN); // Unavailable
                                packetWrapper.write(Type.INT, 0); // Max uses
                                packetWrapper.write(Type.INT, 0); // Max trades
                            }
                            packetWrapper.clearInputBuffer(); // 1.7.x servers are sending garbage after the packet, and the via codebase doesn't allow it to skip all readable bytes
                        }
                        case "MC|RPack" -> {
                            final byte[] data = packetWrapper.read(TypeRegistry1_7_6_10.BYTEARRAY);
                            packetWrapper.clearPacket();
                            packetWrapper.setId(ClientboundPackets1_8.RESOURCE_PACK);
                            packetWrapper.write(Type.STRING, new String(data)); // url
                            packetWrapper.write(Type.STRING, ""); // hash
                        }
                    }
                    packetWrapper.clearInputBuffer();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.PLAYER_INFO, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    // 1.7 Packet
                    String name = packetWrapper.read(Type.STRING);
                    final boolean add = packetWrapper.read(Type.BOOLEAN);
                    final short ping = packetWrapper.read(Type.SHORT);

                    final TablistTracker tablist = tablistTracker(packetWrapper.user());

                    String normalizedName = ChatColorUtil.stripColor(name);

                    TablistTracker.TabListEntry entry = tablist.getTabListEntry(normalizedName);

                    if (entry == null && add) {
                        final GameProfile gameProfile = ViaLegacy.getProvider().profile_1_7();

                        boolean selfPlayer = false;

                        if (gameProfile != null && Objects.equals(normalizedName, gameProfile.getName())) {
                            entry = new TablistTracker.TabListEntry(normalizedName, gameProfile.getUuid());
                            for (Map.Entry<String, Property> propertyEntry : gameProfile.getSkinProperties().entries()) {
                                Property property = propertyEntry.getValue();
                                entry.properties.add(new TablistTracker.Property(property.getName(), property.getValue(), property.getSignature()));
                            }
                            selfPlayer = true;
                        } else entry = new TablistTracker.TabListEntry(normalizedName, UUID.randomUUID());

                        tablist.add(entry);

                        int index = tablist.indexOf(entry);
                        entry.displayName = name;

                        packetWrapper.write(Type.VAR_INT, 0); // ADD
                        packetWrapper.write(Type.VAR_INT, 1);

                        packetWrapper.write(Type.UUID, entry.uuid);
                        packetWrapper.write(Type.STRING, entry.name);
                        packetWrapper.write(Type.VAR_INT, entry.properties.size());
                        for (TablistTracker.Property property : entry.properties) {
                            packetWrapper.write(Type.STRING, property.name);
                            packetWrapper.write(Type.STRING, property.value);
                            packetWrapper.write(Type.BOOLEAN, property.signature != null);
                            if (property.signature != null)
                                packetWrapper.write(Type.STRING, property.signature);
                        }
                        packetWrapper.write(Type.VAR_INT, selfPlayer ? tablist.getGameMode() : 0);
                        packetWrapper.write(Type.VAR_INT, (int) ping);
                        packetWrapper.write(Type.BOOLEAN, true);
                        if (entry.displayName != null) {
                            packetWrapper.write(Type.STRING, entry.displayName);
                        }
                    } else if (entry != null && !add) {
                        packetWrapper.write(Type.VAR_INT, 4); // REMOVE
                        packetWrapper.write(Type.VAR_INT, 1);
                        packetWrapper.write(Type.UUID, entry.uuid);
                        tablist.remove(entry);
                    } else if (entry != null && add && ping > 0) {
                        packetWrapper.write(Type.VAR_INT, 2); // UPDATE LATENCY
                        packetWrapper.write(Type.VAR_INT, 1);
                        packetWrapper.write(Type.UUID, entry.uuid);
                        packetWrapper.write(Type.VAR_INT, (int) ping);
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                handler(packetWrapper -> {
                    int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.LIGHTNING);
                    tracker.sendMetadataBuffer(entityID);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.ENTITY_METADATA, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                intToVarInt(); // Entity ID
                map(TypeRegistry1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST); // Metadata Type
                handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    final int entityID = wrapper.get(Type.VAR_INT, 0);
                    final EntityTracker tracker = entityTracker(wrapper.user());

                    if (tracker.getClientEntityTypes().containsKey(entityID)) {
                        MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityID), metadataList);
                        if (metadataList.isEmpty()) wrapper.cancel();
                    } else {
                        tracker.addMetadataToBuffer(entityID, metadataList);
                        wrapper.cancel();
                    }
                });
            }
        });

        //Spawn Object
        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_ENTITY, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.BYTE);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.BYTE);
                map(Type.BYTE);
                map(Type.INT);
                handler(packetWrapper -> {
                    byte type = packetWrapper.get(Type.BYTE, 0);
                    int x = packetWrapper.get(Type.INT, 0);
                    int y = packetWrapper.get(Type.INT, 1);
                    int z = packetWrapper.get(Type.INT, 2);
                    byte yaw = packetWrapper.get(Type.BYTE, 2);
                    int data = packetWrapper.get(Type.INT, 3);

                    if (type == 71) {
                        switch (data) {
                            case 0 -> {
                                z += 32;
                                yaw = 0;
                            }
                            case 1 -> {
                                x -= 32;
                                yaw = (byte) 64;
                            }
                            case 2 -> {
                                z -= 32;
                                yaw = (byte) 128;
                            }
                            case 3 -> {
                                x += 32;
                                yaw = (byte) 192;
                            }
                        }
                    }

                    if (type == 70) {
                        int id = data;
                        int metadata = data >> 16;
                        data = id | metadata << 12;
                    }

                    if (type == 50 || type == 70 || type == 74) y -= 16;

                    packetWrapper.set(Type.INT, 0, x);
                    packetWrapper.set(Type.INT, 1, y);
                    packetWrapper.set(Type.INT, 2, z);
                    packetWrapper.set(Type.BYTE, 2, yaw);
                    packetWrapper.set(Type.INT, 3, data);
                });
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    final int typeID = packetWrapper.get(Type.BYTE, 0);

                    final EntityTracker tracker = entityTracker(packetWrapper.user());
                    final Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeID, true);

                    tracker.getClientEntityTypes().put(entityID, type);
                    tracker.sendMetadataBuffer(entityID);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_MOB, new CustomPacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.UNSIGNED_BYTE);
                map(Type.INT, 3);
                map(Type.BYTE, 3);
                map(Type.SHORT, 3);
                map(TypeRegistry1_7_6_10.METADATA_LIST, Types1_8.METADATA_LIST);
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    final int typeID = packetWrapper.get(Type.UNSIGNED_BYTE, 0);

                    final EntityTracker tracker = entityTracker(packetWrapper.user());

                    tracker.getClientEntityTypes().put(entityID, Entity1_10Types.getTypeFromId(typeID, false));
                    tracker.sendMetadataBuffer(entityID);
                });
                handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    final int entityID = wrapper.get(Type.VAR_INT, 0);

                    final EntityTracker tracker = entityTracker(wrapper.user());

                    if (tracker.getClientEntityTypes().containsKey(entityID))
                        MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityID), metadataList);
                    else wrapper.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.VAR_INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.INT);
                map(Type.SHORT);
                handler(packetWrapper -> {
                    final int entityID = packetWrapper.get(Type.VAR_INT, 0);
                    final EntityTracker tracker = entityTracker(packetWrapper.user());

                    tracker.getClientEntityTypes().put(entityID, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    tracker.sendMetadataBuffer(entityID);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_7_10.DESTROY_ENTITIES, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    int amount = packetWrapper.read(Type.UNSIGNED_BYTE);
                    int[] entityIds = new int[amount];

                    for (int i = 0; i < amount; i++)
                        entityIds[i] = packetWrapper.read(Type.INT);

                    packetWrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, entityIds);
                });  //Entity ID Array
                handler(packetWrapper -> {
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);

                    for (int entityId : packetWrapper.get(Type.VAR_INT_ARRAY_PRIMITIVE, 0))
                        tracker.removeEntity(entityId);
                });
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
                    } else pw.write(Type.BYTE, (byte) mode);
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
                    short flags = packetWrapper.read(Type.UNSIGNED_BYTE);
                    packetWrapper.write(Type.BOOLEAN, (flags & 1) == 1);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING);
                handler((pw) -> {
                    final String channel = pw.get(Type.STRING, 0);
                    switch (channel) {
                        case "MC|ItemName" -> {
                            final byte[] name = pw.read(Type.STRING).getBytes(StandardCharsets.UTF_8);
                            pw.write(Type.REMAINING_BYTES, name);
                        }
                        case "MC|BEdit", "MC|BSign" -> {
                            final Item item = pw.read(Type.ITEM);
                            final CompoundTag tag = item.tag();
                            if (tag != null && tag.contains("pages")) {
                                final ListTag pages = tag.get("pages");
                                if (pages != null) {
                                    for (int i = 0; i < pages.size(); i++) {
                                        final StringTag page = pages.get(i);
                                        page.setValue(ChatUtil.jsonToLegacy(page.getValue()));
                                    }
                                }
                            }
                            pw.write(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, item);
                        }
                    }
                    pw.cancel();
                    pw.setPacketType(null);
                    final ByteBuf buf = Unpooled.buffer();
                    pw.writeToBuffer(buf);
                    final PacketWrapper wrapper = PacketWrapper.create(ServerboundPackets1_8.PLUGIN_MESSAGE, buf, pw.user());
                    wrapper.passthrough(Type.STRING);
                    wrapper.write(Type.SHORT, (short) buf.readableBytes());
                    wrapper.sendToServer(Protocol1_8to1_7_10.class);
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
                    final WindowIDTracker windowIDTracker = windowTracker(packetWrapper.user());
                    final short windowId = packetWrapper.read(Type.UNSIGNED_BYTE); // Window ID

                    packetWrapper.write(Type.BYTE, (byte) windowId);

                    short windowType = windowIDTracker.get(windowId);
                    short slot = packetWrapper.read(Type.SHORT);

                    if (windowType == 4) {
                        if (slot == 1) packetWrapper.cancel();
                        else if (slot > 1) slot -= 1;
                    }
                    packetWrapper.write(Type.SHORT, slot); // Slot
                });
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Button
                map(Type.SHORT); // Action Number
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Mode
                map(Type.ITEM, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM);
            }
        });

        this.registerServerbound(ServerboundPackets1_8.CREATIVE_INVENTORY_ACTION, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT);  //Slot
                map(Type.ITEM, TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM);  //Item
            }
        });

        this.registerServerbound(ServerboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    Position pos = packetWrapper.read(Type.POSITION);
                    packetWrapper.write(Type.INT, pos.x());
                    packetWrapper.write(Type.SHORT, (short) pos.y());
                    packetWrapper.write(Type.INT, pos.z());

                    for (int i = 0; i < 4; i++)
                        packetWrapper.write(Type.STRING, GsonUtil.getGson().fromJson(packetWrapper.read(Type.STRING), ChatMessage.class).text);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    int x;
                    int y;
                    int z;
                    if (packetWrapper.isReadable(Type.POSITION, 0)) {
                        Position pos = packetWrapper.read(Type.POSITION); // Position
                        x = pos.x();
                        y = pos.y();
                        z = pos.z();
                    } else {
                        Long pos = packetWrapper.read(Type.LONG); // Position
                        x = (int) (pos >> 38);
                        y = (short) (pos >> 26 & 4095L);
                        z = (int) (pos << 38 >> 38);
                    }
                    packetWrapper.write(Type.INT, x);
                    packetWrapper.write(Type.UNSIGNED_BYTE, (short) y);
                    packetWrapper.write(Type.INT, z);

                    final short direction = packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Direction

                    VoidType voidType = new VoidType();
                    if (packetWrapper.isReadable(voidType, 0)) packetWrapper.read(voidType);

                    Item item = packetWrapper.read(Type.ITEM);
                    packetWrapper.write(TypeRegistry1_7_6_10.COMPRESSED_NBT_ITEM, item);

                    for (int i = 0; i < 3; i++) {
                        if (packetWrapper.isReadable(Type.BYTE, 0)) packetWrapper.passthrough(Type.BYTE);
                        else {
                            short cursor = packetWrapper.read(Type.UNSIGNED_BYTE);
                            packetWrapper.write(Type.BYTE, (byte) cursor);
                        }
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_8.TAB_COMPLETE, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    String text = packetWrapper.read(Type.STRING);
                    packetWrapper.clearInputBuffer();
                    packetWrapper.write(Type.STRING, text);
                });
            }
        });
    }

    public void transformChunk(PacketWrapper packetWrapper) throws Exception {
        int chunkX = packetWrapper.read(Type.INT);
        int chunkZ = packetWrapper.read(Type.INT);
        boolean groundUp = packetWrapper.read(Type.BOOLEAN);
        int primaryBitMask = packetWrapper.read(Type.SHORT);
        int addBitMask = packetWrapper.read(Type.SHORT);
        int compressedSize = packetWrapper.read(Type.INT);
        CustomByteType customByteType = new CustomByteType(compressedSize);
        byte[] data = packetWrapper.read(customByteType);

        int k = 0;
        int l = 0;

        for (int j = 0; j < 16; ++j) {
            k += primaryBitMask >> j & 1;
            l += addBitMask >> j & 1;
        }

        int uncompressedSize = 12288 * k;
        uncompressedSize += 2048 * l;
        if (groundUp) {
            uncompressedSize += 256;
        }

        byte[] uncompressedData = new byte[uncompressedSize];
        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, compressedSize);
        try {
            inflater.inflate(uncompressedData);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        Chunk1_8to1_7_6_10 chunk = new Chunk1_8to1_7_6_10(uncompressedData, primaryBitMask, addBitMask, true, groundUp);

        Field field = PacketWrapperImpl.class.getDeclaredField("packetValues");
        field.setAccessible(true);
        ((List) field.get(packetWrapper)).clear();
        field = PacketWrapperImpl.class.getDeclaredField("readableObjects");
        field.setAccessible(true);
        ((ArrayDeque) field.get(packetWrapper)).clear();
        field = PacketWrapperImpl.class.getDeclaredField("inputBuffer");
        field.setAccessible(true);
        ByteBuf buffer = (ByteBuf) field.get(packetWrapper);
        buffer.clear();

        buffer.writeInt(chunkX);
        buffer.writeInt(chunkZ);
        buffer.writeBoolean(groundUp);
        buffer.writeShort(primaryBitMask);
        byte[] finaldata = chunk.get1_8Data();
        Type.VAR_INT.writePrimitive(buffer, finaldata.length);
        buffer.writeBytes(finaldata);
    }

    public void transformChunkBulk(PacketWrapper packetWrapper) throws Exception {
        short columnCount = packetWrapper.read(Type.SHORT); // short1
        int size = packetWrapper.read(Type.INT); // size
        boolean skyLightSent = packetWrapper.read(Type.BOOLEAN); // h
        int[] chunkX = new int[columnCount]; // a
        int[] chunkZ = new int[columnCount]; // b
        int[] primaryBitMask = new int[columnCount]; // c
        int[] addBitMask = new int[columnCount]; // d
        byte[][] inflatedBuffers = new byte[columnCount][]; // inflatedBuffers
        CustomByteType customByteType = new CustomByteType(size);
        byte[] buildBuffer = packetWrapper.read(customByteType); // buildBuffer

        byte[] data = new byte[196864 * columnCount]; // abyte
        Inflater inflater = new Inflater();
        inflater.setInput(buildBuffer, 0, size);

        try {
            inflater.inflate(data);
        } catch (DataFormatException ex) {
            throw new IOException("Bad compressed data format");
        } finally {
            inflater.end();
        }

        int i = 0;

        for (int j = 0; j < columnCount; ++j) {
            chunkX[j] = packetWrapper.read(Type.INT);
            chunkZ[j] = packetWrapper.read(Type.INT);
            primaryBitMask[j] = packetWrapper.read(Type.SHORT);
            addBitMask[j] = packetWrapper.read(Type.SHORT);
            int k = 0;
            int l = 0;

            int i1;
            for (i1 = 0; i1 < 16; ++i1) {
                k += primaryBitMask[j] >> i1 & 1;
                l += addBitMask[j] >> i1 & 1;
            }

            i1 = 8192 * k + 256;
            i1 += 2048 * l;
            if (skyLightSent) {
                i1 += 2048 * k;
            }

            inflatedBuffers[j] = new byte[i1];
            System.arraycopy(data, i, inflatedBuffers[j], 0, i1);
            i += i1;
        }

        Chunk1_8to1_7_6_10[] chunks = new Chunk1_8to1_7_6_10[columnCount];
        for (i = 0; i < columnCount; i++) {
            chunks[i] = new Chunk1_8to1_7_6_10(inflatedBuffers[i], primaryBitMask[i], addBitMask[i], skyLightSent,
                    true);
        }

        packetWrapper.write(Type.BOOLEAN, skyLightSent);
        packetWrapper.write(Type.VAR_INT, (int) columnCount);

        for (i = 0; i < columnCount; ++i) {
            packetWrapper.write(Type.INT, chunkX[i]);
            packetWrapper.write(Type.INT, chunkZ[i]);
            packetWrapper.write(Type.UNSIGNED_SHORT, primaryBitMask[i]);
        }

        for (i = 0; i < columnCount; ++i) {
            data = chunks[i].get1_8Data();
            customByteType = new CustomByteType(data.length);
            packetWrapper.write(customByteType, data);
        }
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        try {
            packetWrapper.writeToBuffer(buffer);
            Type.VAR_INT.readPrimitive(buffer); // Remove Packet ID
            packetWrapper.clearPacket();

            ((PacketWrapperImpl) packetWrapper).getInputBuffer().writeBytes(buffer);
        } catch (Exception e) {
            buffer.release();
        }
    }

    private WindowIDTracker windowTracker(final UserConnection connection) {
        return connection.get(WindowIDTracker.class);
    }

    private ScoreboardLineTracker scoreboardTracker(final UserConnection connection) {
        return connection.get(ScoreboardLineTracker.class);
    }

    private TablistTracker tablistTracker(final UserConnection connection) {
        return connection.get(TablistTracker.class);
    }

    private EntityTracker entityTracker(final UserConnection connection) {
        return connection.get(EntityTracker.class);
    }

    @Override
    public void init(UserConnection userConnection) {
        super.init(userConnection);

        userConnection.put(new WindowIDTracker(userConnection));
        userConnection.put(new ScoreboardLineTracker(userConnection));
        userConnection.put(new TablistTracker(userConnection));
        userConnection.put(new EntityTracker(userConnection));
    }

    public record ChatMessage(String text) {
    }
}
