package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import de.florianmichael.clampclient.injection.mixininterface.ILivingEntity_Protocol;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

// This Class represents a 1.8 Block
public class BlockModel {

    public void addCollisionBoxesToList(final World world, final BlockPos pos, final BlockState state, final Box mask, final List<Box> boundingBoxList, final Entity collidingEntity) {}
    public void setBlockBounds(final float minX, final float minY, final float minZ, final float maxX, final float maxY, final float maxZ) {}
    public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
        return null;
    }
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {}
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        ((ILivingEntity_Protocol) entityIn).protocolhack_getPlayerLivingEntityMovementWrapper().fall(fallDistance, 1.0F);
    }
    public void onLanded(World worldIn, Entity entityIn) {
        entityIn.getVelocity().y = 0.0D;
    }
}
