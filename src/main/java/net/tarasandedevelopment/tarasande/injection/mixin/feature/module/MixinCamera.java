package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.render.Camera;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleCameraNoClip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class MixinCamera {

    @Inject(method = "clipToSpace", at = @At("HEAD"), cancellable = true)
    public void hookCameraNoClip(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        if (ManagerModule.INSTANCE.get(ModuleCameraNoClip.class).getEnabled()) {
            cir.setReturnValue(desiredCameraDistance);
        }
    }
}
