package su.mandora.tarasande.injection.mixin.event;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventTickRate;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {

    @Shadow public float tickTime;
    @Unique
    private float tarasande_baseTPS;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void recordTickRate(float tps, long timeMillis, FloatUnaryOperator targetMillisPerTick, CallbackInfo ci) {
        tarasande_baseTPS = tps;
    }

    @Inject(method = "beginRenderTick", at = @At("HEAD"))
    public void hookEventTimer(long timeMillis, CallbackInfoReturnable<Integer> cir) {
        EventTickRate eventTickRate = new EventTickRate(tarasande_baseTPS);
        EventDispatcher.INSTANCE.call(eventTickRate);
        tickTime = (long) (1000F / eventTickRate.getTickRate());
    }

}
