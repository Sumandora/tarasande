package de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.mouse.SensitivityCalculations;
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues;
import kotlin.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.exploit.ModuleNoPitchLimit;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleFreeCam;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Rotation.Companion.class, remap = false)
public class MixinRotationCompanion {
    @Inject(method = "calculateNewRotation", at = @At("HEAD"), cancellable = true)
    public void overwriteCalculation(Rotation prevRotation, Pair<Double, Double> cursorDeltas, CallbackInfoReturnable<Rotation> cir) {
        if (ProtocolHackValues.INSTANCE.getEmulateMouseInputs().getValue() && !ManagerModule.INSTANCE.get(ModuleFreeCam.class).getEnabled().getValue()) {
            float f = SensitivityCalculations.get1_12SensitivityFor1_19(MinecraftClient.getInstance().options.getMouseSensitivity().getValue()) * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = ((int) cursorDeltas.getFirst().doubleValue()) * f1;
            float f3 = ((int) cursorDeltas.getSecond().doubleValue()) * f1;
            Rotation newRotation = new Rotation(
                    (float)((double)prevRotation.getYaw() + (double)f2 * 0.15D),
                    (float)((double)prevRotation.getPitch() + (double)f3 * 0.15D)
            );
            if(!ManagerModule.INSTANCE.get(ModuleNoPitchLimit.class).getEnabled().getValue())
                newRotation = newRotation.withPitch(MathHelper.clamp(newRotation.getPitch(), -90.0F, 90.0F));
            cir.setReturnValue(newRotation);
        }

    }
}
