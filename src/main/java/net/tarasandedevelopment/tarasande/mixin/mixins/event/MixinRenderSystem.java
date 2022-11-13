package net.tarasandedevelopment.tarasande.mixin.mixins.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import su.mandora.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.events.EventFog;
import net.tarasandedevelopment.tarasande.events.EventScreenInput;
import net.tarasandedevelopment.tarasande.util.math.rotation.RotationUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderSystem.class, remap = false)
public class MixinRenderSystem {

    @Mutable
    @Shadow
    @Final
    private static float[] shaderFogColor;

    @Shadow
    private static float shaderFogStart;

    @Shadow
    private static float shaderFogEnd;

    @Redirect(method = "_setShaderFogStart", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogStart:F"))
    private static void hookEventFogStart(float value) {
        EventFog eventFog = new EventFog(EventFog.State.FOG_START, new float[]{value});
        EventDispatcher.INSTANCE.call(eventFog);
        shaderFogStart = eventFog.getValues()[0];
    }

    @Redirect(method = "_setShaderFogEnd", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogEnd:F"))
    private static void hookEventFogEnd(float value) {
        EventFog eventFog = new EventFog(EventFog.State.FOG_END, new float[]{value});
        EventDispatcher.INSTANCE.call(eventFog);
        shaderFogEnd = eventFog.getValues()[0];
    }

    @Inject(method = "_setShaderFogColor", at = @At("TAIL"))
    private static void hookEventFogColor(float f, float g, float h, float i, CallbackInfo ci) {
        EventFog eventFog = new EventFog(EventFog.State.FOG_COLOR, new float[]{f, g, h, i});
        EventDispatcher.INSTANCE.call(eventFog);
        shaderFogColor = eventFog.getValues();
    }

    @Inject(method = "flipFrame", at = @At("HEAD"))
    private static void hookEventScreenInputAndPollEvents(long window, CallbackInfo ci) {
        if (VersionList.isNewerOrEqualTo(ProtocolVersion.v1_13)) {
            EventScreenInput eventScreenInput = new EventScreenInput(false);
            EventDispatcher.INSTANCE.call(eventScreenInput);
        }

        RotationUtil.INSTANCE.updateFakeRotation(false);
    }
}
