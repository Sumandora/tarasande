package su.mandora.tarasande.injection.mixin.feature.module.camera;

import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleCamera;

@Mixin(Camera.class)
public class MixinCamera {

    @ModifyArg(method = "update", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/render/Camera;clipToSpace(D)D"), index = 0)
    public double hookCamera(double d) {
        ModuleCamera moduleCamera = ManagerModule.INSTANCE.get(ModuleCamera.class);
        if (moduleCamera.getEnabled().getValue() && moduleCamera.getChangeThirdPersonDistance().getValue())
            return moduleCamera.getThirdPersonDistance().getValue();
        return d;
    }

}
