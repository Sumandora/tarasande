package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.features.module.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"))
    public void noRender_shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        final ModuleNoRender moduleNoRender = TarasandeMain.Companion.get().getManagerModule().get(ModuleNoRender.class);

        if (moduleNoRender.getEntity().noEntity(entity)) {
            cir.cancel();
        }
        if (moduleNoRender.getWorld().getFallingBlocks().getValue() && entity instanceof FallingBlockEntity) {
            cir.cancel();
        }
    }
}
