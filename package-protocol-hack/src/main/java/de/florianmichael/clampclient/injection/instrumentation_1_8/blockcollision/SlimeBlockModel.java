package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SlimeBlockModel extends StoneBlockModel {

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        if (Math.abs(entityIn.getVelocity().y) < 0.1D && !entityIn.isSneaking()) {
            final double d0 = 0.4D + Math.abs(entityIn.getVelocity().y) * 0.2D;

            entityIn.getVelocity().x *= d0;
            entityIn.getVelocity().z *= d0;
        }
    }


    @Override
    public void onLanded(World worldIn, Entity entityIn) {
        if (entityIn.isSneaking()) {
            entityIn.getVelocity().y = 0;
        } else if (entityIn.getVelocity().y < 0.0D) {
            entityIn.getVelocity().y = -entityIn.getVelocity().y;
        }
    }
}
