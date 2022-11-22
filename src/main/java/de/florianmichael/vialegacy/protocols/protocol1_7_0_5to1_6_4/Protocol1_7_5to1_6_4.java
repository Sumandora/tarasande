/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 24.06.22, 13:55
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

package de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.util.GsonUtil;
import de.florianmichael.vialegacy.api.material.MaterialReplacement;
import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.pre_netty.DummyPrepender;
import de.florianmichael.vialegacy.pre_netty.PreNettyPacketDecoder;
import de.florianmichael.vialegacy.pre_netty.PreNettyPacketEncoder;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.provider.PreNettyProvider;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.api.EnZaProtocol;
import de.florianmichael.vialegacy.pre_netty.PreNettyConstants;
import de.florianmichael.vialegacy.protocols.base.HandshakeStorage;
import de.florianmichael.vialegacy.protocols.protocol1_7_6_10to1_7_0_5.ClientboundPackets1_7_0_5;
import de.florianmichael.vialegacy.protocols.protocol1_7_6_10to1_7_0_5.ServerboundPackets1_7_0_5;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.model.EntityAttributeModifier;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.model.EntityProperty;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.item.MaterialReplacement1_7_0_5to1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.sound.SoundRewriter1_7_0_5to1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_5to1_6_4.string.DisconnectPacketRemapper;
import io.netty.channel.ChannelPipeline;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.util.*;

public class Protocol1_7_5to1_6_4 extends EnZaProtocol<ClientboundPackets1_6_4, ClientboundPackets1_7_0_5, ServerboundPackets1_6_4, ServerboundPackets1_7_0_5> {

    private final SoundRewriter<Protocol1_7_5to1_6_4> soundRewriter = new SoundRewriter1_7_0_5to1_6_4(this);
    private final MaterialReplacement materialReplacement = new MaterialReplacement1_7_0_5to1_6_4();

    public final Map<Short, Short> pitchList = new HashMap<>();

    public Protocol1_7_5to1_6_4() {
        super(ClientboundPackets1_6_4.class, ClientboundPackets1_7_0_5.class, ServerboundPackets1_6_4.class, ServerboundPackets1_7_0_5.class);

        pitchList.put((short) 0, (short) 31);
        pitchList.put((short) 1, (short) 33);
        pitchList.put((short) 2, (short) 35);
        pitchList.put((short) 3, (short) 37);
        pitchList.put((short) 4, (short) 39);
        pitchList.put((short) 5, (short) 42);
        pitchList.put((short) 6, (short) 44);
        pitchList.put((short) 7, (short) 47);
        pitchList.put((short) 8, (short) 50);
        pitchList.put((short) 9, (short) 52);
        pitchList.put((short) 10, (short) 56);
        pitchList.put((short) 11, (short) 59);
        pitchList.put((short) 12, (short) 63);
        pitchList.put((short) 13, (short) 66);
        pitchList.put((short) 14, (short) 70);
        pitchList.put((short) 15, (short) 74);
        pitchList.put((short) 16, (short) 79);
        pitchList.put((short) 17, (short) 84);
        pitchList.put((short) 18, (short) 89);
        pitchList.put((short) 19, (short) 94);
        pitchList.put((short) 20, (short) 100);
        pitchList.put((short) 21, (short) 105);
        pitchList.put((short) 22, (short) 112);
        pitchList.put((short) 23, (short) 118);
        pitchList.put((short) 24, (short) 126);
    }

    @Override
    protected void registerPackets() {
        this.soundRewriter().register1_7_5NamedSound(ClientboundPackets1_6_4.NAMED_SOUND);

        // Login Start
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    final ProtocolInfo info = pw.user().getProtocolInfo();

                    final int protocol = Math.abs(info.getServerProtocolVersion()); // Bypass ViaVersion, see @VersionUtil.java

                    final String username = pw.read(Type.STRING);
                    info.setUsername(username);
                    //noinspection deprecation
                    pw.setId(ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId());
                    pw.clearPacket();

                    pw.write(Type.UNSIGNED_BYTE, (short) protocol);
                    pw.write(Types1_6_4.STRING, username);

                    final HandshakeStorage handshakeStorage = pw.user().get(HandshakeStorage.class);
                    if (handshakeStorage != null) {
                        pw.write(Types1_6_4.STRING, handshakeStorage.hostname);
                        pw.write(Type.INT, handshakeStorage.port);
                    }
                });
            }
        });

        // Status Request
        this.registerServerbound(State.STATUS, 0xFE, 0x00, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(pw -> {
                    // Server List Ping
                    pw.write(Type.BYTE, (byte) 0x01);
                    // Plugin Message
                    pw.write(Type.UNSIGNED_BYTE, (short) 0xFA);
                    pw.write(Types1_6_4.STRING, "MC|PingHost");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(baos);
                    out.writeByte(pw.user().getProtocolInfo().getServerProtocolVersion());
                    InetSocketAddress addr = (InetSocketAddress) pw.user().getChannel().remoteAddress();
                    String ip = addr.getHostString();
                    int port = addr.getPort();
                    out.writeShort(ip.length());
                    for (int i = 0; i < ip.length(); i++) {
                        out.writeChar(ip.charAt(i));
                    }
                    out.writeInt(port);
                    out.close();
                    pw.write(Types1_7_6_10.BYTEARRAY, baos.toByteArray());
                    pw.sendToServer(Protocol1_7_5to1_6_4.class);
                });
            }
        });

        this.cancelServerbound(State.STATUS, 0x01);

        this.registerServerbound(ServerboundPackets1_7_0_5.CHAT_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING);
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.INTERACT_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> pw.write(Type.INT, 0));
                map(Type.INT);
                handler((pw) -> pw.write(Type.BOOLEAN, pw.read(Type.BYTE) == 1)); // Fix mouse buttons
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.PLAYER_POSITION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                map(Type.DOUBLE); // Y/Stance
                map(Type.DOUBLE); // Z-Position
                map(Type.BOOLEAN); // On Ground
                handler((pw) -> {
                    pw.set(Type.DOUBLE, 2, pw.get(Type.DOUBLE, 1) + 1.62D);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                map(Type.DOUBLE); // Y/Stance
                map(Type.DOUBLE); // Z-Position
                map(Type.FLOAT); // Yaw
                map(Type.FLOAT); // Pitch
                map(Type.BOOLEAN); // On Ground
                handler((pw) -> {
                    pw.set(Type.DOUBLE, 2, pw.get(Type.DOUBLE, 1) + 1.62D);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.WINDOW_CONFIRMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE, Type.BYTE); // Window id
                map(Type.SHORT); // Action number
                map(Type.BOOLEAN); // Accepted
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.CLIENT_SETTINGS, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING); // Locale
                map(Type.BYTE); // View Distance
                map(Type.BYTE); // Chat flags
                handler((pw) -> {
                    boolean chatColors = pw.read(Type.BOOLEAN);
                    byte flags = pw.get(Type.BYTE, 1);
                    flags = (byte) (flags | (chatColors ? 1 : 0) << 3);
                    pw.set(Type.BYTE, 1, flags);
                });
                map(Type.BYTE); // Difficulty
                map(Type.BOOLEAN); // Show Cape
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.CLIENT_STATUS, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Action-ID
                handler((pw) -> {
                    int action = pw.get(Type.VAR_INT, 0);
                    if (action == 0) {
                        action = 1;
                    } else {
                        pw.cancel();
                        return;
                    }
                    pw.set(Type.VAR_INT, 0, action);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.PLUGIN_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING);
                map(Type.SHORT);
                map(Type.REMAINING_BYTES);
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_5.UPDATE_SIGN, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // X-Position
                map(Type.SHORT); // Y-Position
                map(Type.INT); // Z-Position

                map(Type.STRING, Types1_6_4.STRING); // Line-1
                map(Type.STRING, Types1_6_4.STRING); // Line-2
                map(Type.STRING, Types1_6_4.STRING); // Line-3
                map(Type.STRING, Types1_6_4.STRING); // Line-4
            }
        });

        this.registerServerbound(State.LOGIN, 0xFC, 0x01, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.SHORT_BYTE_ARRAY);
                map(Type.SHORT_BYTE_ARRAY);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_EQUIPMENT, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.INT); // Entity ID
                map(Type.SHORT); // Slot
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM); // Item
                handler(wrapper -> wrapper.set(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0, materialReplacement().replace(wrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0))));
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SET_SLOT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE);
                map(Type.SHORT);
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                handler(wrapper -> wrapper.set(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0, materialReplacement().replace(wrapper.get(Types1_7_6_10.COMPRESSED_NBT_ITEM, 0))));
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.WINDOW_ITEMS, new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(packetWrapper -> {
                    packetWrapper.passthrough(Type.UNSIGNED_BYTE); // Window ID
                    Item[] items = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY);

                    for (int i = 0; i < items.length; i++) {
                        items[i] = materialReplacement().replace(items[i]);
                    }
                    packetWrapper.write(Type.ITEM_ARRAY, items);  // Items
                });
            }
        });

        // Disconnect
        this.registerClientbound(State.LOGIN, 0xFF, 0x00, new DisconnectPacketRemapper());
        this.registerClientbound(ClientboundPackets1_6_4.DISCONNECT, new DisconnectPacketRemapper());

        // Encryption Request
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_6_4.SERVER_AUTH_DATA.getId(), 0x01, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // Server-Id
                map(Types1_7_6_10.BYTEARRAY); // PublicKey
                map(Types1_7_6_10.BYTEARRAY); // VerifyToken
                handler((pw) -> {
                    String serverId = pw.get(Type.STRING, 0);
                    // If server id is equal to '-' than the server is offline.
                    byte[] publicKey = pw.get(Types1_7_6_10.BYTEARRAY, 0);
                    byte[] verifyToken = pw.get(Types1_7_6_10.BYTEARRAY, 1);

                    if (serverId.equals("-")) {
                        pw.cancel();
                        // Write Login Success packet
                        PacketWrapper login = new PacketWrapperImpl(ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId(), null, pw.user());
                        login.write(Type.STRING, UUID.randomUUID().toString().replace("-", ""));
                        login.write(Type.STRING, pw.user().getProtocolInfo().getUsername());
                        login.send(Protocol1_7_5to1_6_4.class);

                        // Change state to play
                        pw.user().getProtocolInfo().setState(State.PLAY);

                        // Send client statuses packet
                        PacketWrapper join = new PacketWrapperImpl(0xCD, null, pw.user());
                        join.write(Type.BYTE, (byte) 0);
                        join.sendToServer(Protocol1_7_5to1_6_4.class);
                    } else {
                        pw.cancel();

                        PacketWrapper request = new PacketWrapperImpl(0x01, null, pw.user());
                        request.write(Type.STRING, serverId);

                        request.write(Type.SHORT_BYTE_ARRAY, publicKey);
                        request.write(Type.SHORT_BYTE_ARRAY, verifyToken);

                        request.send(Protocol1_7_5to1_6_4.class);
                    }
                });
            }
        });

        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_6_4.SHARED_KEY.getId(), ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((packetWrapper) -> {
                    packetWrapper.read(Type.SHORT);
                    packetWrapper.read(Type.SHORT);

                    final ProtocolInfo info = packetWrapper.user().getProtocolInfo();

                    packetWrapper.write(Type.STRING, UUID.nameUUIDFromBytes(("OfflinePlayer:" + info.getUsername()).getBytes()).toString().replace("-", ""));
                    packetWrapper.write(Type.STRING, info.getUsername());

                    final PreNettyProvider preNettyProvider = Via.getManager().getProviders().get(PreNettyProvider.class);
                    if (preNettyProvider != null) {
                        final ChannelPipeline pipeline = packetWrapper.user().getChannel().pipeline();
                        pipeline.addBefore(PreNettyConstants.DECODER, preNettyProvider.decryptKey(), preNettyProvider.decryptor());
                        pipeline.addBefore(PreNettyConstants.ENCODER, preNettyProvider.encryptKey(), preNettyProvider.encryptor());
                    }

                    PacketWrapper add = new PacketWrapperImpl(205, null, packetWrapper.user()); //Packet205ClientCommand
                    add.write(Type.BYTE, (byte) 0);
                    try {
                        add.sendToServer(Protocol1_7_5to1_6_4.class);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    info.setState(State.PLAY);
                });
            }
        });

        // Pong
        this.cancelClientbound(State.STATUS, 0x01);

        // Disconnect Status
        this.registerClientbound(State.STATUS, 0xFF, 0x00, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    String reason = pw.read(Types1_6_4.STRING);
                    String[] split = reason.split("\0");
                    if (!split[0].equals("§1") || split.length != 6) {
                        pw.user().disconnect("Invalid response!");
                        pw.cancel();
                    }
                    try {
                        int protocol = Integer.parseInt(split[1]);
                        String version = split[2];
                        String motd = split[3];
                        int online = Integer.parseInt(split[4]);
                        int max = Integer.parseInt(split[5]);

                        JsonObject main = new JsonObject();

                        // Version
                        JsonObject obj = new JsonObject();
                        obj.addProperty("name", version);
                        obj.addProperty("protocol", protocol);
                        main.add("version", obj);

                        // Players
                        obj = new JsonObject();
                        obj.addProperty("max", max);
                        obj.addProperty("online", online);
                        obj.add("sample", new JsonArray());
                        main.add("players", obj);

                        // Description
                        obj = new JsonObject();
                        obj.addProperty("text", motd);
                        main.add("description", obj);
                        String json = GsonUtil.getGson().toJson(main);
                        pw.clearPacket();
                        pw.write(Type.STRING, json);
                    } catch (Throwable t) {
                        pw.cancel();
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_ANIMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Animation-Id
                handler((pw) -> {
                    short animId = pw.get(Type.UNSIGNED_BYTE, 0);
                    if (animId == 0) {
                        pw.cancel();
                        return;
                    } else if (animId >= 1 && animId <= 3)
                        animId--;
                    else if (animId == 5)
                        animId = 3;
                    else if (animId == 6 || animId == 7)
                        animId -= 2;

                    pw.set(Type.UNSIGNED_BYTE, 0, animId);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ATTACH_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Type.INT); // Vehicle-Id
                map(Type.UNSIGNED_BYTE);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_METADATA, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // Entity-Id
                map(Types1_6_4.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_PROPERTIES, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    final int entityId = pw.read(Type.INT);
                    final int count = pw.read(Type.INT);

                    final List<EntityProperty> list = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        final EntityProperty prop = new EntityProperty();
                        prop.key = pw.read(Types1_6_4.STRING);
                        prop.value = pw.read(Type.DOUBLE); // Value

                        short modifierCount = pw.passthrough(Type.SHORT); // Modifier count

                        final List<EntityAttributeModifier> modifiers = new ArrayList<>(modifierCount);

                        for (int k = 0; k < modifierCount; k++) {
                            final EntityAttributeModifier mod = new EntityAttributeModifier();
                            mod.uuid = pw.read(Type.UUID);
                            mod.amount = pw.read(Type.DOUBLE);
                            mod.operation = pw.read(Type.BYTE);
                            modifiers.add(mod);
                        }
                        prop.modifiers = modifiers;
                        list.add(prop);
                    }
                    pw.clearPacket();

                    pw.write(Type.INT, entityId);
                    pw.write(Type.INT, list.size());

                    for (EntityProperty prop : list) {
                        pw.write(Type.STRING, prop.key);
                        pw.write(Type.DOUBLE, prop.value);
                        pw.write(Type.SHORT, (short) prop.modifiers.size());

                        for (int k = 0; k < prop.modifiers.size(); k++) {
                            final EntityAttributeModifier mod = prop.modifiers.get(k);

                            pw.write(Type.UUID, mod.uuid);
                            pw.write(Type.DOUBLE, mod.amount);
                            pw.write(Type.BYTE, mod.operation);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.JOIN_GAME, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    int entityId = pw.read(Type.INT);
                    String levelType = pw.read(Types1_6_4.STRING);
                    short gamemode = pw.read(Type.BYTE);
                    byte dimension = pw.read(Type.BYTE);
                    byte difficulty = pw.read(Type.BYTE);
                    pw.read(Type.BYTE); // Unused (WorldHeight)
                    byte maxPlayers = pw.read(Type.BYTE);

                    pw.clearPacket();
                    pw.write(Type.INT, entityId);
                    pw.write(Type.UNSIGNED_BYTE, gamemode);
                    pw.write(Type.BYTE, dimension);
                    pw.write(Type.UNSIGNED_BYTE, (short) difficulty);
                    pw.write(Type.UNSIGNED_BYTE, (short) maxPlayers);
                    pw.write(Type.STRING, levelType);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.CHAT_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING);
                handler((pw) -> {
                    String message = pw.get(Type.STRING, 0);
                    try {
                        BaseComponent[] components = ComponentSerializer.parse(message.replace("\"using\":", "\"with\":"));
                        pw.set(Type.STRING, 0, ComponentSerializer.toString(components[0]));
                    } catch (Throwable t) {
                        pw.set(Type.STRING, 0, ComponentSerializer.toString(new TextComponent(message)));
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.RESPAWN, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // Dimension
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Difficulty
                map(Type.BYTE, Type.UNSIGNED_BYTE); // GameMode
                handler((pw) -> pw.read(Type.SHORT)); // WorldHeight
                map(Types1_6_4.STRING, Type.STRING); // LevelType
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.PLUGIN_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.HELD_ITEM_CHANGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.SHORT, Type.BYTE); // Slot
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.USE_BED, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.INT); // Entity-Id
                map(Type.BYTE, Type.NOTHING); // Unknown
                map(Type.INT); // X-Position
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Y-Position
                map(Type.INT); // Z-Position
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.GAME_EVENT, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Reason
                map(Type.BYTE, Type.FLOAT); // Value
                handler((pw) -> {
                    short id = pw.get(Type.UNSIGNED_BYTE, 0);
                    if (id == 1)
                        id = 2;
                    else if (id == 2)
                        id = 1;
                    pw.set(Type.UNSIGNED_BYTE, 0, id);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                handler((pw) -> pw.read(Type.DOUBLE)); // Y/Stance
                map(Type.DOUBLE); // Z-Position
                map(Type.FLOAT); // Yaw
                map(Type.FLOAT); // Pitch
                map(Type.BOOLEAN); // On Ground
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.OPEN_WINDOW, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Window id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Inventory type
                map(Types1_6_4.STRING, Type.STRING); // Window title
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Number of slots
                map(Type.BOOLEAN); // Use window title
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.CLOSE_WINDOW, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Window id
            }
        });

        this.cancelClientbound(ClientboundPackets1_6_4.CREATIVE_INVENTORY_ACTION);

        this.registerClientbound(ClientboundPackets1_6_4.WINDOW_ITEMS, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Window id
                map(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY); // Item list
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.WINDOW_PROPERTY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Window id
                map(Type.SHORT); // Property
                map(Type.SHORT); // Value
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.WINDOW_CONFIRMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Window id
                map(Type.SHORT); // Action number
                map(Type.BOOLEAN); // Accepted
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.OPEN_SIGN_EDITOR, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE, Type.NOTHING); // TileEntity-Id

                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.STATISTICS, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    int id = pw.read(Type.INT);
                    int amount = pw.read(Type.INT);

                    pw.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_INFO, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // Player name
                map(Type.BOOLEAN); // Online
                map(Type.SHORT); // Ping
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.TAB_COMPLETE, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    String text = pw.read(Types1_6_4.STRING);
                    pw.write(Type.STRING_ARRAY, text.split("\u0000"));
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SCOREBOARD_OBJECTIVE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // name
                map(Types1_6_4.STRING, Type.STRING); // value
                map(Type.BYTE); // Create / remove
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.UPDATE_SCORE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // Item Name
                map(Type.BYTE); // Update / Remove
                map(Types1_6_4.STRING, Type.STRING); // Score name
                map(Type.INT); // Value
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.DISPLAY_SCOREBOARD, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.BYTE); // Position
                map(Types1_6_4.STRING, Type.STRING); // Score name
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.TEAMS, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    String name = pw.read(Types1_6_4.STRING);
                    byte mode = pw.read(Type.BYTE);
                    String displayName = null;
                    String prefix = null;
                    String suffix = null;
                    byte friendlyFire = 0;
                    String[] players = null;
                    if (mode == 0 || mode == 2) {
                        displayName = pw.read(Types1_6_4.STRING);
                        prefix = pw.read(Types1_6_4.STRING);
                        suffix = pw.read(Types1_6_4.STRING);
                        friendlyFire = pw.read(Type.BYTE);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        players = new String[pw.read(Type.SHORT)];
                        for (int i = 0; i < players.length; i++)
                            players[i] = pw.read(Types1_6_4.STRING);
                    }
                    pw.clearPacket();

                    pw.write(Type.STRING, name);
                    pw.write(Type.BYTE, mode);
                    if (mode == 0 || mode == 2) {
                        pw.write(Type.STRING, displayName);
                        pw.write(Type.STRING, prefix);
                        pw.write(Type.STRING, suffix);
                        pw.write(Type.BYTE, friendlyFire);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        pw.write(Type.SHORT, (short) players.length);
                        for (int i = 0; i < players.length; i++)
                            pw.write(Type.STRING, players[i]);
                    }
                    pw.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PLAYER, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                handler((pw) -> {
                    String name = pw.read(Types1_6_4.STRING);
                    pw.write(Type.STRING, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()).toString().replace("-", ""));
                    pw.write(Type.STRING, name);
                });
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Yaw
                map(Type.BYTE); // Pitch
                map(Type.SHORT); // Current Item
                map(Types1_6_4.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.BYTE); // Type
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Yaw
                map(Type.BYTE); // Pitch
                handler((pw) -> {
                    int throwerEntityId = pw.passthrough(Type.INT);
                    if (throwerEntityId > 0) {
                        pw.passthrough(Type.SHORT);
                        pw.passthrough(Type.SHORT);
                        pw.passthrough(Type.SHORT);
                    }
                }); // Object Data
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_MOB, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Type
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Pitch
                map(Type.BYTE); // Head Pitch
                map(Type.BYTE); // Yaw
                map(Type.SHORT); // Velocity X
                map(Type.SHORT); // Velocity Y
                map(Type.SHORT); // Velocity Z
                map(Types1_6_4.METADATA_LIST, Types1_7_6_10.METADATA_LIST);
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PAINTING, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Types1_6_4.STRING, Type.STRING); // Name of painting
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.INT); // Direction
                handler((pw) -> {
                    int direction = pw.get(Type.INT, 3);
                    int modX = 0;
                    int modZ = 0;
                    switch (direction) {
                        case 0 -> modZ = 1;
                        case 1 -> modX = -1;
                        case 2 -> modZ = -1;
                        case 3 -> modX = 1;
                    }
                    pw.set(Type.INT, 0, pw.get(Type.INT, 0) + modX);
                    pw.set(Type.INT, 2, pw.get(Type.INT, 2) + modZ);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_EXPERIENCE_ORB, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.SHORT); // Count
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_GLOBAL_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.BYTE); // Entity-Type
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_CHANGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // X-Position
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.SHORT, Type.VAR_INT); // Block-Id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Block Metadata
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_ACTION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // X-Position
                map(Type.SHORT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Value-1
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Value-2
                map(Type.SHORT, Type.VAR_INT); // Block-Type

                handler((pw) -> {
                    List<String> list = Arrays.asList("harp", "bd", "snare", "hat", "bassattack");
                    int instrument = pw.get(Type.UNSIGNED_BYTE, 0);
                    if (instrument < 0 || instrument >= list.size())
                        instrument = 0;
                    PacketWrapper sound = PacketWrapper.create(ClientboundPackets1_6_4.ENTITY_EFFECT, pw.user());
                    sound.write(Type.STRING, "note." + list.get(instrument));
                    sound.write(Type.INT, pw.get(Type.INT, 0) * 8);
                    sound.write(Type.INT, pw.get(Type.SHORT, 0) * 8);
                    sound.write(Type.INT, pw.get(Type.INT, 1) * 8);
                    sound.write(Type.FLOAT, 3.0F);
                    short pitch = pw.get(Type.UNSIGNED_BYTE, 1);
                    if (pitchList.containsKey(pitch)) {
                        pitch = pitchList.get(pitch);
                    }
                    sound.write(Type.UNSIGNED_BYTE, pitch);
                    sound.send(Protocol1_7_5to1_6_4.class);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.BLOCK_BREAK_ANIMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.INT); // X-Position
                map(Type.INT); // Y-Position
                map(Type.INT); // Z-Position
                map(Type.BYTE); // Destroy Stage
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.EXPLOSION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE, Type.FLOAT); // X-Position
                map(Type.DOUBLE, Type.FLOAT); // Y-Position
                map(Type.DOUBLE, Type.FLOAT); // Z-Position
                map(Type.FLOAT); // Radius
                map(Type.INT); // Record count
                handler((pw) -> {
                    int count = pw.get(Type.INT, 0);

                    for (int i = 0; i < count; i++) {
                        pw.passthrough(Type.BYTE); // X-Offset
                        pw.passthrough(Type.BYTE); // Y-Offset
                        pw.passthrough(Type.BYTE); // Z-Offset
                    }
                });
                map(Type.FLOAT); // Player Motion X
                map(Type.FLOAT); // Player Motion Y
                map(Type.FLOAT); // Player Motion Z
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PARTICLE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // Particle name
                map(Type.FLOAT); // X-Position
                map(Type.FLOAT); // Y-Position
                map(Type.FLOAT); // Z-Position
                map(Type.FLOAT); // Offset X
                map(Type.FLOAT); // Offset Y
                map(Type.FLOAT); // Offset Z
                map(Type.FLOAT); // Particle Data
                map(Type.INT); // Particle count
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.MAP_DATA, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler((pw) -> {
                    short type = pw.read(Type.SHORT);
                    int id = pw.read(Type.SHORT);
                    byte[] data = pw.read(Types1_7_6_10.BYTEARRAY);
                    pw.clearPacket();

                    pw.write(Type.VAR_INT, id);
                    pw.write(Types1_7_6_10.BYTEARRAY, data);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.UPDATE_SIGN, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT); // X-Position
                map(Type.SHORT); // Y-Position
                map(Type.INT); // Z-Position

                map(Types1_6_4.STRING, Type.STRING); // Line-1
                map(Types1_6_4.STRING, Type.STRING); // Line-2
                map(Types1_6_4.STRING, Type.STRING); // Line-3
                map(Types1_6_4.STRING, Type.STRING); // Line-4
            }
        });
    }

    @Override
    public SoundRewriter soundRewriter() {
        return this.soundRewriter;
    }

    @Override
    public MaterialReplacement materialReplacement() {
        return this.materialReplacement;
    }


    @Override
    public void init(UserConnection userConnection) {
        userConnection.put(new ClientWorld(userConnection));
        userConnection.put(new SplitterTracker(userConnection, ClientboundPackets1_6_4.values(), ClientboundLoginPackets1_6_4.values()));

        final PreNettyProvider preNettyProvider = Via.getManager().getProviders().get(PreNettyProvider.class);
        if (preNettyProvider != null) {
            final ChannelPipeline pipeline = userConnection.getChannel().pipeline();

            pipeline.addBefore(preNettyProvider.splitterKey(), PreNettyConstants.DECODER, new PreNettyPacketDecoder(userConnection));
            pipeline.addBefore(preNettyProvider.prependerKey(), PreNettyConstants.ENCODER, new PreNettyPacketEncoder());

            pipeline.replace(preNettyProvider.prependerKey(), preNettyProvider.prependerKey(), new DummyPrepender());
            pipeline.remove(preNettyProvider.splitterKey());
        }
    }
}
