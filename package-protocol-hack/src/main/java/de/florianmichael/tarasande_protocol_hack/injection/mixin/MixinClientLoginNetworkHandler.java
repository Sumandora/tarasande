package de.florianmichael.tarasande_protocol_hack.injection.mixin;

import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol;
import de.florianmichael.viabeta.api.BetaProtocols;
import de.florianmichael.viabeta.protocol.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Shadow @Final private ClientConnection connection;

    @Inject(method = "joinServerSession", at = @At("HEAD"), cancellable = true)
    public void dontVerifySessionIfCracked(String serverId, CallbackInfoReturnable<Text> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(BetaProtocols.r1_6_4)) {
            if (!((IClientConnection_Protocol) connection).protocolhack_getViaConnection().get(ProtocolMetadataStorage.class).isAuthenticated()) {
                cir.setReturnValue(null);
            }
        }
    }
}
