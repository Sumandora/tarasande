package su.mandora.tarasande.injection.mixin.feature.module.camera;

import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleCamera;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Redirect(method = "getBasicProjectionMatrix", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;setPerspective(FFFF)Lorg/joml/Matrix4f;", remap = false))
    public Matrix4f hookCamera(Matrix4f instance, float fovy, float aspect, float zNear, float zFar) {
        ModuleCamera moduleCamera = ManagerModule.INSTANCE.get(ModuleCamera.class);
        if (moduleCamera.getEnabled().getValue() && moduleCamera.getForceAspectRatio().getValue())
            aspect = (float) moduleCamera.getAspectRatio().getValue();
        return instance.setPerspective(fovy, aspect, zNear, zFar);
    }

}
