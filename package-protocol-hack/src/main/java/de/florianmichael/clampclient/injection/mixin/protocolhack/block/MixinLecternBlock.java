package de.florianmichael.clampclient.injection.mixin.protocolhack.block;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.LecternBlock;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LecternBlock.class)
public class MixinLecternBlock {

    @Shadow @Final public static VoxelShape BASE_SHAPE;
    @Shadow @Final public static VoxelShape EAST_SHAPE;
    @Shadow @Final public static VoxelShape SOUTH_SHAPE;
    @Unique
    public final VoxelShape protocolhack_EAST_SHAPE_1_17 = VoxelShapes.union(
            Block.createCuboidShape(15.0, 10.0, 0.0, 10.666667, 14.0, 16.0),
            Block.createCuboidShape(10.666667, 12.0, 0.0, 6.333333, 16.0, 16.0),
            Block.createCuboidShape(6.333333, 14.0, 0.0, 2.0, 18.0, 16.0),
            BASE_SHAPE
    );

    @Unique
    public final VoxelShape protocolhack_SOUTH_SHAPE_1_17 = VoxelShapes.union(
            Block.createCuboidShape(0.0, 10.0, 15.0, 16.0, 14.0, 10.666667),
            Block.createCuboidShape(0.0, 12.0, 10.666667, 16.0, 16.0, 6.333333),
            Block.createCuboidShape(0.0, 14.0, 6.333333, 16.0, 18.0, 2.0),
            BASE_SHAPE
    );

    @Redirect(method = "getOutlineShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/LecternBlock;EAST_SHAPE:Lnet/minecraft/util/shape/VoxelShape;"))
    public VoxelShape changeEastShape() {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_17)) {
            return protocolhack_EAST_SHAPE_1_17;
        }
        return EAST_SHAPE;
    }

    @Redirect(method = "getOutlineShape", at = @At(value = "FIELD", target = "Lnet/minecraft/block/LecternBlock;SOUTH_SHAPE:Lnet/minecraft/util/shape/VoxelShape;"))
    public VoxelShape changeSouthShape() {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_17)) {
            return protocolhack_SOUTH_SHAPE_1_17;
        }
        return SOUTH_SHAPE;
    }
}
