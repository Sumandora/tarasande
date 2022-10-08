package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.InfestedBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockSubAbstractBlockState {

    @Shadow protected abstract BlockState asBlockState();

    @Inject(method = "getHardness", at = @At("RETURN"), cancellable = true)
    public void injectGetHardness(BlockView world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        final BlockState state = this.asBlockState();

        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            if (state.getBlock() instanceof InfestedBlock)
                cir.setReturnValue(0.75F);


        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_4))
            if (state.getBlock() == Blocks.END_STONE_BRICKS ||
                    state.getBlock() == Blocks.END_STONE_BRICK_SLAB ||
                    state.getBlock() == Blocks.END_STONE_BRICK_STAIRS ||
                    state.getBlock() == Blocks.END_STONE_BRICK_WALL)
                cir.setReturnValue(0.8F);


        if (VersionList.isOlderOrEqualTo(VersionList.R1_15_2))
            if (state.getBlock() == Blocks.PISTON || state.getBlock() == Blocks.STICKY_PISTON || state.getBlock() == Blocks.PISTON_HEAD)
                cir.setReturnValue(0.5F);

        if (VersionList.isOlderOrEqualTo(VersionList.R1_16_5))
            if (state.getBlock() instanceof InfestedBlock)
                cir.setReturnValue(0F);
    }
}
