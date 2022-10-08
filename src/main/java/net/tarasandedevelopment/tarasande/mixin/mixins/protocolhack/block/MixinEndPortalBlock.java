package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndPortalBlock.class)
public class MixinEndPortalBlock {

    @Unique
    private final VoxelShape SHAPE_1_8 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    @Unique
    private final VoxelShape SHAPE_1_16_5 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (MinecraftClient.getInstance().world == null) return;

        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            cir.setReturnValue(SHAPE_1_8);
        else if (VersionList.isOlderOrEqualTo(VersionList.R1_16_5))
            cir.setReturnValue(SHAPE_1_16_5);
    }
}
