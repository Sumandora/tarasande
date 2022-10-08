package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PaneBlock.class)
public abstract class MixinPaneBlock extends HorizontalConnectingBlock {

    public MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            return correctShape(world, state);

        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            return correctShape(world, state);

        return super.getCollisionShape(state, world, pos, context);
    }

    // Original Code from 1.8
    public VoxelShape correctShape(BlockView worldIn, BlockState state) {
        VoxelShape toReturn = VoxelShapes.empty();

        boolean flag = state.get(NORTH);
        boolean flag1 = state.get(SOUTH);
        boolean flag2 = state.get(WEST);
        boolean flag3 = state.get(EAST);

        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2)
                toReturn = Block.createCuboidShape(0, 0, 7, 8, 16, 9);
            else if (flag3)
                toReturn = Block.createCuboidShape(8, 0, 7, 16, 16, 9);
        } else
            toReturn = Block.createCuboidShape(0, 0, 7, 16, 16, 9);

        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag)
                toReturn = Block.createCuboidShape(7, 0, 0, 9, 16, 8);
            else if (flag1)
                toReturn = Block.createCuboidShape(7, 0, 8, 9, 16, 16);
        } else
            toReturn = Block.createCuboidShape(7, 0, 0, 9, 16, 16);

        return toReturn;
    }
}
