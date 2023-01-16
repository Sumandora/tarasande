package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera.Camera;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.debug.camera.ViewModel;
import net.tarasandedevelopment.tarasande.util.math.MathUtil;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Unique
    private boolean tarasande_skipNext = false;

    @Unique
    private void tarasande_applyTransform(MatrixStack matrices, Hand hand) {
        ViewModel viewModel = (ViewModel) ((Camera) DebugValues.INSTANCE.getCamera().getValuesOwner()).getViewModel().getValuesOwner();

        double handOffset = -MathUtil.INSTANCE.roundAwayFromZero(hand.ordinal() - 0.5) /* rofl */;

        matrices.translate(viewModel.getX().getValue() * handOffset, viewModel.getY().getValue(), viewModel.getZ().getValue());
        matrices.multiply(
                new Quaternionf()
                        .rotateX((float) Math.toRadians(viewModel.getRotateX().getValue()))
                        .rotateY((float) Math.toRadians(viewModel.getRotateY().getValue() * handOffset))
                        .rotateZ((float) Math.toRadians(viewModel.getRotateZ().getValue()))
        );
        matrices.scale((float) viewModel.getScaleX().getValue(), (float) viewModel.getScaleY().getValue(), (float) viewModel.getScaleZ().getValue());
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"))
    public void applyViewModelValues(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if(tarasande_skipNext) {
            tarasande_skipNext = false;
            return;
        }
        tarasande_applyTransform(matrices, hand);
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEatOrDrinkTransformation(Lnet/minecraft/client/util/math/MatrixStack;FLnet/minecraft/util/Arm;Lnet/minecraft/item/ItemStack;)V"))
    public void applyBeforeEatAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        tarasande_applyTransform(matrices, hand);
        tarasande_skipNext = true;
    }
}
