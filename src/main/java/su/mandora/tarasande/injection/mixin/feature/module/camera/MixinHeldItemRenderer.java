package su.mandora.tarasande.injection.mixin.feature.module.camera;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleCamera;
import su.mandora.tarasande.util.extension.minecraft.HandKt;

import java.util.HashMap;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Unique
    private final HashMap<Hand, Boolean> tarasande_skipNext = new HashMap<>();

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEquipOffset(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/Arm;F)V"))
    public void hookCamera_applyAfterEatAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        Boolean blocked = tarasande_skipNext.get(hand);
        if (blocked != null && blocked) {
            tarasande_skipNext.put(hand, false);
            return;
        }
        ModuleCamera moduleCamera = ManagerModule.INSTANCE.get(ModuleCamera.class);
        if (moduleCamera.getEnabled().getValue())
            moduleCamera.applyTransform(matrices, HandKt.toArm(hand));
    }

    @Inject(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;applyEatOrDrinkTransformation(Lnet/minecraft/client/util/math/MatrixStack;FLnet/minecraft/util/Arm;Lnet/minecraft/item/ItemStack;)V"))
    public void hookCamera_applyBeforeEatAnimation(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        tarasande_skipNext.put(hand, true);
        ModuleCamera moduleCamera = ManagerModule.INSTANCE.get(ModuleCamera.class);
        if (moduleCamera.getEnabled().getValue())
            moduleCamera.applyTransform(matrices, HandKt.toArm(hand));
    }

    @Inject(method = "renderArmHoldingItem", at = @At("HEAD"))
    public void hookCamera(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
        ModuleCamera moduleCamera = ManagerModule.INSTANCE.get(ModuleCamera.class);
        if (moduleCamera.getEnabled().getValue())
            moduleCamera.applyTransform(matrices, arm);
    }
}
