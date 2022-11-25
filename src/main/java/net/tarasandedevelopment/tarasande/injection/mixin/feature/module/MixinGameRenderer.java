package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.player.ModuleNoMiningTrace;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Redirect(method = "updateTargetedEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileUtil;raycast(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;D)Lnet/minecraft/util/hit/EntityHitResult;"))
    public @Nullable EntityHitResult hookNoMiningTrace(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d) {
        ModuleNoMiningTrace moduleNoMiningTrace = TarasandeMain.Companion.managerModule().get(ModuleNoMiningTrace.class);
        if (moduleNoMiningTrace.getEnabled())
            if (!moduleNoMiningTrace.getOnlyWhenPickaxe().getValue() || (MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof PickaxeItem || MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof PickaxeItem))
                if (MinecraftClient.getInstance().crosshairTarget == null || MinecraftClient.getInstance().crosshairTarget.getType() == HitResult.Type.BLOCK)
                    return null;
        return ProjectileUtil.raycast(entity, min, max, box, predicate, d);
    }
}
