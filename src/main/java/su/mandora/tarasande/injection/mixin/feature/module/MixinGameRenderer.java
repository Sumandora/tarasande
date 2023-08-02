package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleNoMiningTrace;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleNoHurtCam;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    public @Nullable EntityHitResult hookNoMiningTrace(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d) {
        if (ManagerModule.INSTANCE.get(ModuleNoMiningTrace.class).shouldCancel())
            return null;
        return ProjectileUtil.raycast(entity, min, max, box, predicate, d);
    }

    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    public void hookNoHurtcam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleNoHurtCam.class).getEnabled().getValue())
            ci.cancel();
    }
}
