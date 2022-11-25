package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Box;
import su.mandora.events.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.EventBoundingBoxOverride;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {

    @Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private static Box hookEventBoundingBoxOverride(Entity instance) {
        EventBoundingBoxOverride eventBoundingBoxOverride = new EventBoundingBoxOverride(instance, instance.getBoundingBox());
        EventDispatcher.INSTANCE.call(eventBoundingBoxOverride);
        return eventBoundingBoxOverride.getBoundingBox();
    }

}
