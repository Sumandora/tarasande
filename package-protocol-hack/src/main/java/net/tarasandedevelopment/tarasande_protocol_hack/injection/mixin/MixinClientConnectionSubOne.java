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

package net.tarasandedevelopment.tarasande_protocol_hack.injection.mixin;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol;
import de.florianmichael.vialegacy.pre_netty.DummyPrepender;
import de.florianmichael.vialegacy.pre_netty.PreNettyConstants;
import de.florianmichael.vialegacy.pre_netty.PreNettyPacketDecoder;
import de.florianmichael.vialegacy.pre_netty.PreNettyPacketEncoder;
import de.florianmichael.vialegacy.protocol.LegacyProtocolVersion;
import de.florianmichael.vialegacy.protocols.base.BaseProtocol1_6;
import de.florianmichael.viaprotocolhack.netty.CustomViaDecodeHandler;
import de.florianmichael.viaprotocolhack.netty.CustomViaEncodeHandler;
import de.florianmichael.viaprotocolhack.netty.NettyConstants;
import de.florianmichael.viaprotocolhack.util.VersionList;
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
public class MixinClientConnectionSubOne {

    // synthetic field
    @Final
    @Shadow
    ClientConnection field_11663;

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void addViaVersionHandler(Channel channel, CallbackInfo ci) {
        if (channel instanceof SocketChannel) {
            UserConnection user = new UserConnectionImpl(channel, true);
            ((IClientConnection_Protocol) field_11663).protocolhack_setViaConnection(user);
            new ProtocolPipelineImpl(user);

            if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                user.getProtocolInfo().getPipeline().add(BaseProtocol1_6.INSTANCE);
            }

            channel.pipeline()
                    .addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new CustomViaEncodeHandler(user))
                    .addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new CustomViaDecodeHandler(user));

            if (VersionList.isOlderOrEqualTo(LegacyProtocolVersion.r1_6_4)) {
                channel.pipeline()
                        .addBefore(NettyConstants.HANDLER_ENCODER_NAME, PreNettyConstants.ENCODER, new PreNettyPacketEncoder())
                        .addBefore(NettyConstants.HANDLER_DECODER_NAME, PreNettyConstants.DECODER, new PreNettyPacketDecoder(user));

                channel.pipeline().replace("prepender", "prepender", new DummyPrepender());
                channel.pipeline().remove("splitter");
            }
        }
    }
}
