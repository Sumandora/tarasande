package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean hookCommandBlockBypass(PlayerEntity instance) {
        if (DebugValues.INSTANCE.getAlwaysAllowToOpenCommandBlocks().getValue()) return true;
        return instance.isCreativeLevelTwoOp();
    }
}
