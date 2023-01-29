/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 * <p>
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 * <p>
 * Changelog:
 * v1.0:
 * Added License
 * v1.1:
 * Ownership withdrawn
 * v1.2:
 * Version-independent validity and automatic renewal
 */

package de.florianmichael.tarasande_protocol_hack.injection.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol;
import de.florianmichael.tarasande_protocol_hack.TarasandeProtocolHack;
import de.florianmichael.viabedrock.api.BedrockProtocols;
import de.florianmichael.viabedrock.protocol.Protocol1_19_3toBedrock1_19_51;
import de.florianmichael.viabeta.api.BetaProtocols;
import de.florianmichael.viabeta.pre_netty.PreNettyConstants;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.event.PipelineReorderEvent;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.local.LocalChannel;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PacketDecryptor;
import net.minecraft.network.encryption.PacketEncryptor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.crypto.Cipher;
import java.net.InetAddress;
import java.net.InetSocketAddress;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements IClientConnection_Protocol {

    @Shadow
    private Channel channel;

    @Shadow private boolean encrypted;
    @Unique
    private UserConnection protocolhack_viaConnection;

    @Unique
    private Cipher vialegacy_decryptionCipher;

    @Unique
    private Cipher vialegacy_encryptionCipher;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "setupEncryption", at = @At("HEAD"), cancellable = true)
    private void handlePreNettyCrypto(Cipher decryptionCipher, Cipher encryptionCipher, CallbackInfo ci) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.r1_6_4)) {
            ci.cancel();
            this.vialegacy_decryptionCipher = decryptionCipher;
            this.vialegacy_encryptionCipher = encryptionCipher;
        }
    }

    @Redirect(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;connect(Ljava/net/InetAddress;I)Lio/netty/channel/ChannelFuture;", remap = false))
    private static ChannelFuture startLocalServer(Bootstrap instance, InetAddress inetHost, int inetPort) {
        if (ViaLoadingBase.getTargetVersion().getVersion() == BedrockProtocols.VIA_PROTOCOL_VERSION.getVersion()) {
            TarasandeProtocolHack.Companion.setConnectedAddress(new InetSocketAddress(inetHost, inetPort));
            return instance.connect(Protocol1_19_3toBedrock1_19_51.createLimboServer(TarasandeProtocolHack.Companion.getConnectedAddress()));
        }
        return instance.connect(inetHost, inetPort);
    }

    @Redirect(method = "connect", at = @At(value = "INVOKE", target = "Lio/netty/bootstrap/Bootstrap;channel(Ljava/lang/Class;)Lio/netty/bootstrap/AbstractBootstrap;", remap = false))
    private static AbstractBootstrap useLocalChannel(Bootstrap instance, Class aClass) {
        if (ViaLoadingBase.getTargetVersion().getVersion() == BedrockProtocols.VIA_PROTOCOL_VERSION.getVersion()) {
            return instance.channel(LocalChannel.class);
        }
        return instance.channel(aClass);
    }

    @Unique
    public void viabeta_setupPreNettyEncryption() {
        this.encrypted = true;
        this.channel.pipeline().addBefore(PreNettyConstants.DECODER, "decrypt", new PacketDecryptor(this.vialegacy_decryptionCipher));
        this.channel.pipeline().addBefore(PreNettyConstants.ENCODER, "encrypt", new PacketEncryptor(this.vialegacy_encryptionCipher));
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
