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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {

    @Shadow
    @Final
    protected MinecraftClient client;

//    @Inject(method = "signChatMessage", at = @At("HEAD"), cancellable = true)
//    public void injectSignChatMessage(MessageMetadata metadata, DecoratedContents content, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<MessageSignatureData> cir) {
//        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
//            try {
//                final Signer signer = this.client.getProfileKeys().getSigner();
//
//                if (signer != null)
//                    cir.setReturnValue(MessageSigner1_19_0.INSTANCE.sign(signer, content.decorated(), metadata.sender(), metadata.timestamp(), metadata.salt()));
//            } catch (Exception ignored) {
//            }
//            cir.cancel();
//        }
//    }
//
//    @Inject(method = "signArguments", at = @At(value = "HEAD"))
//    public void injectSignArguments(MessageMetadata signer, ParseResults<CommandSource> parseResults, @Nullable Text preview, LastSeenMessageList lastSeenMessages, CallbackInfoReturnable<ArgumentSignatureDataMap> cir) {
//        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_19)) {
//            MessageSigner1_19_0.INSTANCE.track(signer);
//        }
//    }
}
