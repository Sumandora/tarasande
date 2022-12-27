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
/*
  --FLORIAN MICHAEL PRIVATE LICENCE v1.2--

  This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
  any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
  file / project is prohibited. It requires in that use a written permission with official signature of the owner
  "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
  cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
  The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
  that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
  the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.

  Changelog:
      v1.0:
          Added License
      v1.1:
          Ownership withdrawn
      v1.2:
          Version-independent validity and automatic renewal
 */

package de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonArray;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocol.packet.PacketWrapperImpl;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.util.GsonUtil;
import de.florianmichael.vialegacy.api.material.MaterialReplacement;
import de.florianmichael.vialegacy.api.sound.SoundRewriter;
import de.florianmichael.vialegacy.api.viaversion.EnZaProtocol;
import de.florianmichael.vialegacy.protocol.SplitterTracker;
import de.florianmichael.vialegacy.protocols.protocol1_6_4.ClientboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_6_4.ServerboundLoginPackets1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_6_4.storage.HandshakeStorage;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.item.MaterialReplacement1_7_0_1_preto1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model.EntityAttributeModifier;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model.EntityProperty;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model.PluginMessage;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.model.ViewDistance;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.provider.EncryptionProvider;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.provider.UUIDProvider;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.sound.NoteBlockPitch;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.sound.SoundRewriter1_7_0_1_preto1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.storage.PluginMessageStorage;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.string.DisconnectPacketRemapper;
import de.florianmichael.vialegacy.protocols.protocol1_7_0_1_preto1_6_4.type.Types1_6_4;
import de.florianmichael.vialegacy.protocols.protocol1_7_2_5to1_7_0_1_pre.ClientboundPackets1_7_0_1_pre;
import de.florianmichael.vialegacy.protocols.protocol1_7_2_5to1_7_0_1_pre.ServerboundPackets1_7_0_1_pre;
import de.florianmichael.vialegacy.protocols.protocol1_8_0_9to1_7_6_10.type.Types1_7_6_10;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Protocol1_7_0_1_preto1_6_4 extends EnZaProtocol<ClientboundPackets1_6_4, ClientboundPackets1_7_0_1_pre, ServerboundPackets1_6_4, ServerboundPackets1_7_0_1_pre> {

    private final SoundRewriter<Protocol1_7_0_1_preto1_6_4> soundRewriter = new SoundRewriter1_7_0_1_preto1_6_4(this);
    private final MaterialReplacement materialReplacement = new MaterialReplacement1_7_0_1_preto1_6_4();

    public Protocol1_7_0_1_preto1_6_4() {
        super(ClientboundPackets1_6_4.class, ClientboundPackets1_7_0_1_pre.class, ServerboundPackets1_6_4.class, ServerboundPackets1_7_0_1_pre.class);
    }

    // This method switches the current packet state from login to play
    public static void setupPlayState(final UserConnection connection) throws Exception {
        final UUIDProvider uuidProvider = Via.getManager().getProviders().get(UUIDProvider.class);
        if (uuidProvider == null) {
            throw new IllegalStateException("ViaProviders didn't fired all load????");
        }

        // Finishing modern login and forcing the game to switching to play state
        final PacketWrapper loginHello = new PacketWrapperImpl(ClientboundLoginPackets.GAME_PROFILE.getId(), null, connection);
        loginHello.write(Type.STRING, uuidProvider.getPlayerUuid().toString().replace("-", ""));
        loginHello.write(Type.STRING, connection.getProtocolInfo().getUsername());

        loginHello.send(Protocol1_7_0_1_preto1_6_4.class);

        // Setting ViaVersion's state also to play
        connection.getProtocolInfo().setState(State.PLAY);
    }

    @Override
    protected void registerPackets() {
        this.soundRewriter().register1_7_0_1_preNamedSound(ClientboundPackets1_6_4.NAMED_SOUND);

        // Login Start
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    // Cancelling the packet
                    wrapper.cancel();

                    // Tracking the Username and UUID
                    final String username = wrapper.read(Type.STRING);
                    wrapper.user().getProtocolInfo().setUsername(username);
                    final UUIDProvider uuidProvider = Via.getManager().getProviders().get(UUIDProvider.class);
                    if (uuidProvider != null) {
                        wrapper.user().getProtocolInfo().setUuid(uuidProvider.getPlayerUuid());
                    }

                    // Sending Handshake
                    final PacketWrapper clientProtocol = new PacketWrapperImpl(ServerboundLoginPackets1_6_4.CLIENT_PROTOCOL.getId(), null, wrapper.user());
                    clientProtocol.write(Type.UNSIGNED_BYTE, (short) Math.abs(wrapper.user().getProtocolInfo().getServerProtocolVersion()));
                    clientProtocol.write(Types1_6_4.STRING, username);
                    final HandshakeStorage handshakeStorage = wrapper.user().get(HandshakeStorage.class);
                    if (handshakeStorage != null) {
                        clientProtocol.write(Types1_6_4.STRING, handshakeStorage.hostname);
                        clientProtocol.write(Type.INT, handshakeStorage.port);
                    }

                    clientProtocol.sendToServer(Protocol1_7_0_1_preto1_6_4.class);
                });
            }
        });

        // Saving Plugin messages from login and sending them after join game
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.PLUGIN_MESSAGE.getId(), ClientboundPackets1_6_4.PLUGIN_MESSAGE.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final PluginMessageStorage pluginMessageStorage = wrapper.user().get(PluginMessageStorage.class);
                    if (pluginMessageStorage != null) {
                        final String channel = wrapper.read(Types1_6_4.STRING);
                        final byte[] message = wrapper.read(Types1_7_6_10.BYTEARRAY);
                        wrapper.cancel();

                        if (message != null && message.length > 0) {
                            pluginMessageStorage.getPluginMessages().add(new PluginMessage(channel, message));
                        }
                    }
                });
            }
        });

        // Protocol Support servers doesn't response with an empty SharedKey, they send a join game packet in the login state
        this.registerClientbound(State.LOGIN, ClientboundPackets1_6_4.JOIN_GAME.getId(), ClientboundPackets1_6_4.JOIN_GAME.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    // Cancel the original packet
                    wrapper.cancel();

                    // Sending login success to force the game to switch to play state
                    setupPlayState(wrapper.user());

                    // read all joinGame fields and re-send the packet in play state
                    final int entityId = wrapper.read(Type.INT);
                    final String levelType = wrapper.read(Types1_6_4.STRING);
                    final short gameMode = wrapper.read(Type.BYTE);
                    final byte dimension = wrapper.read(Type.BYTE);
                    final byte difficulty = wrapper.read(Type.BYTE);
                    wrapper.read(Type.BYTE);
                    final byte maxPlayers = wrapper.read(Type.BYTE);

                    final PacketWrapper joinGame = PacketWrapper.create(ClientboundPackets1_6_4.JOIN_GAME, wrapper.user());
                    joinGame.write(Type.INT, entityId);
                    joinGame.write(Type.UNSIGNED_BYTE, gameMode);
                    joinGame.write(Type.BYTE, dimension);
                    joinGame.write(Type.UNSIGNED_BYTE, (short) difficulty);
                    joinGame.write(Type.UNSIGNED_BYTE, (short) maxPlayers);
                    joinGame.write(Type.STRING, levelType);

                    joinGame.send(Protocol1_7_0_1_preto1_6_4.class);

                    // Re-sync all missing PluginMessages in case the server doesn't test us and they're important...
                    final PluginMessageStorage pluginMessageStorage = wrapper.user().get(PluginMessageStorage.class);
                    if (pluginMessageStorage != null) {
                        pluginMessageStorage.reSyncPluginMessages(wrapper.user());
                    }
                });
            }
        });

        // Encryption Key (ServerAuthData / Encryption Request Response) -> Shared Key (ServerAuthData Response)
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets1_6_4.SHARED_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId());

        // Server Auth Data 1.6 -> Login Hello 1.7 (S -> C)
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_6_4.SERVER_AUTH_DATA.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING); // Server-Id
                map(Type.SHORT_BYTE_ARRAY); // PublicKey
                map(Type.SHORT_BYTE_ARRAY); // VerifyToken
            }
        });

        // Shared Key -> Nothing
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets1_6_4.SHARED_KEY.getId(), ClientboundLoginPackets1_6_4.SHARED_KEY.getId(), new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    // We don't need this packet anymore
                    wrapper.cancel();

                    // Sending login success to force the game to switch to play state
                    setupPlayState(wrapper.user());

                    // Enabling clientside encryption for the connection
                    final EncryptionProvider encryptionProvider = Via.getManager().getProviders().get(EncryptionProvider.class);
                    if (encryptionProvider != null) {
                        encryptionProvider.encryptConnection();
                    }

                    // Login Success 1.6.4 (C -> S)
                    final PacketWrapper clientCommand = new PacketWrapperImpl(ServerboundPackets1_6_4.CLIENT_STATUS, null, wrapper.user());
                    clientCommand.write(Type.BYTE, (byte) 0);

                    clientCommand.sendToServer(Protocol1_7_0_1_preto1_6_4.class);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.JOIN_GAME, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    final int entityId = wrapper.read(Type.INT);
                    final String levelType = wrapper.read(Types1_6_4.STRING);
                    final short gameMode = wrapper.read(Type.BYTE);
                    final byte dimension = wrapper.read(Type.BYTE);
                    final byte difficulty = wrapper.read(Type.BYTE);
                    wrapper.read(Type.BYTE);
                    final byte maxPlayers = wrapper.read(Type.BYTE);

                    wrapper.clearPacket();

                    wrapper.write(Type.INT, entityId);
                    wrapper.write(Type.UNSIGNED_BYTE, gameMode);
                    wrapper.write(Type.BYTE, dimension);
                    wrapper.write(Type.UNSIGNED_BYTE, (short) difficulty);
                    wrapper.write(Type.UNSIGNED_BYTE, (short) maxPlayers);
                    wrapper.write(Type.STRING, levelType);

                    // Re-sync all missing PluginMessages in case the server doesn't test us and they're important...
                    final PluginMessageStorage pluginMessageStorage = wrapper.user().get(PluginMessageStorage.class);
                    if (pluginMessageStorage != null) {
                        pluginMessageStorage.reSyncPluginMessages(wrapper.user());
                    }
                });
            }
        });

        // Status Request
        this.registerServerbound(State.STATUS, 0xFE, 0x00, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    // Server List Ping
                    wrapper.write(Type.BYTE, (byte) 0x01);
                    // Plugin Message
                    wrapper.write(Type.UNSIGNED_BYTE, (short) 0xFA);
                    wrapper.write(Types1_6_4.STRING, "MC|PingHost");

                    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    final DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                    out.writeByte(wrapper.user().getProtocolInfo().getServerProtocolVersion());

                    final InetSocketAddress addr = (InetSocketAddress) wrapper.user().getChannel().remoteAddress();
                    final String ip = addr.getHostString();

                    out.writeShort(ip.length());
                    for (int i = 0; i < ip.length(); i++) {
                        out.writeChar(ip.charAt(i));
                    }
                    out.writeInt(addr.getPort());
                    out.close();
                    wrapper.write(Types1_7_6_10.BYTEARRAY, byteArrayOutputStream.toByteArray());
                    wrapper.sendToServer(Protocol1_7_0_1_preto1_6_4.class);
                });
            }
        });

        this.cancelServerbound(State.STATUS, 0x01);

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.CHAT_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING);
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.INTERACT_ENTITY, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> wrapper.write(Type.INT, 0));
                map(Type.INT);
                handler(wrapper -> wrapper.write(Type.BOOLEAN, wrapper.read(Type.BYTE) == 1)); // Fix mouse buttons
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.PLAYER_POSITION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                map(Type.DOUBLE); // Y/Stance
                map(Type.DOUBLE); // Z-Position
                map(Type.BOOLEAN); // On Ground

                // Add Offset
                handler(wrapper -> wrapper.set(Type.DOUBLE, 2, wrapper.get(Type.DOUBLE, 1) + 1.62D));
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.PLAYER_POSITION_AND_ROTATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                map(Type.DOUBLE); // Y/Stance
                map(Type.DOUBLE); // Z-Position
                map(Type.FLOAT); // Yaw
                map(Type.FLOAT); // Pitch
                map(Type.BOOLEAN); // On Ground

                // Add Offset
                handler(wrapper -> wrapper.set(Type.DOUBLE, 2, wrapper.get(Type.DOUBLE, 1) + 1.62D));
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.WINDOW_CONFIRMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.UNSIGNED_BYTE, Type.BYTE); // Window id
                map(Type.SHORT); // Action number
                map(Type.BOOLEAN); // Accepted
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.CLIENT_SETTINGS, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING); // Locale
                handler(wrapper -> wrapper.write(Type.BYTE, (byte) ViewDistance.approximateDistance(wrapper.read(Type.BYTE)).ordinal())); // View distance
                map(Type.BYTE); // Chat flags
                handler(wrapper -> {
                    boolean chatColors = wrapper.read(Type.BOOLEAN);
                    byte flags = wrapper.get(Type.BYTE, 1);
                    flags = (byte) (flags | (chatColors ? 1 : 0) << 3);
                    wrapper.set(Type.BYTE, 1, flags);
                });
                map(Type.BYTE); // Difficulty
                map(Type.BOOLEAN); // Show Cape
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.CLIENT_STATUS, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.VAR_INT); // Action-ID
                handler(wrapper -> {
                    int action = wrapper.get(Type.VAR_INT, 0);
                    if (action == 0) {
                        action = 1;
                    } else {
                        wrapper.cancel();
                        return;
                    }
                    wrapper.set(Type.VAR_INT, 0, action);
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.PLUGIN_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.STRING, Types1_6_4.STRING);
                map(Type.SHORT);
                map(Type.REMAINING_BYTES);

                handler(wrapper -> {
                    final String channel = wrapper.get(Types1_6_4.STRING, 0);
                    final byte[] payload = wrapper.get(Type.REMAINING_BYTES, 0);

                    if (channel.equals("MC|BEdit") || channel.equals("MC|BSign")) {
                        final ByteBuf payloadBuffer = Unpooled.wrappedBuffer(payload);
                        final Item item = Type.ITEM.read(payloadBuffer);
                        Types1_7_6_10.COMPRESSED_NBT_ITEM.write(payloadBuffer, getItemRewriter().handleItemToServer(item));
                        wrapper.set(Type.REMAINING_BYTES, 0, payloadBuffer.array());
                    }
                });
            }
        });

        this.registerServerbound(ServerboundPackets1_7_0_1_pre.UPDATE_SIGN, new PacketRemapper() {

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
                    packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM_ARRAY, items);  // Items
                });
            }
        });

        // Disconnect
        this.registerClientbound(State.LOGIN, 0xFF, 0x00, new DisconnectPacketRemapper());
        this.registerClientbound(ClientboundPackets1_6_4.DISCONNECT, new DisconnectPacketRemapper());

        // Pong
        this.cancelClientbound(State.STATUS, 0x01);

        // Disconnect Status
        this.registerClientbound(State.STATUS, 0xFF, 0x00, new PacketRemapper() {

            @Override
            public void registerMap() {
                handler(wrapper -> {
                    String reason = wrapper.read(Types1_6_4.STRING);
                    String[] split = reason.split("\0");
                    if (!split[0].equals("§1") || split.length != 6) {
                        wrapper.user().disconnect("Invalid response!");
                        wrapper.cancel();
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
                        wrapper.clearPacket();
                        wrapper.write(Type.STRING, json);
                    } catch (Throwable t) {
                        wrapper.cancel();
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.ENTITY_ANIMATION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                map(Type.BYTE, Type.UNSIGNED_BYTE); // Animation-Id
                handler(wrapper -> {
                    short animId = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (animId == 0) {
                        wrapper.cancel();
                        return;
                    } else if (animId >= 1 && animId <= 3)
                        animId--;
                    else if (animId == 5)
                        animId = 3;
                    else if (animId == 6 || animId == 7)
                        animId -= 2;

                    wrapper.set(Type.UNSIGNED_BYTE, 0, animId);
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
                handler(wrapper -> {
                    final int entityId = wrapper.read(Type.INT);
                    final int count = wrapper.read(Type.INT);

                    final List<EntityProperty> list = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        final EntityProperty prop = new EntityProperty();
                        prop.key = wrapper.read(Types1_6_4.STRING);
                        prop.value = wrapper.read(Type.DOUBLE); // Value

                        short modifierCount = wrapper.passthrough(Type.SHORT); // Modifier count

                        final List<EntityAttributeModifier> modifiers = new ArrayList<>(modifierCount);

                        for (int k = 0; k < modifierCount; k++) {
                            final EntityAttributeModifier mod = new EntityAttributeModifier();
                            mod.uuid = wrapper.read(Type.UUID);
                            mod.amount = wrapper.read(Type.DOUBLE);
                            mod.operation = wrapper.read(Type.BYTE);
                            modifiers.add(mod);
                        }
                        prop.modifiers = modifiers;
                        list.add(prop);
                    }
                    wrapper.clearPacket();

                    wrapper.write(Type.INT, entityId);
                    wrapper.write(Type.INT, list.size());

                    for (EntityProperty prop : list) {
                        wrapper.write(Type.STRING, prop.key);
                        wrapper.write(Type.DOUBLE, prop.value);
                        wrapper.write(Type.SHORT, (short) prop.modifiers.size());

                        for (int k = 0; k < prop.modifiers.size(); k++) {
                            final EntityAttributeModifier mod = prop.modifiers.get(k);

                            wrapper.write(Type.UUID, mod.uuid);
                            wrapper.write(Type.DOUBLE, mod.amount);
                            wrapper.write(Type.BYTE, mod.operation);
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.CHAT_MESSAGE, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Types1_6_4.STRING, Type.STRING);
                handler(wrapper -> {
                    String message = wrapper.get(Type.STRING, 0);
                    try {
                        BaseComponent[] components = ComponentSerializer.parse(message.replace("\"using\":", "\"with\":"));
                        wrapper.set(Type.STRING, 0, ComponentSerializer.toString(components[0]));
                    } catch (Throwable t) {
                        wrapper.set(Type.STRING, 0, ComponentSerializer.toString(new TextComponent(message)));
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
                handler(wrapper -> wrapper.read(Type.SHORT)); // WorldHeight
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
                handler(wrapper -> {
                    short id = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (id == 1)
                        id = 2;
                    else if (id == 2)
                        id = 1;
                    wrapper.set(Type.UNSIGNED_BYTE, 0, id);
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.PLAYER_POSITION, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.DOUBLE); // X-Position
                map(Type.DOUBLE); // Y-Position
                handler(wrapper -> wrapper.read(Type.DOUBLE)); // Y/Stance
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
                handler(PacketWrapper::cancel);
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
                handler(wrapper -> {
                    String text = wrapper.read(Types1_6_4.STRING);
                    wrapper.write(Type.STRING_ARRAY, text.split("\u0000"));
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
                handler(wrapper -> {
                    String name = wrapper.read(Types1_6_4.STRING);
                    byte mode = wrapper.read(Type.BYTE);
                    String displayName = null;
                    String prefix = null;
                    String suffix = null;
                    byte friendlyFire = 0;
                    String[] players = null;
                    if (mode == 0 || mode == 2) {
                        displayName = wrapper.read(Types1_6_4.STRING);
                        prefix = wrapper.read(Types1_6_4.STRING);
                        suffix = wrapper.read(Types1_6_4.STRING);
                        friendlyFire = wrapper.read(Type.BYTE);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        players = new String[wrapper.read(Type.SHORT)];

                        for (int i = 0; i < players.length; i++) {
                            players[i] = wrapper.read(Types1_6_4.STRING);
                        }
                    }
                    wrapper.clearPacket();

                    wrapper.write(Type.STRING, name);
                    wrapper.write(Type.BYTE, mode);
                    if (mode == 0 || mode == 2) {
                        wrapper.write(Type.STRING, displayName);
                        wrapper.write(Type.STRING, prefix);
                        wrapper.write(Type.STRING, suffix);
                        wrapper.write(Type.BYTE, friendlyFire);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        wrapper.write(Type.SHORT, (short) players.length);

                        for (String player : players) {
                            wrapper.write(Type.STRING, player);
                        }
                    }
                    wrapper.cancel();
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_6_4.SPAWN_PLAYER, new PacketRemapper() {

            @Override
            public void registerMap() {
                map(Type.INT, Type.VAR_INT); // Entity-Id
                handler(wrapper -> {
                    String name = wrapper.read(Types1_6_4.STRING);
                    wrapper.write(Type.STRING, UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes()).toString().replace("-", ""));
                    wrapper.write(Type.STRING, name);
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
                handler(wrapper -> {
                    int throwerEntityId = wrapper.passthrough(Type.INT);
                    if (throwerEntityId > 0) {
                        wrapper.passthrough(Type.SHORT);
                        wrapper.passthrough(Type.SHORT);
                        wrapper.passthrough(Type.SHORT);
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
                handler(wrapper -> {
                    int direction = wrapper.get(Type.INT, 3);
                    int modX = 0;
                    int modZ = 0;
                    switch (direction) {
                        case 0 -> modZ = 1;
                        case 1 -> modX = -1;
                        case 2 -> modZ = -1;
                        case 3 -> modX = 1;
                    }
                    wrapper.set(Type.INT, 0, wrapper.get(Type.INT, 0) + modX);
                    wrapper.set(Type.INT, 2, wrapper.get(Type.INT, 2) + modZ);
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
                handler(wrapper -> {
                    final int instrument = NoteBlockPitch.limitInstrument(wrapper.get(Type.UNSIGNED_BYTE, 0));

                    final PacketWrapper sound = PacketWrapper.create(ClientboundPackets1_6_4.ENTITY_EFFECT, wrapper.user());
                    sound.write(Type.STRING, NoteBlockPitch.getInstrument(instrument));
                    sound.write(Type.INT, wrapper.get(Type.INT, 0) * 8);
                    sound.write(Type.INT, wrapper.get(Type.SHORT, 0) * 8);
                    sound.write(Type.INT, wrapper.get(Type.INT, 1) * 8);
                    sound.write(Type.FLOAT, 3.0F);
                    sound.write(Type.UNSIGNED_BYTE, NoteBlockPitch.getPitch(wrapper.get(Type.UNSIGNED_BYTE, 1)));

                    sound.send(Protocol1_7_0_1_preto1_6_4.class);
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
                handler(wrapper -> {
                    int count = wrapper.get(Type.INT, 0);

                    for (int i = 0; i < count; i++) {
                        wrapper.passthrough(Type.BYTE); // X-Offset
                        wrapper.passthrough(Type.BYTE); // Y-Offset
                        wrapper.passthrough(Type.BYTE); // Z-Offset
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
                read(Type.SHORT); // Type
                map(Type.SHORT, Type.VAR_INT); // ID
                map(Type.SHORT_BYTE_ARRAY); // Data

                handler(PacketWrapper::clearPacket);
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
    public SoundRewriter<Protocol1_7_0_1_preto1_6_4> soundRewriter() {
        return this.soundRewriter;
    }

    @Override
    public MaterialReplacement materialReplacement() {
        return this.materialReplacement;
    }

    @Override
    public void register(ViaProviders providers) {
        super.register(providers);

        providers.register(UUIDProvider.class, new UUIDProvider());
        providers.register(EncryptionProvider.class, new EncryptionProvider());
    }

    @Override
    public void init(UserConnection userConnection) {
        if (!userConnection.has(ClientWorld.class)) {
            userConnection.put(new ClientWorld(userConnection));
        }
        userConnection.put(new PluginMessageStorage(userConnection));
        userConnection.put(new SplitterTracker(userConnection, ClientboundPackets1_6_4.values(), ClientboundLoginPackets1_6_4.values()));
    }
}
