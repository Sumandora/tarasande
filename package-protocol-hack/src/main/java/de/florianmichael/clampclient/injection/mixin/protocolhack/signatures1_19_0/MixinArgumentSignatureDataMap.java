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

package de.florianmichael.clampclient.injection.mixin.protocolhack.signatures1_19_0;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.command.argument.DecoratableArgumentList;
import net.minecraft.network.encryption.Signer;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.MessageMetadata;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande_protocol_hack.fix.MessageSigner1_19_0;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(ArgumentSignatureDataMap.class)
public class MixinArgumentSignatureDataMap {

    @Inject(method = "sign", at = @At("HEAD"), cancellable = true)
    private static void injectSign(DecoratableArgumentList<?> arguments, ArgumentSignatureDataMap.ArgumentSigner signer, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
            final List<ArgumentSignatureDataMap.Entry> list = ArgumentSignatureDataMap.toNameValuePairs(arguments).stream().map(entry -> {
                final MessageMetadata metadata = MessageSigner1_19_0.INSTANCE.get();
                final MessageSignatureData messageSignatureData = MessageSigner1_19_0.INSTANCE.sign((Signer) signer, Text.of(entry.getFirst()), metadata.sender(), metadata.timestamp(), metadata.salt());

                return new ArgumentSignatureDataMap.Entry(entry.getFirst(), messageSignatureData);
            }).collect(Collectors.toList());

            cir.setReturnValue(new ArgumentSignatureDataMap(list));
        }
    }
}
