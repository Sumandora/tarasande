package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {FireworksSparkParticle.Explosion.class, FireworksSparkParticle.Flash.class})
public class MixinFireworksSparkParticleSubExplosionAndFlash {

    @Inject(method = "buildGeometry", at = @At("HEAD"), cancellable = true)
    public void noRender_buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getFireworkExplosions().should()) {
            ci.cancel();
        }
    }
}
