package de.florianmichael.clampclient.injection.mixin.protocolhack;

import de.florianmichael.clampclient.injection.instrumentation_1_12_2.model.ViaRaytraceResult;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.RaytraceBase;
import de.florianmichael.clampclient.injection.instrumentation_1_12_2.raytrace.RaytraceDefinition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.tarasandedevelopment.tarasande.event.EventUpdateTargetedEntity;
import net.tarasandedevelopment.tarasande.injection.accessor.IGameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.event.EventDispatcher;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    public void replaceRayTrace(float tickDelta, CallbackInfo ci) {
        final RaytraceBase raytraceBase = RaytraceDefinition.getClassWrapper();
        if (raytraceBase == null) return;

        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.PRE));

        ci.cancel();
        client.getProfiler().push("pick");
        Entity entity2 = this.client.getCameraEntity();
        if (entity2 == null)
            return;
        final IGameRenderer gameRendererAccessor = (IGameRenderer) MinecraftClient.getInstance().gameRenderer;

        final ViaRaytraceResult raytrace = raytraceBase.raytrace(entity2, entity2.prevYaw, entity2.prevPitch, entity2.getYaw(), entity2.getPitch(), 1.0F);
        client.targetedEntity = raytrace.pointed();
        if (!gameRendererAccessor.tarasande_isAllowThroughWalls()) {
            client.crosshairTarget = raytrace.target();
        }

        client.getProfiler().pop();
        EventDispatcher.INSTANCE.call(new EventUpdateTargetedEntity(EventUpdateTargetedEntity.State.POST));
    }
}
