package su.mandora.tarasande.injection.mixin.event;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.*;
import su.mandora.tarasande.feature.tarasandevalue.TarasandeValues;

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
        EventFogStart eventFogStart = new EventFogStart(value);
        EventDispatcher.INSTANCE.call(eventFogStart);
        shaderFogStart = eventFogStart.getDistance();
    }

    @Redirect(method = "_setShaderFogEnd", at = @At(value = "FIELD", target = "Lcom/mojang/blaze3d/systems/RenderSystem;shaderFogEnd:F"))
    private static void hookEventFogEnd(float value) {
        EventFogEnd eventFogEnd = new EventFogEnd(value);
        EventDispatcher.INSTANCE.call(eventFogEnd);
        shaderFogEnd = eventFogEnd.getDistance();
    }

    @Inject(method = "_setShaderFogColor", at = @At("TAIL"))
    private static void hookEventFogColor(float f, float g, float h, float i, CallbackInfo ci) {
        EventFogColor eventFogColor = new EventFogColor(new float[]{f, g, h, i});
        EventDispatcher.INSTANCE.call(eventFogColor);
        shaderFogColor = eventFogColor.getColor();
    }

    @Inject(method = "flipFrame", at = @At("HEAD"))
    private static void hookEventScreenInputAndPollEvents(long window, CallbackInfo ci) {
        if (!TarasandeValues.INSTANCE.getExecuteScreenInputsInTicks().getValue()) // Counterpart in MixinMinecraftClient
            EventDispatcher.INSTANCE.call(new EventScreenInput(false));

        EventDispatcher.INSTANCE.call(new EventPollEvents());
    }

}
