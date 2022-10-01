package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.mixin.accessor.IWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventBlockChange;

@Mixin(World.class)
public class MixinWorld implements IWorld {
    @Mutable
    @Shadow
    @Final
    public boolean isClient;

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"))
    public void injectHandleBlockUpdate(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
        TarasandeMain.Companion.get().getManagerEvent().call(new EventBlockChange(pos, state));
    }

    @Override
    public void tarasande_setIsClient(boolean isClient) {
        this.isClient = isClient;
    }
}
