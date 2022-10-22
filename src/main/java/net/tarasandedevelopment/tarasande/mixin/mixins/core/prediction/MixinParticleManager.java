package net.tarasandedevelopment.tarasande.mixin.mixins.core.prediction;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.tarasandedevelopment.tarasande.mixin.accessor.prediction.IParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager implements IParticleManager {

    @Unique
    private boolean tarasande_particlesEnabled = true;

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    public void disableParticles(Particle particle, CallbackInfo ci) {
        if (!tarasande_particlesEnabled)
            ci.cancel();
    }

    @Override
    public boolean tarasande_areParticlesEnabled() {
        return tarasande_particlesEnabled;
    }

    @Override
    public void tarasande_setParticlesEnabled(boolean particlesEnabled) {
        this.tarasande_particlesEnabled = particlesEnabled;
    }
}
