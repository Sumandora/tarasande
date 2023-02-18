package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class MixinCamera {

    @Shadow
    private float cameraY;

    @Shadow
    private float lastCameraY;

    @Shadow
    private Entity focusedEntity;

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.BEFORE))
    public void onUpdateHeight(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getReplaceSneaking().getValue() && ProtocolHackValues.INSTANCE.getSneakInstant().getValue()) {
            cameraY = lastCameraY = focusedEntity.getStandingEyeHeight();
        }
    }

    @Inject(method = "updateEyeHeight", at = @At(value = "HEAD"), cancellable = true)
    public void onUpdateEyeHeight(CallbackInfo ci) {
        if (this.focusedEntity == null) return;

        if (ProtocolHackValues.INSTANCE.getReplaceSneaking().getValue()) {
            ci.cancel();
            this.lastCameraY = this.cameraY;

            if (this.focusedEntity instanceof PlayerEntity player && !player.isSleeping()) {
                final boolean longSneaking = !ProtocolHackValues.INSTANCE.getReplaceSneaking().getValue() || ProtocolHackValues.INSTANCE.getAdjustLongSneaking().getValue();

                if (player.isSneaking()) {
                    cameraY = 1.54F;
                } else if (!longSneaking) {
                    cameraY = 1.62F;
                } else if (cameraY < 1.62F) {
                    float delta = 1.62F - cameraY;
                    delta *= 0.4;
                    cameraY = 1.62F - delta;
                }
            } else {
                cameraY = focusedEntity.getStandingEyeHeight();
            }
        }
    }
}
