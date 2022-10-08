package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.block;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public class MixinBedBlock {

    @Unique
    private final static VoxelShape BED_SHAPE_1132 = Block.createCuboidShape(0, 0, 0, 16, 9, 16);

    @Inject(method = "bounceEntity", at = @At("HEAD"), cancellable = true)
    public void injectBounceEntity(Entity entity, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_11_1))
            ci.cancel();
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void injectGetOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_13_2))
            cir.setReturnValue(BED_SHAPE_1132);
    }
}
