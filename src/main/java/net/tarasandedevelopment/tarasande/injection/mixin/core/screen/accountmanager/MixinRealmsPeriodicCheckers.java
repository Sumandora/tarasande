package net.tarasandedevelopment.tarasande.injection.mixin.core.screen.accountmanager;

import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.RealmsPeriodicCheckers;
import net.tarasandedevelopment.tarasande.injection.accessor.IRealmsPeriodicCheckers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RealmsPeriodicCheckers.class)
public class MixinRealmsPeriodicCheckers implements IRealmsPeriodicCheckers {

    @Unique
    private RealmsClient tarasande_client;


    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/realms/util/PeriodicRunnerFactory;create(Ljava/lang/String;Ljava/util/concurrent/Callable;Ljava/time/Duration;Lnet/minecraft/client/util/Backoff;)Lnet/minecraft/client/realms/util/PeriodicRunnerFactory$PeriodicRunner;", ordinal = 0))
    public void storeClient(RealmsClient client, CallbackInfo ci) {
        tarasande_client = client;
    }

    @Override
    public RealmsClient tarasande_getClient() {
        return tarasande_client;
    }
}
