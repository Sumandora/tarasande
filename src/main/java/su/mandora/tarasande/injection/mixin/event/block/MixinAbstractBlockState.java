package su.mandora.tarasande.injection.mixin.event.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.event.EventDispatcher;
import su.mandora.tarasande.event.impl.EventBlockCollision;
import su.mandora.tarasande.event.impl.EventCollisionShape;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class MixinAbstractBlockState {

    @Shadow
    protected abstract BlockState asBlockState();

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    public void hookEventBlockCollision(World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        EventBlockCollision eventBlockCollision = new EventBlockCollision(this.asBlockState(), pos, entity);
        EventDispatcher.INSTANCE.call(eventBlockCollision);
        if (eventBlockCollision.getCancelled())
            ci.cancel();
    }

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"), cancellable = true)
    public void hookEventCollisionShape(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (pos != null && cir.getReturnValue() != null) {
            EventCollisionShape eventCollisionShape = new EventCollisionShape(pos, cir.getReturnValue());
            EventDispatcher.INSTANCE.call(eventCollisionShape);
            cir.setReturnValue(eventCollisionShape.getCollisionShape());
        }
    }

}
