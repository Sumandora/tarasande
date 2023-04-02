package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventParticle;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;

@Mixin(ParticleManager.class)
public abstract class MixinParticleManager {

    @Shadow
    @Nullable
    protected abstract <T extends ParticleEffect> Particle createParticle(T parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ);

    @Inject(method = "addBlockBreakParticles", at = @At("HEAD"), cancellable = true)
    public void noRender_addBlockBreakParticles(BlockPos pos, BlockState state, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getBlockBreakParticles().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "addBlockBreakingParticles", at = @At("HEAD"), cancellable = true)
    public void noRender_addBlockBreakingParticles(BlockPos pos, Direction direction, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoRender.class).getWorld().getBlockBreakParticles().should()) {
            ci.cancel();
        }
    }

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    public void noRender_addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        final EventParticle eventParticle = new EventParticle(parameters);
        EventDispatcher.INSTANCE.call(eventParticle);

        if (eventParticle.getCancelled()) {
            if (parameters.getType() == ParticleTypes.FLASH) {
                cir.setReturnValue(createParticle(parameters, x, y, z, velocityX, velocityY, velocityZ));
            } else {
                cir.cancel();
            }
        }
    }
}
