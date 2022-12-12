/*
 * Copyright (c) FlorianMichael as EnZaXD 2022
 * Created on 6/24/22, 8:12 PM
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

package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.google.common.primitives.Longs;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedArgument;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.PlayerMessageSignature;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.BitSetType;
import com.viaversion.viaversion.api.type.types.ByteArrayType;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.kyori.adventure.text.Component;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.command.argument.SignedArgumentType;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.MessageChain1_19_2;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.storage.PacketNonceStorage1_19_2;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.storage.ProfileKeyStorage1_19_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(value = Protocol1_19_3To1_19_1.class, remap = false)
public class MixinProtocol1_19_3To1_19_1 extends AbstractProtocol<ClientboundPackets1_19_1, ClientboundPackets1_19_3, ServerboundPackets1_19_1, ServerboundPackets1_19_3> {

    @Unique
    private static final ByteArrayType.OptionalByteArrayType OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType.OptionalByteArrayType(256);

    @Unique
    private static final ByteArrayType MESSAGE_SIGNATURE_BYTES_TYPE = new ByteArrayType(256);

    @Unique
    private static final BitSetType ACKNOWLEDGED_BIT_SET_TYPE = new BitSetType(20);

    @Unique
    private static final UUID ZERO_UUID = new UUID(0, 0);

    @Inject(method = "registerPackets", at = @At("RETURN"))
    public void fixKeys(CallbackInfo ci) {
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Server-ID
                map(Type.BYTE_ARRAY_PRIMITIVE); // Public Key
                map(Type.BYTE_ARRAY_PRIMITIVE); // Nonce

                handler(wrapper -> {
                    final byte[] originalNonce = wrapper.get(Type.BYTE_ARRAY_PRIMITIVE, 1);
                    final PacketNonceStorage1_19_2 packetNonceStorage = wrapper.user().get(PacketNonceStorage1_19_2.class);
                    if (packetNonceStorage != null) {
                        packetNonceStorage.setNonce(originalNonce);
                    }
                });
            }
        });
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                handler(wrapper -> {
                    wrapper.passthrough(Type.STRING); // Name
                    final UUID uuid = wrapper.read(Type.OPTIONAL_UUID);

                    final PlayerKeyPair playerPublicKey = MinecraftClient.getInstance().getProfileKeys().fetchKeyPair().get().orElse(null);
                    if (playerPublicKey != null) {
                        // Online Mode
                        final PlayerPublicKey.PublicKeyData publicKeyData = playerPublicKey.publicKey().data();
                        wrapper.write(Type.OPTIONAL_PROFILE_KEY, new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature()));

                        final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);
                        if (profileKeyStorage != null) {
                            profileKeyStorage.setupConnection(publicKeyData, playerPublicKey.privateKey());
                        }
                    } else {
                        // Cracked mode
                        wrapper.write(Type.OPTIONAL_PROFILE_KEY, null);
                    }
                    wrapper.write(Type.OPTIONAL_UUID, uuid);
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE); // Encrypted Private Key
                read(Type.BYTE_ARRAY_PRIMITIVE); // Packet-Nonce

                handler(wrapper -> {
                    final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);
                    if (profileKeyStorage != null) {
                        wrapper.write(Type.BOOLEAN, profileKeyStorage.getSigner() == null);

                        final PacketNonceStorage1_19_2 packetNonceStorage = wrapper.user().get(PacketNonceStorage1_19_2.class);
                        if (packetNonceStorage != null) {
                            final byte[] nonce = packetNonceStorage.getNonce(); // since 1.19.3 encrypts the nonce before writing it, we need to track the original nonce, since 1.19.2 uses the original nonce by the server

                            if (profileKeyStorage.getSigner() != null) {
                                // Online mode
                                final long salt = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
                                final byte[] signedNonce = profileKeyStorage.getSigner().sign(updater -> {
                                    updater.update(nonce);
                                    updater.update(Longs.toByteArray(salt));
                                });
                                wrapper.write(Type.LONG, salt);
                                wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signedNonce);
                            } else {
                                // Cracked mode
                                wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, nonce);
                            }
                        }
                    }
                });
            }
        });

        this.registerClientbound(ClientboundPackets1_19_1.PLAYER_CHAT, ClientboundPackets1_19_3.DISGUISED_CHAT, new PacketRemapper() {
            @Override
            public void registerMap() {
                read(Type.OPTIONAL_BYTE_ARRAY_PRIMITIVE); // Previous signature
                handler(wrapper -> {
                    final PlayerMessageSignature signature = wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE);

                    // Store message signature for last seen
                    if (!signature.uuid().equals(ZERO_UUID) && signature.signatureBytes().length != 0) {
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                        if (messagesStorage != null) {
                            messagesStorage.add(signature);
                            if (messagesStorage.tickUnacknowledged() > 64) {
                                messagesStorage.resetUnacknowledgedCount();

                                // Send chat acknowledgement
                                final PacketWrapper chatAckPacket = wrapper.create(ServerboundPackets1_19_1.CHAT_ACK);
                                chatAckPacket.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                                wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);

                                chatAckPacket.sendToServer(Protocol1_19_3To1_19_1.class);
                            }
                        }
                    }

                    final String plainMessage = wrapper.read(Type.STRING);
                    JsonElement decoratedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);

                    wrapper.read(Type.LONG); // Timestamp
                    wrapper.read(Type.LONG); // Salt
                    wrapper.read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen

                    final JsonElement unsignedMessage = wrapper.read(Type.OPTIONAL_COMPONENT);
                    if (unsignedMessage != null) {
                        decoratedMessage = unsignedMessage;
                    }
                    if (decoratedMessage == null) {
                        decoratedMessage = GsonComponentSerializer.gson().serializeToTree(Component.text(plainMessage));
                    }

                    final int filterMaskType = wrapper.read(Type.VAR_INT);
                    if (filterMaskType == 2) { // Partially filtered
                        wrapper.read(Type.LONG_ARRAY_PRIMITIVE); // Mask
                    }

                    wrapper.write(Type.COMPONENT, decoratedMessage);
                    // Keep chat type at the end
                });
            }
        });

        registerServerbound(ServerboundPackets1_19_3.CHAT_COMMAND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.VAR_INT); // Signatures

                // Removing old signatures
                handler(wrapper -> {
                    final int signatures = wrapper.get(Type.VAR_INT, 0);
                    for (int i = 0; i < signatures; i++) {
                        wrapper.read(Type.STRING); // Argument name
                        wrapper.read(MESSAGE_SIGNATURE_BYTES_TYPE); // Signature
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Signing all arguments
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String command = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ClientPlayNetworkHandler clientPlayNetworkHandler = MinecraftClient.getInstance().getNetworkHandler();
                    if (clientPlayNetworkHandler != null) {
                        final ParseResults<CommandSource> parseResults = clientPlayNetworkHandler.getCommandDispatcher().parse(command, clientPlayNetworkHandler.getCommandSource());

                        final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);
                        final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);

                        if (messagesStorage != null && profileKeyStorage != null && sender != null && profileKeyStorage.getSigner() != null) {
                            for (SignedArgumentList.ParsedArgument<CommandSource> argument : SignedArgumentList.of(parseResults).arguments()) {
                                final byte[] signature = MessageChain1_19_2.INSTANCE.pack(
                                        argument.value(),
                                        Instant.ofEpochMilli(timestamp),
                                        salt,
                                        messagesStorage.lastSignatures(),
                                        sender,
                                        profileKeyStorage.getSigner()
                                );

                                wrapper.write(Type.STRING, argument.getNodeName());
                                wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                            }
                        }
                    }
                    wrapper.write(Type.BOOLEAN, false); // No signed preview
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        });
        registerServerbound(ServerboundPackets1_19_3.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                read(OPTIONAL_MESSAGE_SIGNATURE_BYTES_TYPE); // Signature

                // Emulate old Message chain
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String message = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);

                    if (messagesStorage != null && profileKeyStorage != null && sender != null && profileKeyStorage.getSigner() != null) {
                        final byte[] signature = MessageChain1_19_2.INSTANCE.pack(
                                message,
                                Instant.ofEpochMilli(timestamp),
                                salt,
                                messagesStorage.lastSignatures(),
                                sender,
                                profileKeyStorage.getSigner()
                        );

                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                        wrapper.write(Type.BOOLEAN, false); // Signed Preview - not implemented yet, but i could do it
                    } else {
                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, new byte[0]);
                        wrapper.write(Type.BOOLEAN, false); // Signed Preview
                    }
                });

                // Removing new acknowledgement
                handler(wrapper -> {
                    wrapper.read(Type.VAR_INT); // Offset
                    wrapper.read(ACKNOWLEDGED_BIT_SET_TYPE); // Acknowledged
                });

                // Adding old acknowledgement
                handler(wrapper -> {
                    final ReceivedMessagesStorage messagesStorage = wrapper.user().get(ReceivedMessagesStorage.class);
                    if (messagesStorage != null) {
                        messagesStorage.resetUnacknowledgedCount();
                        wrapper.write(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY, messagesStorage.lastSignatures());
                        wrapper.write(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE, null);
                    }
                });
            }
        });
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void addProfileKeyStorage(UserConnection user, CallbackInfo ci) {
        user.put(new ProfileKeyStorage1_19_2(user));
        user.put(new PacketNonceStorage1_19_2(user));
    }
}
