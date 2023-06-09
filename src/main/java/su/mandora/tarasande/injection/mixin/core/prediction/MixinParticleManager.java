package su.mandora.tarasande.injection.mixin.core.prediction;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.prediction.IParticleManager;

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
    public boolean tarasande_isParticlesEnabled() {
        return tarasande_particlesEnabled;
    }

    @Override
    public void tarasande_setParticlesEnabled(boolean particlesEnabled) {
        this.tarasande_particlesEnabled = particlesEnabled;
    }
}
