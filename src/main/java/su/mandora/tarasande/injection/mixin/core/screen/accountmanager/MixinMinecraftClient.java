package su.mandora.tarasande.injection.mixin.core.screen.accountmanager;

import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.injection.accessor.IMinecraftClient;
import su.mandora.tarasande.system.screen.accountmanager.account.Account;
import su.mandora.tarasande.system.screen.screenextensionsystem.ManagerScreenExtension;
import su.mandora.tarasande.system.screen.screenextensionsystem.impl.multiplayer.ScreenExtensionButtonListMultiplayerScreen;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient implements IMinecraftClient {

    @Unique
    private RunArgs tarasande_runArgs;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;navigationType:Lnet/minecraft/client/gui/navigation/GuiNavigationType;", shift = At.Shift.BEFORE))
    public void captureRunArgs(RunArgs args, CallbackInfo ci) {
        tarasande_runArgs = args;
    }

    @Inject(method = "getSessionService", at = @At("RETURN"), cancellable = true)
    public void correctSessionService(CallbackInfoReturnable<MinecraftSessionService> cir) {
        final Account account = ManagerScreenExtension.INSTANCE.get(ScreenExtensionButtonListMultiplayerScreen.class).getScreenBetterSlotListAccountManager().getCurrentAccount();
        if (account != null) {
            cir.setReturnValue(account.getService());
        }
    }

    @Override
    public RunArgs tarasande_getRunArgs() {
        return tarasande_runArgs;
    }
}
