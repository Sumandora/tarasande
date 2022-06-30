package su.mandora.tarasande.mixin.mixins;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.util.ProfileKeys;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.module.misc.ModuleNoSign;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(ProfileKeys.class)
public class MixinProfileKeys {

    // This methods spams the console if it cant find a key pair
    @Inject(method = "getKeyPair", at = @At("HEAD"), cancellable = true)
    public void injectGetKeyPair(UserApiService userApiService, CallbackInfoReturnable<CompletableFuture<Optional<PlayerKeyPair>>> cir) {
        if (TarasandeMain.Companion.get().getManagerModule() != null && TarasandeMain.Companion.get().getManagerModule().get(ModuleNoSign.class).getEnabled()) {
            cir.setReturnValue(CompletableFuture.supplyAsync(Optional::empty, Util.getMainWorkerExecutor()));
        }
    }

}
