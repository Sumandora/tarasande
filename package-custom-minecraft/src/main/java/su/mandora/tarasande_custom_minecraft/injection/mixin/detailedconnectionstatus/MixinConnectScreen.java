package su.mandora.tarasande_custom_minecraft.injection.mixin.detailedconnectionstatus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.ConnectionState;
import su.mandora.tarasande_custom_minecraft.tarasandevalues.debug.DetailedConnectionStatus;

@Mixin(ConnectScreen.class)
public class MixinConnectScreen extends Screen {

    public MixinConnectScreen(Text title) {
        super(title);
    }

    @Inject(method = "connect(Lnet/minecraft/client/gui/screen/Screen;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V", at = @At("HEAD"))
    private static void reset(Screen screen, MinecraftClient client, ServerAddress address, ServerInfo info, CallbackInfo ci) {
        DetailedConnectionStatus.INSTANCE.updateConnectionState(ConnectionState.UNKNOWN);
    }
}
