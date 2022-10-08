package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnvilBlock.class)
public class MixinAnvilBlock {

    @Shadow @Final public static DirectionProperty FACING;

    @Unique
    private static final VoxelShape X_AXIS_SHAPE_112 = Block.createCuboidShape(0, 0, 2, 16, 16, 14);

    @Unique
    private static final VoxelShape Z_AXIS_SHAPE_112 = Block.createCuboidShape(2, 0, 0, 14, 16, 16);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        Direction direction = state.get(FACING);
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            cir.setReturnValue(direction.getAxis() == Direction.Axis.X ? X_AXIS_SHAPE_112 : Z_AXIS_SHAPE_112);
    }
}