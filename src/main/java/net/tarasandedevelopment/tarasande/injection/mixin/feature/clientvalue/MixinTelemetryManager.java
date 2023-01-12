package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.util.telemetry.TelemetryManager;
import net.minecraft.client.util.telemetry.TelemetrySender;
import net.tarasandedevelopment.tarasande.TarasandeMainKt;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.PrivacyValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.logging.Level;

@Mixin(TelemetryManager.class)
public class MixinTelemetryManager {

    @Inject(method = "getSender", at = @At("HEAD"), cancellable = true)
    public void disableTelemetry(CallbackInfoReturnable<TelemetrySender> cir) {
        // This bypasses the TarasandeMain#disabled, because ms spying on us is a major problem
        if (PrivacyValues.INSTANCE.getDisableTelemetry().getValue()) {
            TarasandeMainKt.getLogger().log(Level.INFO, "Returned dummy telemetry sender in order to hold back telemetry");
            cir.setReturnValue(TelemetrySender.NOOP);
        }
    }

}
