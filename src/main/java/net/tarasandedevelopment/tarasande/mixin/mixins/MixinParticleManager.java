package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.tarasandedevelopment.tarasande.mixin.accessor.IParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager implements IParticleManager {

    @Unique
    boolean particlesEnabled = true;

    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    public void injectAddParticle(Particle particle, CallbackInfo ci) {
        if (!particlesEnabled)
            ci.cancel();
    }

    @Override
    public boolean tarasande_areParticlesEnabled() {
        return particlesEnabled;
    }

    @Override
    public void tarasande_setParticlesEnabled(boolean particlesEnabled) {
        this.particlesEnabled = particlesEnabled;
    }
}
