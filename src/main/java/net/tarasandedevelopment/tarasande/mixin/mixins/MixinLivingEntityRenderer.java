package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleTrueSight;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Unique
    T livingEntity;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void injectPreRender(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if (livingEntity == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null)
            livingEntity.bodyYaw = livingEntity.prevBodyYaw = RotationUtil.INSTANCE.getFakeRotation().getYaw();
        this.livingEntity = livingEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;setAngles(Lnet/minecraft/entity/Entity;FFFFF)V"))
    public <E extends Entity> void hookedSetAngles(EntityModel<E> entityModel, E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        boolean showFakeAngles = entity == MinecraftClient.getInstance().player && RotationUtil.INSTANCE.getFakeRotation() != null;
        float newHeadYaw = showFakeAngles ? 0 /* this is a delta */ : headYaw;
        float newHeadPitch = showFakeAngles ? RotationUtil.INSTANCE.getFakeRotation().getPitch() : headPitch;
        entityModel.setAngles(entity, limbAngle, limbDistance, animationProgress, newHeadYaw, newHeadPitch);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    public boolean render_hookedIsInvisibleToPlayer(LivingEntity instance, PlayerEntity playerEntity) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleTrueSight.class).getEnabled())
                return false;
        }

        return instance.isInvisibleTo(playerEntity);
    }

    @Redirect(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    public boolean hasLabel_hookedIsInvisibleToPlayer(LivingEntity instance, PlayerEntity playerEntity) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleTrueSight.class).getEnabled())
                return false;
        }

        return instance.isInvisibleTo(playerEntity);
    }

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    public void injectIsVisible(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleTrueSight.class).getEnabled())
                cir.setReturnValue(true);
        }
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void hookedRender(EntityModel<?> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int a, int b, float c, float d, float e, float f) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            ModuleTrueSight moduleTrueSight = TarasandeMain.Companion.get().getManagerModule().get(ModuleTrueSight.class);
            if (moduleTrueSight.getEnabled())
                if (livingEntity.isInvisibleTo(MinecraftClient.getInstance().player) || livingEntity.isInvisible())
                    f = (float) moduleTrueSight.getAlpha().getValue();
        }

        instance.render(matrixStack, vertexConsumer, a, b, c, d, e, f);
    }

}
