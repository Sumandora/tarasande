package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.text.Text;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity> {

    @SuppressWarnings("CancellableInjectionUsage") // This plugin is garbage
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void noRender_shouldRender(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        final ModuleNoRender moduleNoRender = ManagerModule.INSTANCE.get(ModuleNoRender.class);
        if (!moduleNoRender.getEnabled().getValue()) return;

        if (moduleNoRender.getEntity().getEntities().isSelected(entity.getType())) {
            cir.cancel();
        }
        if (moduleNoRender.getWorld().getFallingBlocks().getValue() && entity instanceof FallingBlockEntity) {
            cir.cancel();
        }
    }

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    public void noRender_RenderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        final ModuleNoRender moduleNoRender = ManagerModule.INSTANCE.get(ModuleNoRender.class);
        if (!moduleNoRender.getEnabled().getValue()) return;

        if (moduleNoRender.getEntity().getEntityNameTags().isSelected(entity.getType())) {
            ci.cancel();
        }
    }
}
