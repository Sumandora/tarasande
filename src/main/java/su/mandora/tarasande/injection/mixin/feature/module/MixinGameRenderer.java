package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoHurtCam;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void hookNoHurtcam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoHurtCam.class).getEnabled().getValue())
            ci.cancel();
    }
}
