package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module;

import kotlin.Triple;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleBlockChangeTracker;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.render.ModuleWorldTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class MixinWorld implements WorldAccess {

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void hookBlockChangeTracker(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        final ModuleBlockChangeTracker moduleBlockChangeTracker = TarasandeMain.Companion.managerModule().get(ModuleBlockChangeTracker.class);

        if (moduleBlockChangeTracker.getEnabled())
            if (!getBlockState(pos).getBlock().equals(state.getBlock()))
                moduleBlockChangeTracker.getChanges().add(new Triple<>(pos, state, System.currentTimeMillis()));
    }

    @Override
    public int getMoonPhase() {
        final int moonPhase = TarasandeMain.Companion.managerModule().get(ModuleWorldTime.class).moonPhase();
        if (moonPhase != 1) {
            return moonPhase;
        }

        return WorldAccess.super.getMoonPhase();
    }
}