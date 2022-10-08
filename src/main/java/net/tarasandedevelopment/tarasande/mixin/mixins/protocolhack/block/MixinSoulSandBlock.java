package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoulSandBlock.class)
public class MixinSoulSandBlock extends Block {

    public MixinSoulSandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);

        if (VersionList.isOlderOrEqualTo(VersionList.R1_14_4)) {
            final Vec3d velocity = entity.getVelocity();

            entity.setVelocity(velocity.getX() * 0.4D, velocity.getY(), velocity.getZ() * 0.4D);
        }
    }
}
