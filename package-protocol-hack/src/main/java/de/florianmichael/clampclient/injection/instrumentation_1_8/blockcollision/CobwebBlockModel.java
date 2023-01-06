package de.florianmichael.clampclient.injection.instrumentation_1_8.blockcollision;

import de.florianmichael.clampclient.injection.mixininterface.IEntity_Protocol;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CobwebBlockModel extends BlockModel {

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        ((IEntity_Protocol) entityIn).protocolhack_setInWeb(true);
    }
}
