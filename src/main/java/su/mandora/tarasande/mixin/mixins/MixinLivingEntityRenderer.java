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
import net.minecraft.util.math.MathHelper;
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

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void injectPreRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRenderEntity(livingEntity, EventRenderEntity.State.PRE));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", shift = At.Shift.BEFORE))
    public void injectPostRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventRenderEntity(livingEntity, EventRenderEntity.State.POST));
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
    public <E extends Entity> void hookedSetAngles(EntityModel<E> entityModel, E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean showFakeAngles = entity == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null;
        entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress,
                showFakeAngles ? MathHelper.wrapDegrees(RotationUtil.INSTANCE.getFakeRotation().getYaw()) - MathHelper.lerpAngleDegrees(MinecraftClient.getInstance().getTickDelta(), ((LivingEntity) entity).prevBodyYaw, ((LivingEntity) entity).bodyYaw) : headYaw,
                showFakeAngles ? RotationUtil.INSTANCE.getFakeRotation().getPitch() : headPitch
        );
    }

}
