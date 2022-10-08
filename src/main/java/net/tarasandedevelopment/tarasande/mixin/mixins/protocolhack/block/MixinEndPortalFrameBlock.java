package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.EndPortalFrameBlock;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndPortalFrameBlock.class)
public class MixinEndPortalFrameBlock {

    @Unique
    private final VoxelShape EYE_SHAPE_1_12_2 = Block.createCuboidShape(5.0, 13.0, 5.0, 11.0, 16.0, 11.0);

    @Shadow @Final protected static VoxelShape FRAME_SHAPE;
    @Shadow @Final protected static VoxelShape FRAME_WITH_EYE_SHAPE;

    @Redirect(method = "getOutlineShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/EndPortalFrameBlock;FRAME_WITH_EYE_SHAPE:Lnet/minecraft/util/shape/VoxelShape;"))
    public VoxelShape redirectGetOutlineShape() {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_12_2))
            return VoxelShapes.union(FRAME_SHAPE, EYE_SHAPE_1_12_2);

        return FRAME_WITH_EYE_SHAPE;
    }
}