package net.tarasandedevelopment.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class MixinInventoryScreen {

    @Unique
    private static float tarasande_headPitch;
    @Unique
    private static float tarasande_prevHeadPitch;
    @Unique
    private static float tarasande_headYaw;
    @Unique
    private static float tarasande_prevHeadYaw;

    @Inject(method = "drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIIFFLnet/minecraft/entity/LivingEntity;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F", ordinal = 1))
    private static void clearHeadRotation(MatrixStack matrices, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;

        tarasande_headYaw = accessor.tarasande_getHeadYaw();
        tarasande_prevHeadYaw = accessor.tarasande_getPrevHeadYaw();

        tarasande_headPitch = accessor.tarasande_getHeadPitch();
        tarasande_prevHeadPitch = accessor.tarasande_getPrevHeadPitch();

        accessor.tarasande_setHeadYaw(entity.getYaw());
        accessor.tarasande_setPrevHeadYaw(entity.getYaw());

        accessor.tarasande_setHeadPitch(entity.getPitch());
        accessor.tarasande_setPrevHeadPitch(entity.getPitch());
    }

    @Inject(method = "drawEntity(Lnet/minecraft/client/util/math/MatrixStack;IIIFFLnet/minecraft/entity/LivingEntity;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F", ordinal = 2))
    private static void resetHeadRotation(MatrixStack matrices, int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;

        accessor.tarasande_setHeadYaw(tarasande_headYaw);
        accessor.tarasande_setPrevHeadYaw(tarasande_prevHeadYaw);

        accessor.tarasande_setHeadPitch(tarasande_headPitch);
        accessor.tarasande_setPrevHeadPitch(tarasande_prevHeadPitch);
    }

}
