package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import su.mandora.tarasande.injection.accessor.IConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Redirect(method = "method_34013", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    public void trackResourcePackScreen(MinecraftClient instance, Screen screen) {
        ((IConfirmScreen) screen).tarasande_markAsResourcePacksScreen();
        instance.setScreen(screen);
    }
}
