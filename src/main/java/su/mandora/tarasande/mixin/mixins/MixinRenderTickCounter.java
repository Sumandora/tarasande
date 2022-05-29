package su.mandora.tarasande.mixin.mixins;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.IRenderTickCounter;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter implements IRenderTickCounter {

    @Mutable
    @Shadow
    @Final
    private float tickTime;

    @Shadow
    private long prevTimeMillis;

    @Override
    public float getTickTime() {
        return tickTime;
    }

    @Override
    public void setTickTime(float tickTime) {
        this.tickTime = tickTime;
    }

    @Override
    public float getPrevTimeMillis() {
        return prevTimeMillis;
    }
}
