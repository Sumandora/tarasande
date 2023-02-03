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
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol;
import de.florianmichael.viabeta.api.BetaProtocols;
import de.florianmichael.viabeta.baseprotocol.BaseProtocol1_3;
import de.florianmichael.viabeta.baseprotocol.BaseProtocol1_5;
import de.florianmichael.viabeta.baseprotocol.BaseProtocol1_6;
import de.florianmichael.viabeta.baseprotocol.BaseProtocolb1_7;
import de.florianmichael.viabeta.pre_netty.PreNettyConstants;
import de.florianmichael.viabeta.pre_netty.handler.PreNettyPacketDecoder;
import de.florianmichael.viabeta.pre_netty.handler.PreNettyPacketEncoder;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.CustomViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.CustomViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnection_1 {

    // synthetic field
    @Final
    @Shadow
    ClientConnection field_11663;

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void hackNettyPipeline(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            // Creating the user connection
            final UserConnection user = new UserConnectionImpl(channel, true);
            ((IClientConnection_Protocol) field_11663).protocolhack_setViaConnection(user);
            // Creating the pipeline
            new ProtocolPipelineImpl(user);

            // ViaLoadingBase Packet handling
            channel.pipeline().addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new CustomViaEncodeHandler(user));
            channel.pipeline().addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new CustomViaDecodeHandler(user));

            // ViaBeta
            if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.r1_6_4)) {
                // Handshake forwarding in <= 1.6.4
                user.getProtocolInfo().getPipeline().add(BaseProtocol1_6.INSTANCE);
                if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.r1_5_2)) {
                    // Status/ping handling in <= 1.5.2
                    user.getProtocolInfo().getPipeline().add(BaseProtocol1_5.INSTANCE);
                    if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.r1_3_1tor1_3_2)) {
                        // Status/ping handling in <= 1.3.2
                        user.getProtocolInfo().getPipeline().add(BaseProtocol1_3.INSTANCE);
                        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.b1_7tob1_7_3)) {
                            // Status/ping handling in <= b1.7.3
                            user.getProtocolInfo().getPipeline().add(BaseProtocolb1_7.INSTANCE);
                        }
                    }
                }

                // Pre Netty Packet handling in <= 1.6.4
                channel.pipeline().addBefore("prepender", PreNettyConstants.ENCODER, new PreNettyPacketEncoder(user));
                channel.pipeline().addBefore("splitter", PreNettyConstants.DECODER, new PreNettyPacketDecoder(user));
            }
        }
    }
}
