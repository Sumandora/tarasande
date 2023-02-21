package de.florianmichael.tarasande_custom_minecraft.injection.mixin.detailedconnectionstatus;

import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState;
import de.florianmichael.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class MixinConnectScreen extends Screen {

    public MixinConnectScreen(Text title) {
        super(title);
    }

    @Inject(method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    private static void reset(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.UNKNOWN);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ConnectScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    public void hideConnectionStatus(MatrixStack matrixStack, TextRenderer textRenderer, Text text, int x, int y, int color) {
        if (DetailedConnectionStatus.INSTANCE.getShowDetailedConnectionStatus().getValue() && DetailedConnectionStatus.INSTANCE.getShowDetailedConnectionStatus().isEnabled().invoke()) return;

        drawCenteredText(matrixStack, textRenderer, text, x, y, color);
    }
}
