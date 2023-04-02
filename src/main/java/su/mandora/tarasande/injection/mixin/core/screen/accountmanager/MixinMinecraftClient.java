package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.screen.accountmanager.account.Account;
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension;
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void correctSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        final Account account = ManagerScreenExtension.INSTANCE.get(ScreenExtensionButtonListMultiplayerScreen.class).getScreenBetterSlotListAccountManager().getCurrentAccount();
        if (account != null) {
            cir.setReturnValue(account.getService());
        }
    }
}
