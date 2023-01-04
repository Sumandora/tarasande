package net.tarasandedevelopment.tarasande_protocol_hack.injection.mixin;

import de.florianmichael.clampclient.injection.mixininterface.IClientConnection_Protocol;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.vialoadingbase.util.VersionListEnum;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import net.raphimc.vialegacy.protocols.release.protocol1_7_2_5to1_6_4.storage.ProtocolMetadataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLoginNetworkHandler.class)
public class MixinClientLoginNetworkHandler {

    @Shadow @Final private ClientConnection connection;

    @Inject(method = "joinServerSession", at = @At("HEAD"), cancellable = true)
    public void dontVerifySessionIfCracked(String serverId, CallbackInfoReturnable<Text> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(VersionListEnum.r1_6_4)) {
            if (!((IClientConnection_Protocol) connection).protocolhack_getViaConnection().get(ProtocolMetadataStorage.class).authenticate) {
                cir.setReturnValue(null);
            }
        }
    }
}
