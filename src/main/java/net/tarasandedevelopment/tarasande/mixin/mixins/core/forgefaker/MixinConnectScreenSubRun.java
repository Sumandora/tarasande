package net.tarasandedevelopment.tarasande.mixin.mixins.core.forgefaker;

import net.minecraft.network.ClientConnection;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuToggleForgeFaker;
import net.tarasandedevelopment.tarasande.screen.clientmenu.forgefaker.ForgeCreator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.net.InetSocketAddress;

@Mixin(targets = "net.minecraft.client.gui.screen.ConnectScreen$1")
public class MixinConnectScreenSubRun {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;connect(Ljava/net/InetSocketAddress;Z)Lnet/minecraft/network/ClientConnection;"))
    public ClientConnection hookForgeHandler(InetSocketAddress address, boolean useEpoll) {
        final ClientConnection connection = ClientConnection.connect(address, useEpoll);
        TarasandeMain.Companion.get().getManagerClientMenu().get(ElementMenuToggleForgeFaker.class).setCurrentHandler(ForgeCreator.INSTANCE.createNetHandler(connection));
        return connection;
    }
}
