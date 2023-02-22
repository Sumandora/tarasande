package su.mandora.tarasande_mod_fixes.injection.mixin.sodium;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventRenderBlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Sodium compatibility for block esp (I hate sodium)
@Pseudo
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.WorldSlice")
public class MixinWorldSlice {

    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "getBlockState(III)Lnet/minecraft/block/BlockState;", at = @At("RETURN"), cancellable = true)
    public void hookEventRenderBlockModel(int x, int y, int z, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        EventRenderBlockModel eventRenderBlockModel = new EventRenderBlockModel(state, new BlockPos(x, y, z));
        EventDispatcher.INSTANCE.call(eventRenderBlockModel);
        if (eventRenderBlockModel.getCancelled())
            cir.setReturnValue(Blocks.AIR.getDefaultState());
    }

}
