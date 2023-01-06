package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class StoneBlockModel extends BlockModel {

    private double minX = 0;
    private double minY = 0;
    private double minZ = 0;
    private double maxX = 1;
    private double maxY = 1;
    private double maxZ = 1;

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, BlockState state, Box mask, List<Box> boundingBoxList, Entity collidingEntity) {
        final Box axisalignedbb = this.getCollisionBoundingBox(world, pos, state);

        System.out.println(axisalignedbb + " " + this);

        if (axisalignedbb != null && mask.intersects(axisalignedbb)) boundingBoxList.add(axisalignedbb);
    }

    @Override
    public Box getCollisionBoundingBox(World worldIn, BlockPos pos, BlockState state) {
        return new Box((double)pos.getX() + this.minX, (double)pos.getY() + this.minY, (double)pos.getZ() + this.minZ, (double)pos.getX() + this.maxX, (double)pos.getY() + this.maxY, (double)pos.getZ() + this.maxZ);
    }

    @Override
    public void setBlockBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = (double)minX;
        this.minY = (double)minY;
        this.minZ = (double)minZ;
        this.maxX = (double)maxX;
        this.maxY = (double)maxY;
        this.maxZ = (double)maxZ;
    }
}
