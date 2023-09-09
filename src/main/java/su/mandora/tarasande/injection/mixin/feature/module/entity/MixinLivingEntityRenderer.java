package su.mandora.tarasande.injection.mixin.feature.module.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.injection.accessor.IEntity;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleTrueSight;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity> {

    @Unique
    private T tarasande_livingEntity;

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void captureEntity(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        this.tarasande_livingEntity = livingEntity;
    }

    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true)
    public void hookTrueSight_isVisible(T entity, CallbackInfoReturnable<Boolean> cir) {
        if (ManagerModule.INSTANCE.get(ModuleTrueSight.class).getEnabled().getValue())
            cir.setReturnValue(true);
    }

    @ModifyArg(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"), index = 7)
    public float hookTrueSight(float transparency) {
        ModuleTrueSight moduleTrueSight = ManagerModule.INSTANCE.get(ModuleTrueSight.class);
        if (moduleTrueSight.getEnabled().getValue()) {
            if (tarasande_livingEntity.isInvisibleTo(MinecraftClient.getInstance().player) || ((IEntity) tarasande_livingEntity).tarasande_forceGetFlag(Entity.INVISIBLE_FLAG_INDEX))
                transparency = (float) moduleTrueSight.getAlpha().getValue();
        }

        return transparency;
    }
}
