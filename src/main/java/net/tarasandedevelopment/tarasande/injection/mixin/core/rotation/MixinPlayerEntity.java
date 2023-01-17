package net.tarasandedevelopment.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.injection.accessor.ILivingEntity;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Redirect(method = "tickNewAi", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;headYaw:F"))
    public void overwriteYaw(PlayerEntity instance, float value) {
        Rotation rotation = RotationUtil.INSTANCE.getFakeRotation();
        //noinspection ConstantValue
        if((Object) this == MinecraftClient.getInstance().player && rotation != null) {
            value =  rotation.getYaw();
            ((ILivingEntity) instance).tarasande_setHeadPitch(rotation.getPitch());
        }
        instance.headYaw = value;
    }

}
