package su.mandora.tarasande.mixin.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventBoundingBoxOverride;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {

    @Redirect(method = "raycast", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getBoundingBox()Lnet/minecraft/util/math/Box;"))
    private static Box hookedGetBoundingBox(Entity instance) {
        EventBoundingBoxOverride eventBoundingBoxOverride = new EventBoundingBoxOverride(instance, instance.getBoundingBox());
        TarasandeMain.Companion.get().getManagerEvent().call(eventBoundingBoxOverride);
        return eventBoundingBoxOverride.getBoundingBox();
    }

}
