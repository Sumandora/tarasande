package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRenderEntity;
import su.mandora.tarasande.util.math.rotation.RotationUtil;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    float prevBodyYaw;
    float prevPrevBodyYaw;

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void injectPreRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        prevPrevBodyYaw = livingEntity.prevBodyYaw;
        prevBodyYaw = livingEntity.bodyYaw;

        if (livingEntity == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null)
            livingEntity.bodyYaw = livingEntity.prevBodyYaw = RotationUtil.INSTANCE.getFakeRotation().getYaw();

        TarasandeMain.Companion.get().getManagerEvent().call(new EventRenderEntity(livingEntity, EventRenderEntity.State.PRE));
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE))
    public void injectPostRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRenderEntity(livingEntity, EventRenderEntity.State.POST));
        livingEntity.bodyYaw = prevBodyYaw;
        livingEntity.prevBodyYaw = prevPrevBodyYaw;
    }

//    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerpAngleDegrees(FFF)F"))
//    public float hookedLerpAngleDegrees(float delta, float start, float end) {
//        return end;
//    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
    public <E extends Entity> void hookedSetAngles(EntityModel<E> entityModel, E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean showFakeAngles = entity == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null;
        float newHeadYaw = showFakeAngles ? 0 /* this is a delta */ : headYaw;
        float newHeadPitch = showFakeAngles ? RotationUtil.INSTANCE.getFakeRotation().getPitch() : headPitch;
        entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, newHeadYaw, newHeadPitch);
    }

}
