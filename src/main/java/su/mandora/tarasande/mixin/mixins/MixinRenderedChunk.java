package su.mandora.tarasande.mixin.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventRenderBlockModel;

@Mixin(targets = "net.minecraft.client.render.chunk.RenderedChunk")
public class MixinRenderedChunk {

    @Inject(method = "getBlockState", at = @At("RETURN"), cancellable = true)
    public void injectGetBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        EventRenderBlockModel eventRenderBlockModel = new EventRenderBlockModel(state, pos);
        TarasandeMain.Companion.get().getManagerEvent().call(eventRenderBlockModel);
        if (eventRenderBlockModel.getCancelled())
            cir.setReturnValue(Blocks.AIR.getDefaultState());
    }

}
