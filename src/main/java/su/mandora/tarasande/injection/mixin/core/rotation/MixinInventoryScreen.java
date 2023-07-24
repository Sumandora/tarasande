package su.mandora.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.injection.accessor.ILivingEntity;

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

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At("HEAD"))
    private static void clearHeadRotation(DrawContext context, int x, int y, int size, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, CallbackInfo ci) {
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

    @Inject(method = "drawEntity(Lnet/minecraft/client/gui/DrawContext;IIILorg/joml/Quaternionf;Lorg/joml/Quaternionf;Lnet/minecraft/entity/LivingEntity;)V", at = @At("TAIL"))
    private static void resetHeadRotation(DrawContext context, int x, int y, int size, Quaternionf quaternionf, Quaternionf quaternionf2, LivingEntity entity, CallbackInfo ci) {
        ILivingEntity accessor = (ILivingEntity) entity;

        accessor.tarasande_setHeadYaw(tarasande_headYaw);
        accessor.tarasande_setPrevHeadYaw(tarasande_prevHeadYaw);

        accessor.tarasande_setHeadPitch(tarasande_headPitch);
        accessor.tarasande_setPrevHeadPitch(tarasande_prevHeadPitch);
    }

}
