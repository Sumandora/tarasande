package su.mandora.tarasande.mixin.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import su.mandora.tarasande.TarasandeMain;
import su.mandora.tarasande.event.EventBlockCollision;

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

}
