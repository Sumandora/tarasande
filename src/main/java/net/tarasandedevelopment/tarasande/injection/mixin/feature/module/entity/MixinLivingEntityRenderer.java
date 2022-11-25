package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.entity;

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
import net.tarasandedevelopment.tarasande.injection.accessor.IEntity;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleTrueSight;
import net.tarasandedevelopment.tarasande.util.dummy.AbstractClientPlayerEntityDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    @Unique
    T tarasande_livingEntity;

    protected MixinLivingEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void modifyYaw(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.tarasande_livingEntity = livingEntity;
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    public boolean hookTrueSight_render(LivingEntity instance, PlayerEntity playerEntity) {
        if (TarasandeMain.Companion.managerModule().get(ModuleTrueSight.class).getEnabled())
            return false;

        return instance.isInvisibleTo(playerEntity);
    }

    @Redirect(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isInvisibleTo(Lnet/minecraft/entity/player/PlayerEntity;)Z"))
    public boolean hookTrueSight_hasLabel(LivingEntity instance, PlayerEntity playerEntity) {
        if (TarasandeMain.Companion.managerModule().get(ModuleTrueSight.class).getEnabled())
            return false;

        return instance.isInvisibleTo(playerEntity);
    }

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    public void hookTrueSight_isVisible(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeMain.Companion.managerModule().get(ModuleTrueSight.class).getEnabled())
            cir.setReturnValue(true);
    }

    @Redirect(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"))
    public void hookTrueSight(EntityModel<?> instance, MatrixStack matrixStack, VertexConsumer vertexConsumer, int a, int b, float c, float d, float e, float f) {
        if (!(tarasande_livingEntity instanceof AbstractClientPlayerEntityDummy)) {
            ModuleTrueSight moduleTrueSight = TarasandeMain.Companion.managerModule().get(ModuleTrueSight.class);
            if (moduleTrueSight.getEnabled()) {
                IEntity accessor = (IEntity) tarasande_livingEntity;
                if (tarasande_livingEntity.isInvisibleTo(MinecraftClient.getInstance().player) || accessor.tarasande_forceGetFlag(Entity.INVISIBLE_FLAG_INDEX))
                    f = (float) moduleTrueSight.getAlpha().getValue();
            }
        }

        instance.render(matrixStack, vertexConsumer, a, b, c, d, e, f);
    }
}
