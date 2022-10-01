package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreenSubRun {

    @Final
    @Shadow
    ServerAddress field_33737;

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getHostName()Ljava/lang/String;"))
    public String redirectRun(InetSocketAddress instance) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_17))
            return field_33737.getAddress();

        return instance.getHostString();
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Ljava/net/InetSocketAddress;getPort()I"))
    public int redirectRun2(InetSocketAddress instance) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_17))
            return field_33737.getPort();

        return instance.getPort();
    }
}
