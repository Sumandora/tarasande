package su.mandora.tarasande.injection.mixin.core.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.feature.rotation.Rotations;
import su.mandora.tarasande.injection.accessor.ILivingEntity;
import su.mandora.tarasande.feature.rotation.api.Rotation;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity {

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickNewAi", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;headYaw:F"))
    public void updateHeadRotation(CallbackInfo ci) {
        Rotation rotation = Rotations.INSTANCE.getFakeRotation();
        float yaw = getYaw();
        float pitch = getPitch();
        //noinspection ConstantValue
        if ((Object) this == MinecraftClient.getInstance().player && rotation != null) {
            yaw = rotation.getYaw();
            pitch = rotation.getPitch();
        }
        ((ILivingEntity) this).tarasande_setHeadYaw(yaw);
        ((ILivingEntity) this).tarasande_setHeadPitch(pitch);
    }

}
