package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleCameraNoClip;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void hookCameraNoClip(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (ManagerModule.INSTANCE.get(ModuleCameraNoClip.class).getEnabled().getValue()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }
}
