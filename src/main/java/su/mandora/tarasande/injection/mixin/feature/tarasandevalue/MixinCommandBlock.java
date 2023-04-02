package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean hookCommandBlockBypass(PlayerEntity instance) {
        if (DebugValues.INSTANCE.getAlwaysAllowToOpenCommandBlocks().getValue()) return true;
        return instance.isCreativeLevelTwoOp();
    }
}
