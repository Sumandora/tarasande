package net.tarasandedevelopment.tarasande.mixin.mixins.core.screens.accountmanager;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.base.screen.clientmenu.accountmanager.account.Account;
import net.tarasandedevelopment.tarasande.screen.clientmenu.ElementMenuScreenAccountManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {


    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void correctSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        Account account = TarasandeMain.Companion.get().getManagerClientMenu().get(ElementMenuScreenAccountManager.class).getScreenBetterSlotListAccountManager().getCurrentAccount();
        if (account != null)
            cir.setReturnValue(account.getSessionService());
    }

}
