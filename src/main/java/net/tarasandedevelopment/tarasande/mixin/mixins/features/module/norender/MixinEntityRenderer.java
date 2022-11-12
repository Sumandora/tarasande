package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.norender;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"))
    public void noRender_shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        final ModuleNoRender moduleNoRender = TarasandeMain.Companion.managerModule().get(ModuleNoRender.class);
        if (!moduleNoRender.getEnabled()) return;

        if (moduleNoRender.getEntity().getEntities().getList().contains(entity.getType())) {
            cir.cancel();
        }
        if (moduleNoRender.getWorld().getFallingBlocks().getValue() && entity instanceof FallingBlockEntity) {
            cir.cancel();
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void noRender_RenderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        final ModuleNoRender moduleNoRender = TarasandeMain.Companion.managerModule().get(ModuleNoRender.class);
        if (!moduleNoRender.getEnabled()) return;

        if (moduleNoRender.getEntity().getEntityNameTags().getList().contains(entity.getType())) {
            ci.cancel();
        }
    }
}
