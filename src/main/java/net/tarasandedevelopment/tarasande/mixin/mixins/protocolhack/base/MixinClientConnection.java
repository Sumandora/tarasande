package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.base;

import com.viaversion.viaversion.api.connection.UserConnection;
import de.florianmichael.viaprotocolhack.ViaProtocolHack;
import de.florianmichael.viaprotocolhack.event.PipelineReorderEvent;
import io.netty.channel.Channel;
import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IClientConnection_Protocol;
import net.tarasandedevelopment.tarasande.protocol.service.ProtocolAutoDetector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Mixin(ClientConnection.class)
public class MixinClientConnection implements IClientConnection_Protocol {

    @Shadow private Channel channel;

    @Unique
    private UserConnection viaConnection;

    @Inject(method = "setCompressionThreshold", at = @At("RETURN"))
    private void reorderCompression(int compressionThreshold, boolean rejectBad, CallbackInfo ci) {
        channel.pipeline().fireUserEventTriggered(new PipelineReorderEvent());
    }

    @Inject(method = "connect", at = @At("HEAD"))
    private static void onConnect(InetSocketAddress address, boolean useEpoll, CallbackInfoReturnable<ClientConnection> cir) {
        if (TarasandeMain.Companion.get().getProtocolHack().isAuto())
            try {
                ProtocolAutoDetector.INSTANCE.detectVersion(address).get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                ViaProtocolHack.instance().logger().log(Level.WARNING, "Could not auto-detect protocol for " + address + " " + e);
            }
    }

    @Override
    public void tarasande_setViaConnection(UserConnection userConnection) {
        this.viaConnection = userConnection;
    }

    @Override
    public UserConnection tarasande_getViaConnection() {
        return this.viaConnection;
    }
}
