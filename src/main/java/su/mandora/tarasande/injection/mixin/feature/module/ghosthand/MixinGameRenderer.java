package su.mandora.tarasande.injection.mixin.feature.module.ghosthand;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import su.mandora.tarasande.injection.accessor.IGameRenderer;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleGhostHand;
import su.mandora.tarasande.util.extension.minecraft.HitResultKt;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final MinecraftClient client;

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;")))
    public double hookGhostHand(Vec3d instance, Vec3d vec) {
        if(!((IGameRenderer) this).tarasande_isSelfInflicted()) {
            ModuleGhostHand moduleGhostHand = ManagerModule.INSTANCE.get(ModuleGhostHand.class);
            if (moduleGhostHand.getEnabled().getValue() && moduleGhostHand.getMode().isSelected(1) && HitResultKt.isBlockHitResult(client.crosshairTarget))
                return moduleGhostHand.getBlockDistance();
        }
        return instance.squaredDistanceTo(vec);
    }

}
