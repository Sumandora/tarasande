package su.mandora.tarasande.mixin.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventPollEvents;
import su.mandora.tarasande.mixin.accessor.ILivingEntity;
import su.mandora.tarasande.util.math.rotation.Rotation;
import su.mandora.tarasande.util.math.rotation.RotationUtil;
import su.mandora.tarasande.util.render.RenderUtil;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(RenderSystem.class)
public class MixinRenderSystem {

    private static double lastMinRotateToOriginSpeed = 1.0;
    private static double lastMaxRotateToOriginSpeed = 1.0;

    @Inject(method = "flipFrame", at = @At("HEAD"), remap = false)
    private static void injectFlipFrame(long window, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().interactionManager != null) {
            EventPollEvents eventPollEvents = new EventPollEvents(new Rotation(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch()));
            TarasandeMain.Companion.get().getManagerEvent().call(eventPollEvents);
            if (eventPollEvents.getDirty()) {
                RotationUtil.INSTANCE.setFakeRotation(eventPollEvents.getRotation());
                lastMinRotateToOriginSpeed = eventPollEvents.getMinRotateToOriginSpeed();
                lastMaxRotateToOriginSpeed = eventPollEvents.getMaxRotateToOriginSpeed();
            } else if (RotationUtil.INSTANCE.getFakeRotation() != null) {
                Rotation realRotation = new Rotation(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch());
                Rotation rotation = new Rotation(RotationUtil.INSTANCE.getFakeRotation())
                        .smoothedTurn(
                                realRotation,
                                ((ILivingEntity) MinecraftClient.getInstance().player).getBodyTrackingIncrements() > 0 ? 1.0 :
                                        (lastMinRotateToOriginSpeed == 1.0 && lastMaxRotateToOriginSpeed == 1.0) ?
                                                1.0 : MathHelper.clamp(
                                                (
                                                        (lastMinRotateToOriginSpeed == lastMaxRotateToOriginSpeed) ?
                                                                lastMinRotateToOriginSpeed :
                                                                ThreadLocalRandom.current().nextDouble(Math.min(lastMinRotateToOriginSpeed, lastMaxRotateToOriginSpeed), Math.max(lastMinRotateToOriginSpeed, lastMaxRotateToOriginSpeed))
                                                ) * RenderUtil.INSTANCE.getDeltaTime() * 0.05,
                                                0.0,
                                                1.0
                                        )
                        );
                rotation.correctSensitivity();
                float actualDist = RotationUtil.INSTANCE.getFakeRotation().fov(rotation);
                double gcd = Math.pow(MinecraftClient.getInstance().options.mouseSensitivity * 0.6000000238418579 + 0.20000000298023224, 3.0) * 8.0 * 0.15;
                if (actualDist <= gcd / 2 + 0.1 /* little more */) {
                    Rotation rotation2 = new Rotation(MinecraftClient.getInstance().player.getYaw(), MinecraftClient.getInstance().player.getPitch());
                    rotation2.correctSensitivity();
                    RotationUtil.INSTANCE.setFakeRotation(null);
                    MinecraftClient.getInstance().player.setYaw(MinecraftClient.getInstance().player.prevYaw = MinecraftClient.getInstance().player.lastRenderYaw = MinecraftClient.getInstance().player.renderYaw = rotation2.getYaw());
                    MinecraftClient.getInstance().player.setPitch(rotation2.getPitch());
                } else {
                    RotationUtil.INSTANCE.setFakeRotation(rotation);
                }
            }
        }
    }
}
