package net.tarasandedevelopment.tarasande.mixin.mixins;

import kotlin.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.mixin.accessor.IWorld;
import net.tarasandedevelopment.tarasande.module.render.ModuleBlockChangeTracker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld implements IWorld {
    @Mutable
    @Shadow
    @Final
    public boolean isClient;

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void injectHandleBlockUpdate(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        if(!TarasandeMain.Companion.get().getDisabled()) {
            ModuleBlockChangeTracker moduleBlockChangeTracker = TarasandeMain.Companion.get().getManagerModule().get(ModuleBlockChangeTracker.class);
            if(moduleBlockChangeTracker.getEnabled())
                if(!getBlockState(pos).getBlock().equals(state.getBlock()))
                    moduleBlockChangeTracker.getHashMap().put(new Pair<>(pos, state), System.currentTimeMillis());
        }
    }

    @Override
    public void tarasande_setIsClient(boolean isClient) {
        this.isClient = isClient;
    }
}
