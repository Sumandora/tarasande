package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.norender;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnerBlockEntityRenderer.class)
public class MixinMobSpawnerBlockEntityRenderer {

    @Inject(method = "render(Lnet/minecraft/block/entity/MobSpawnerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD"), cancellable = true)
    public void noRender_render(MobSpawnerBlockEntity mobSpawnerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        if (TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getEntity().getMobInSpawner().should()) {
            ci.cancel();
        }
    }
}
