package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.Raytrace_1_8to1_12_2;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.model.ViaRaytraceResult;
import de.florianmichael.tarasande_protocol_hack.util.values.ProtocolHackValues;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.tarasandedevelopment.tarasande.event.EventUpdateTargetedEntity;
import net.tarasandedevelopment.tarasande.feature.rotation.Rotations;
import net.tarasandedevelopment.tarasande.util.math.rotation.Rotation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

@SuppressWarnings("ShadowModifiers")
@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    public void replaceRayTrace(float tickDelta, CallbackInfo ci) {
        if (!ProtocolHackValues.INSTANCE.getReplaceRayTrace().getValue()) return;

        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));
        ci.cancel();
        client.getProfiler().push("pick");
        final Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null) return;

        final Rotation fakeRotation = Rotations.INSTANCE.getFakeRotation();

        float prevYaw = entity2.prevYaw;
        float prevPitch = entity2.prevPitch;

        float yaw = entity2.getYaw();
        float pitch = entity2.getPitch();

        if(fakeRotation != null) {
            prevYaw = fakeRotation.getYaw();
            prevPitch = fakeRotation.getPitch();

            yaw = fakeRotation.getYaw();
            pitch = fakeRotation.getPitch();
        }

        final ViaRaytraceResult raytrace;
        if(ProtocolHackValues.INSTANCE.getBruteforceRaytraceFastMathTables().anySelected())
            raytrace = Raytrace_1_8to1_12_2.CLASS_WRAPPER.bruteforceRaytrace(entity2, prevYaw, prevPitch, yaw, pitch, tickDelta);
        else
            raytrace = Raytrace_1_8to1_12_2.CLASS_WRAPPER.raytrace(entity2, prevYaw, prevPitch, yaw, pitch, tickDelta);
        client.crosshairTarget = raytrace.target();
        client.targetedEntity = raytrace.pointed();

        client.getProfiler().pop();
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }
}
