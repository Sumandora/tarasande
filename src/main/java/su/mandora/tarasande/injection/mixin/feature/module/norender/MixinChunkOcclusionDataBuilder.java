package su.mandora.tarasande.injection.mixin.feature.module.norender;

import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventChunkOcclusion;

@Mixin(ChunkOcclusionDataBuilder.class)
public class MixinChunkOcclusionDataBuilder {

    @Inject(method = "markClosed", at = @At("HEAD"), cancellable = true)
    public void noRender_MarkClosed(BlockPos pos, CallbackInfo ci) {
        final EventChunkOcclusion eventChunkOcclusion = new EventChunkOcclusion();
        EventDispatcher.INSTANCE.call(eventChunkOcclusion);

        if (eventChunkOcclusion.getCancelled()) {
            ci.cancel();
        }
    }
}
