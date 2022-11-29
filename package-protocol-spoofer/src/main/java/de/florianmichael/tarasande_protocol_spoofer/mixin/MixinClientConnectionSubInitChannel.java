package de.florianmichael.tarasande_protocol_spoofer.mixin;

import de.florianmichael.tarasande_protocol_spoofer.multiplayerfeature.MultiplayerFeatureToggleableExploitsHAProxyHack;
import io.netty.channel.Channel;
import io.netty.handler.codec.haproxy.HAProxyMessageEncoder;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.network.ClientConnection$1")
public class MixinClientConnectionSubInitChannel {

    @Inject(method = "initChannel", at = @At("TAIL"))
    public void addChannelHandlers(Channel channel, CallbackInfo ci) {
        final MultiplayerFeatureToggleableExploitsHAProxyHack haProxyHack = TarasandeMain.Companion.managerMultiplayerFeature().get(MultiplayerFeatureToggleableExploitsHAProxyHack.class);

        if (haProxyHack.getState().getValue()) {
            channel.pipeline().addFirst("haproxy-encoder", HAProxyMessageEncoder.INSTANCE);
            channel.pipeline().addLast(haProxyHack.createHandler());
        }
    }
}