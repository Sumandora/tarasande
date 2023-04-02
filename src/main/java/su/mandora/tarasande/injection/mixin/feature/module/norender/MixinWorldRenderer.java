package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    public void noRender_renderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getWeather().should()) {
            ci.cancel();
        }
    }
}
