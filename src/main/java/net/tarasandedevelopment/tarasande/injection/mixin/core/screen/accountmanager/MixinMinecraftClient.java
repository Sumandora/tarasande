package net.tarasandedevelopment.tarasande.injection.mixin.core.screen.accountmanager;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.screen.accountmanager.account.Account;
import net.tarasandedevelopment.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void correctSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        final Account account = TarasandeMain.Companion.managerScreenExtension().get(ScreenExtensionButtonListMultiplayerScreen.class).getScreenBetterSlotListAccountManager().getCurrentAccount();
        if (account != null) {
            cir.setReturnValue(account.getSessionService());
        }
    }
}
