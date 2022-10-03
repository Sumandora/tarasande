package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.util.telemetry.TelemetrySender;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.misc.ModuleDisableTelemetry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TelemetrySender.class)
public class MixinTelemetrySender {

    @SuppressWarnings("InvalidInjectorMethodSignature") // Coerce is not supported cuz massive brain
    @Inject(method = "send(Lnet/minecraft/client/util/telemetry/TelemetrySender$PlayerGameMode;)V", at = @At("HEAD"), cancellable = true)
    public void injectSend(@Coerce Object gameMode, CallbackInfo ci) {
        // This will bypass the event system
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleDisableTelemetry.class).getEnabled()) {
            TarasandeMain.Companion.get().getLogger().info("Blocked telemetry services");
            ci.cancel();
        }
    }

}
