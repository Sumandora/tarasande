package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.clampclient.injection.instrumentation_1_8.definition.MathHelper_1_8;
import de.florianmichael.vialoadingbase.ViaLoadingBase;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Vec3d.class)
public class MixinVec3d {

    @Redirect(method = "distanceTo", at = @At(value = "INVOKE", target = "Ljava/lang/Math;sqrt(D)D"))
    public double onSqrt(double a) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            return (double) MathHelper_1_8.sqrt_double(a);
        }

        return Math.sqrt(a);
    }

    @Inject(method = "fromPolar(FF)Lnet/minecraft/util/math/Vec3d;", at = @At("HEAD"), cancellable = true)
    private static void fromPolar(float pitch, float yaw, CallbackInfoReturnable<Vec3d> cir) {
        if (ViaLoadingBase.getTargetVersion().isOlderThanOrEqualTo(ProtocolVersion.v1_12_2)) {
            float f = MathHelper_1_8.cos(-yaw * 0.017453292F - (float)Math.PI);
            float f1 = MathHelper_1_8.sin(-yaw * 0.017453292F - (float)Math.PI);
            float f2 = -MathHelper_1_8.cos(-pitch * 0.017453292F);
            float f3 = MathHelper_1_8.sin(-pitch * 0.017453292F);

            cir.setReturnValue(new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2)));
        }
    }
}
