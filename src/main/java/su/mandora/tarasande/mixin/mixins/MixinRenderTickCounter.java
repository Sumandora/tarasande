package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventTimer;
import su.mandora.tarasande.module.player.ModuleTimer;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {

    @Shadow
    public float lastFrameDuration;

    @Inject(method = "beginRenderTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;lastFrameDuration:F", ordinal = 1, shift = At.Shift.BEFORE))
    private void injectBeginRenderTick(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        ModuleTimer moduleTimer = TarasandeMain.Companion.get().getManagerModule().get(ModuleTimer.class);
        if (moduleTimer.getEnabled()) {
            EventTimer eventTimer = new EventTimer(lastFrameDuration);
            TarasandeMain.Companion.get().getManagerEvent().call(eventTimer);
            lastFrameDuration = eventTimer.getLastFrameDuration();
        }
    }

}
