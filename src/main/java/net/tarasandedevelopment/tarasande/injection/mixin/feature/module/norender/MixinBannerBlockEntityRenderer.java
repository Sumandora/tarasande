package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoRender;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntityRenderer.class)
public class MixinBannerBlockEntityRenderer {

    @Shadow
    @Final
    private ModelPart pillar;

    @Shadow
    @Final
    private ModelPart crossbar;

    @Inject(method = "render(Lnet/minecraft/block/entity/BannerBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V",
            at = @At("HEAD"), cancellable = true)
    public void noRender_render(BannerBlockEntity bannerBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        final ValueMode modeValue = TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getWorld().getBanners();
        if (!TarasandeMain.Companion.managerModule().get(ModuleNoRender.class).getEnabled()) return;

        if (!modeValue.isSelected(0)) {
            ci.cancel();
            if (modeValue.isSelected(2)) {
                final BlockState blockState = bannerBlockEntity.getCachedState();

                if (blockState.getBlock() instanceof BannerBlock) {
                    this.pillar.visible = true;
                    this.crossbar.visible = false;
                    tarasande_renderPillar(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j);
                } else {
                    this.pillar.visible = false;
                    this.crossbar.visible = true;
                    tarasande_renderCrossbar(bannerBlockEntity, matrixStack, vertexConsumerProvider, i, j);
                }
            }
        }
    }

    @Unique
    private void tarasande_renderPillar(BannerBlockEntity bannerBlockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        BlockState blockState = bannerBlockEntity.getCachedState();
        matrixStack.translate(0.5, 0.5, 0.5);
        float h = (float) (-(Integer) blockState.get(BannerBlock.ROTATION) * 360) / 16.0F;
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
        matrixStack.push();
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        final VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.pillar.render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
        matrixStack.pop();
    }

    @Unique
    private void tarasande_renderCrossbar(BannerBlockEntity bannerBlockEntity, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        matrixStack.push();
        BlockState blockState = bannerBlockEntity.getCachedState();
        matrixStack.translate(0.5, -0.1666666716337204, 0.5);
        float h = -blockState.get(WallBannerBlock.FACING).asRotation();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
        matrixStack.translate(0.0, -0.3125, -0.4375);
        matrixStack.push();
        matrixStack.scale(0.6666667F, -0.6666667F, -0.6666667F);
        final VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumerProvider, RenderLayer::getEntitySolid);
        this.crossbar.render(matrixStack, vertexConsumer, i, j);
        matrixStack.pop();
        matrixStack.pop();
    }
}
