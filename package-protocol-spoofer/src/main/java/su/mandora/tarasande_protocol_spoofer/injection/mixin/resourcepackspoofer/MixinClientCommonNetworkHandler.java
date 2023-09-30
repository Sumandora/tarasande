package su.mandora.tarasande_protocol_spoofer.injection.mixin.resourcepackspoofer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande_protocol_spoofer.injection.accessor.IConfirmScreen;

@Mixin(ClientCommonNetworkHandler.class)
public class MixinClientCommonNetworkHandler {

    @Redirect(method = "showPackConfirmationScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    public void trackResourcePackScreen(MinecraftClient instance, Screen screen) {
        ((IConfirmScreen) screen).tarasande_markAsResourcePacksScreen();
        instance.setScreen(screen);
    }
}
