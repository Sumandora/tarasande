package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LiquidBlockModel extends BlockModel {

    protected int getLevel(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock() == Blocks.WATER ? ((Integer)worldIn.getBlockState(pos).get(FluidBlock.LEVEL)).intValue() : -1;
    }

    protected int getEffectiveFlowDecay(World worldIn, BlockPos pos) {
        int i = this.getLevel(worldIn, pos);
        return i >= 8 ? 0 : i;
    }

    public boolean isBlockSolid(World worldIn, BlockPos pos, Direction side) {
        Block material = worldIn.getBlockState(pos).getBlock();
        return material != Blocks.WATER && (side == Direction.UP || (material != Blocks.ICE && super.isBlockSolid(worldIn, pos, side)));
    }

    protected Vec3d getFlowVector(World worldIn, BlockPos pos) {
        Vec3d vec3 = new Vec3d(0.0D, 0.0D, 0.0D);
        int i = this.getEffectiveFlowDecay(worldIn, pos);

        for (Direction enumfacing : Direction.HORIZONTAL) {
            BlockPos blockpos = pos.offset(enumfacing);
            int j = this.getEffectiveFlowDecay(worldIn, blockpos);

            if (j < 0) {
                if (!worldIn.getBlockState(blockpos).getBlock().getDefaultState().getMaterial().blocksMovement()) {
                    j = this.getEffectiveFlowDecay(worldIn, blockpos.down());

                    if (j >= 0) {
                        int k = j - (i - 8);
                        vec3 = vec3.add((double)((blockpos.getX() - pos.getX()) * k), (double)((blockpos.getY() - pos.getY()) * k), (double)((blockpos.getZ() - pos.getZ()) * k));
                    }
                }
            } else if (j >= 0) {
                int l = j - i;
                vec3 = vec3.add((double)((blockpos.getX() - pos.getX()) * l), (double)((blockpos.getY() - pos.getY()) * l), (double)((blockpos.getZ() - pos.getZ()) * l));
            }
        }

        if (((Integer)worldIn.getBlockState(pos).get(FluidBlock.LEVEL)).intValue() >= 8) {
            for (Direction enumfacing1 : Direction.HORIZONTAL) {
                BlockPos blockpos1 = pos.offset(enumfacing1);

                if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1)) {
                    vec3 = vec3.normalize().add(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    @Override
    public Vec3d modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3d motion) {
        return motion.add(getFlowVector(worldIn, pos));
    }
}
