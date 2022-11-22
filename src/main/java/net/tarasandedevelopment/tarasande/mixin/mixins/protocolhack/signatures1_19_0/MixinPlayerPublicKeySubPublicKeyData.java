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

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.signatures1_19_0;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IPublicKeyData_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Instant;
import java.util.UUID;

@Mixin(PlayerPublicKey.PublicKeyData.class)
public class MixinPlayerPublicKeySubPublicKeyData implements IPublicKeyData_Protocol {

    @Shadow
    @Final
    PublicKey key;
    @Shadow
    @Final
    private Instant expiresAt;
    @Unique
    private byte[] protocolhack_1_19_0Key;

    @Redirect(method = {"write", "verifyKey"}, at = @At(value = "FIELD", target = "Lnet/minecraft/network/encryption/PlayerPublicKey$PublicKeyData;keySignature:[B"))
    public byte[] replaceKeys(PlayerPublicKey.PublicKeyData instance) {
        if (this.protocolhack_1_19_0Key != null && VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            return this.protocolhack_1_19_0Key;
        }

        return instance.keySignature();
    }

    @Inject(method = "toSerializedString", at = @At(value = "HEAD"), cancellable = true)
    public void injectToSerializedString(UUID playerUuid, CallbackInfoReturnable<byte[]> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            cir.setReturnValue((this.expiresAt.toEpochMilli() + NetworkEncryptionUtils.encodeRsaPublicKey(this.key)).getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    public void protocolhack_set1_19_0Key(ByteBuffer byteBuffer) {
        this.protocolhack_1_19_0Key = byteBuffer.array();
    }
}
