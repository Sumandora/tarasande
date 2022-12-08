package de.florianmichael.clampclient.injection.mixin.protocolhack.viaversion;

import com.google.common.primitives.Longs;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.ProfileKey;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ClientboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_1to1_19.ServerboundPackets1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ClientboundPackets1_19_3;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.Protocol1_19_3To1_19_1;
import com.viaversion.viaversion.protocols.protocol1_19_3to1_19_1.ServerboundPackets1_19_3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.Signer;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.MessageSigner1_19_2;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.ProfileKeyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(value = Protocol1_19_3To1_19_1.class, remap = false)
public class MixinProtocol1_19_3To1_19_1 extends AbstractProtocol<ClientboundPackets1_19_1, ClientboundPackets1_19_3, ServerboundPackets1_19_1, ServerboundPackets1_19_3> {

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
                    final ProfileKeyStorage profileKeyStorage = wrapper.user().get(ProfileKeyStorage.class);
                    if (profileKeyStorage != null) {
                        profileKeyStorage.setOriginalNonce(originalNonce);
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
                    if (playerPublicKey == null) {
                        wrapper.write(Type.OPTIONAL_PROFILE_KEY, null);
                    } else {
                        final PlayerPublicKey.PublicKeyData publicKeyData = playerPublicKey.publicKey().data();
                        wrapper.write(Type.OPTIONAL_PROFILE_KEY, new ProfileKey(publicKeyData.expiresAt().toEpochMilli(), publicKeyData.key().getEncoded(), publicKeyData.keySignature()));
                        final ProfileKeyStorage profileKeyStorage = wrapper.user().get(ProfileKeyStorage.class);
                        if (profileKeyStorage != null) {
                            profileKeyStorage.setPrivateKey(playerPublicKey.privateKey());
                            profileKeyStorage.setSigner(MessageSigner1_19_2.create(playerPublicKey.privateKey(), "SHA256withRSA"));
                        }
                    }
                    wrapper.write(Type.OPTIONAL_UUID, uuid);
                });
            }
        });

        this.registerServerbound(State.LOGIN, ServerboundLoginPackets.ENCRYPTION_KEY.getId(), ServerboundLoginPackets.ENCRYPTION_KEY.getId(), new PacketRemapper() {
            @Override
            public void registerMap() {
                map(Type.BYTE_ARRAY_PRIMITIVE); // Keys
                handler(wrapper -> {
                    final ProfileKeyStorage profileKeyStorage = wrapper.user().get(ProfileKeyStorage.class);
                    if (profileKeyStorage != null) {
                        wrapper.read(Type.BYTE_ARRAY_PRIMITIVE); // Packet-Nonce
                        final byte[] nonce = profileKeyStorage.getOriginalNonce();

                        wrapper.write(Type.BOOLEAN, profileKeyStorage.getSigner() == null);

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
                });
            }
        });
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void addProfileKeyStorage(UserConnection user, CallbackInfo ci) {
        user.put(new ProfileKeyStorage(user));
    }
}
