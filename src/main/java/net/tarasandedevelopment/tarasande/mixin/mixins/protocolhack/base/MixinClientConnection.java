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

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.viaprotocolhack.event.PipelineReorderEvent;
import de.florianmichael.viaprotocolhack.util.VersionList;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol;
import net.tarasandedevelopment.tarasande.protocolhack.fix.WolfHealthTracker1_14_4;
import net.tarasandedevelopment.tarasande.protocolhack.provider.vialegacy.FabricPreNettyProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements IClientConnection_Protocol {

    @Shadow
    public Channel channel;

    @Shadow
    private boolean encrypted;
    @Unique
    private UserConnection protocolhack_viaConnection;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "disconnect", at = @At("RETURN"))
    public void onDisconnect(Text disconnectReason, CallbackInfo ci) {
        WolfHealthTracker1_14_4.INSTANCE.clear();
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    public void injectSetupEncryption(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
            FabricPreNettyProvider.Companion.setDecryptionKey(decryptionCipher);
            FabricPreNettyProvider.Companion.setEncryptionKey(encryptionCipher);

            this.encrypted = true;
            ci.cancel();
        }
    }

    @Override
    public void protocolhack_setViaConnection(UserConnection userConnection) {
        this.protocolhack_viaConnection = userConnection;
    }

    @Override
    public UserConnection protocolhack_getViaConnection() {
        return this.protocolhack_viaConnection;
    }
}
