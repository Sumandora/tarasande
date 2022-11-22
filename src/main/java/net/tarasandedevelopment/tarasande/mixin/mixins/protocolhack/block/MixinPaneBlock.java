/**
 * --FLORIAN MICHAEL PRIVATE LICENCE v1.2--
 *
 * This file / project is protected and is the intellectual property of Florian Michael (aka. EnZaXD),
 * any use (be it private or public, be it copying or using for own use, be it publishing or modifying) of this
 * file / project is prohibited. It requires in that use a written permission with official signature of the owner
 * "Florian Michael". "Florian Michael" receives the right to control and manage this file / project. This right is not
 * cancelled by copying or removing the license and in case of violation a criminal consequence is to be expected.
 * The owner "Florian Michael" is free to change this license. The creator assumes no responsibility for any infringements
 * that have arisen, are arising or will arise from this project / file. If this licence is used anywhere,
 * the latest version published by the author Florian Michael (aka EnZaXD) always applies automatically.
 *
 * Changelog:
 *     v1.0:
 *         Added License
 *     v1.1:
 *         Ownership withdrawn
 *     v1.2:
 *         Version-independent validity and automatic renewal
 */

package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PaneBlock.class)
public class MixinPaneBlock extends HorizontalConnectingBlock {

    public MixinPaneBlock(float radius1, float radius2, float boundingHeight1, float boundingHeight2, float collisionHeight, Settings settings) {
        super(radius1, radius2, boundingHeight1, boundingHeight2, collisionHeight, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8))
            return correctShape(world, state);

        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8))
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
