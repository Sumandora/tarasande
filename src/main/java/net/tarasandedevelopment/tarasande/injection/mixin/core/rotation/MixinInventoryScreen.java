package net.tarasandedevelopment.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
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
    private static float tarasande_prevHeadPitch;

    @Inject(method = "drawEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F", ordinal = 1))
    private static void clearHeadPitch(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;
        tarasande_headPitch = accessor.tarasande_getHeadPitch();
        tarasande_prevHeadPitch = accessor.tarasande_getPrevHeadPitch();
        accessor.tarasande_setHeadPitch(entity.getPitch());
        accessor.tarasande_setPrevHeadPitch(entity.getPitch());
    }

    @Inject(method = "drawEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;headYaw:F", ordinal = 2))
    private static void resetHeadPitch(int x, int y, int size, float mouseX, float mouseY, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;
        accessor.tarasande_setHeadPitch(tarasande_headPitch);
        accessor.tarasande_setPrevHeadPitch(tarasande_prevHeadPitch);
    }

}
