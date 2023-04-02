package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(value = FireworksSparkParticle.Flash.class, targets = "net.minecraft.client.particle.FireworksSparkParticle$Explosion")
public class MixinFireworksSparkParticle_ExplosionAndFlash {

    @Inject(method = "buildGeometry", at = @At("HEAD"), cancellable = true)
    public void noRender_buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getFireworkExplosions().should()) {
            ci.cancel();
        }
    }
}
