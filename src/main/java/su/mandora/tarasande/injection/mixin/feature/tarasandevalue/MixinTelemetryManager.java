package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMainKt;
import su.mandora.tarasande.feature.tarasandevalue.impl.PrivacyValues;

import java.util.logging.Level;

@Mixin(TelemetryManager.class)
public class MixinTelemetryManager {

    @Inject(method = "getSender", at = @At("HEAD"), cancellable = true)
    public void disableTelemetry(CallbackInfoReturnable<TelemetrySender> cir) {
        if (PrivacyValues.INSTANCE.getDisableTelemetry().getValue()) {
            TarasandeMainKt.getLogger().log(Level.INFO, "Returned dummy telemetry sender in order to hold back telemetry");
            cir.setReturnValue(TelemetrySender.NOOP);
        }
    }

}
