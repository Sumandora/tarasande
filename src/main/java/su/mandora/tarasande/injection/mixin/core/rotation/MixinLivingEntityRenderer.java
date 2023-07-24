package su.mandora.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.rotation.Rotations;
import su.mandora.tarasande.injection.accessor.ILivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Unique
    private LivingEntity tarasande_entity;

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void storeEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        tarasande_entity = livingEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F", ordinal = 1))
    public float modifyYaw(float delta, float start, float end) {
        if (Rotations.INSTANCE.getAdjustThirdPersonModel().getValue() && tarasande_entity == MinecraftClient.getInstance().player) {
            ILivingEntity accessor = (ILivingEntity) tarasande_entity;
            return MathHelper.lerpAngleDegrees(delta, accessor.tarasande_getPrevHeadYaw(), accessor.tarasande_getHeadYaw());
        }
        return MathHelper.lerpAngleDegrees(delta, start, end);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0))
    public float modifyPitch(float delta, float start, float end) {
        if (Rotations.INSTANCE.getAdjustThirdPersonModel().getValue() && tarasande_entity == MinecraftClient.getInstance().player) {
            ILivingEntity accessor = (ILivingEntity) tarasande_entity;
            return MathHelper.lerp(delta, accessor.tarasande_getPrevHeadPitch(), accessor.tarasande_getHeadPitch());
        }
        return MathHelper.lerp(delta, start, end);
    }
}
