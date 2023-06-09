package su.mandora.tarasande.injection.mixin.feature.module;

import kotlin.Triple;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;
import su.mandora.tarasande.feature.tarasandevalue.impl.debug.BlockChangeTracker;

@Mixin(World.class)
public abstract class MixinWorld implements WorldAccess {

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void hookBlockChangeTracker(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        BlockChangeTracker blockChangeTracker = ((BlockChangeTracker) DebugValues.INSTANCE.getBlockChangeTracker().getValuesOwner());
        if (blockChangeTracker.getEnabled().getValue())
            if (!getBlockState(pos).getBlock().equals(state.getBlock()))
                blockChangeTracker.getChanges().add(new Triple<>(pos, state, System.currentTimeMillis()));
    }
}
