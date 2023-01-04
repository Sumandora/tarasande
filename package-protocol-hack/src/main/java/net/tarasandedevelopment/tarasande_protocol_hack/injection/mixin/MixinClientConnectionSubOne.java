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
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.netty.CustomViaDecodeHandler;
import de.florianmichael.vialoadingbase.netty.CustomViaEncodeHandler;
import de.florianmichael.vialoadingbase.netty.NettyConstants;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import net.minecraft.network.ClientConnection;
import net.raphimc.vialegacy.netty.PreNettyDecoder;
import net.raphimc.vialegacy.netty.PreNettyEncoder;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.baseprotocols.PreNettyBaseProtocol;
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

            channel.pipeline()
                    .addBefore("encoder", NettyConstants.HANDLER_ENCODER_NAME, new CustomViaEncodeHandler(user))
                    .addBefore("decoder", NettyConstants.HANDLER_DECODER_NAME, new CustomViaDecodeHandler(user));

            if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_6_4)) {
                user.getProtocolInfo().getPipeline().add(PreNettyBaseProtocol.INSTANCE);
                channel.pipeline().addBefore("prepender", "via-pre_netty-encoder", new PreNettyEncoder(user));
                channel.pipeline().addBefore("splitter", "via-pre_netty-decoder", new PreNettyDecoder(user));
            }
        }
    }
}
