package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventBlockCollision;
import net.tarasandedevelopment.tarasande.event.EventCollisionShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {

    @Shadow
    protected abstract BlockState asBlockState();

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void injectOnEntityCollision(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        EventBlockCollision eventBlockCollision = new EventBlockCollision(this.asBlockState(), pos, entity);
        TarasandeMain.Companion.get().getManagerEvent().call(eventBlockCollision);
        if (eventBlockCollision.getCancelled())
            ci.cancel();
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"), cancellable = true)
    public void injectGetCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        EventCollisionShape eventCollisionShape = new EventCollisionShape(pos, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventCollisionShape);
        cir.setReturnValue(eventCollisionShape.getCollisionShape());
    }

}
