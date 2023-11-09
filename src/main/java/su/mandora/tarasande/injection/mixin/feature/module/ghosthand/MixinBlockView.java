package su.mandora.tarasande.injection.mixin.feature.module.ghosthand;

import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.player.ModuleGhostHand;
import su.mandora.tarasande.util.extension.minecraft.HitResultKt;

@Mixin(BlockView.class)
public interface MixinBlockView {

    @Shadow
    BlockState getBlockState(BlockPos var1);

    @SuppressWarnings("ShadowModifiers") // Interfaces can't have protected functions
    @Shadow
    BlockHitResult method_17743(RaycastContext par1, BlockPos par2);

    @Shadow
    private static BlockHitResult method_17746(RaycastContext par1) {
        return null;
    }

    @Inject(method = "raycast(Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;", at = @At("HEAD"), cancellable = true)
    default void hookGhostHand(RaycastContext context, CallbackInfoReturnable<BlockHitResult> cir) {
        ModuleGhostHand moduleGhostHand = ManagerModule.INSTANCE.get(ModuleGhostHand.class);
        if(moduleGhostHand.getEnabled().getValue() && moduleGhostHand.getMode().isSelected(0)) {
            BlockHitResult hitResult = BlockView.raycast(context.getStart(), context.getEnd(), context, (innerContext, pos) -> {
                if(!moduleGhostHand.getBlocks().isSelected(getBlockState(pos).getBlock()))
                    return null;
                return method_17743(innerContext, pos);
            }, MixinBlockView::method_17746);
            if(HitResultKt.isBlockHitResult(hitResult))
                cir.setReturnValue(hitResult);
        }
    }

}
