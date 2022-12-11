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

import com.mojang.brigadier.ParseResults;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.Protocol1_19_1To1_19;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.storage.ReceivedMessagesStorage;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ClientboundPackets1_19;
import com.viaversion.viaversion.protocols.protocol1_19to1_18_2.ServerboundPackets1_19;
import de.florianmichael.clampclient.injection.mixininterface.IPublicKeyData_Protocol;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.SignedArgumentList;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.MessageChain1_19_2;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.signer.MessageSigner1_19_0;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.storage.ProfileKeyStorage1_19_2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;
import java.util.UUID;

@Mixin(Protocol1_19_1To1_19.class)
public class MixinProtocol1_19_1To1_19 extends AbstractProtocol<ClientboundPackets1_19, ClientboundPackets1_19_1, ServerboundPackets1_19, ServerboundPackets1_19_1> {

    @Inject(method = "registerPackets", at = @At("RETURN"), remap = false)
    public void injectRegisterPackets(CallbackInfo ci) {
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO.getId(), ServerboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.OPTIONAL_PROFILE_KEY);
                handler(wrapper -> {
                    final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);
                    if (profileKeyStorage != null) {
                        final PlayerPublicKey.PublicKeyData publicKeyData = profileKeyStorage.getPublicKeyData();
                        if (publicKeyData != null) {
                            final ProfileKey profileKey = wrapper.get(Type.OPTIONAL_PROFILE_KEY, 0);
                            if (profileKey != null) {
                                final byte[] legacyKey = ((IPublicKeyData_Protocol) (Object) publicKeyData).protocolhack_get1_19_0Key().array();
                                wrapper.set(Type.OPTIONAL_PROFILE_KEY, 0, new ProfileKey(profileKey.expiresAt(), profileKey.publicKey(), legacyKey));
                            }
                        }
                    }
                });
                this.read(Type.OPTIONAL_UUID);
            }
        }, true);
        this.registerClientbound(State.LOGIN, ClientboundLoginPackets.HELLO.getId(), ClientboundLoginPackets.HELLO.getId(), new PacketRemapper() {
            public void registerMap() {
            }
        }, true);
        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            public void registerMap() {
            }
        }, true);
        this.registerServerbound(ServerboundPackets1_19_1.CHAT_MESSAGE, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Message
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                read(Type.BYTE_ARRAY_PRIMITIVE); // Signature
                handler(wrapper -> {
                    final UUID sender = wrapper.user().getProtocolInfo().getUuid();
                    final String message = wrapper.get(Type.STRING, 0);
                    final long timestamp = wrapper.get(Type.LONG, 0);
                    final long salt = wrapper.get(Type.LONG, 1);

                    final ProfileKeyStorage1_19_2 profileKeyStorage = wrapper.user().get(ProfileKeyStorage1_19_2.class);

                    if (profileKeyStorage != null && profileKeyStorage.getSigner() != null && sender != null) {
                        final byte[] signature = MessageSigner1_19_0.INSTANCE.sign(
                                profileKeyStorage.getSigner(),
                                message,
                                sender,
                                Instant.ofEpochMilli(timestamp),
                                salt
                        );

                        wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                    }
                });
                map(Type.BOOLEAN); // Signed preview
                read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen messages
                read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE); // Last received message
            }
        });
        this.registerServerbound(ServerboundPackets1_19_1.CHAT_COMMAND, new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.STRING); // Command
                map(Type.LONG); // Timestamp
                map(Type.LONG); // Salt
                map(Type.VAR_INT); // Signatures

                // Removing new signatures
                handler(wrapper -> {
                    final int signatures = wrapper.get(Type.VAR_INT, 0);
                    for (int i = 0; i < signatures; i++) {
                        wrapper.read(Type.STRING); // Argument name
                        wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); // Signature
                    }
                });

                // Emulating old signatures
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
                                final byte[] signature = MessageSigner1_19_0.INSTANCE.sign(
                                        profileKeyStorage.getSigner(),
                                        command,
                                        sender,
                                        Instant.ofEpochMilli(timestamp),
                                        salt
                                );

                                wrapper.write(Type.STRING, argument.getNodeName());
                                wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, signature);
                            }
                        }
                    }
                });
                map(Type.BOOLEAN); // Signed preview
                read(Type.PLAYER_MESSAGE_SIGNATURE_ARRAY); // Last seen messages
                read(Type.OPTIONAL_PLAYER_MESSAGE_SIGNATURE); // Last received message
            }
        });
    }
}
