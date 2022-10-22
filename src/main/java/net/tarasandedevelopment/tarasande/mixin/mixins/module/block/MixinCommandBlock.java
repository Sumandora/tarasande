package net.tarasandedevelopment.tarasande.mixin.mixins.module.block;

import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.render.ModuleCommandBlockBypass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean hookCommandBlockBypass(PlayerEntity instance) {
        if (!TarasandeMain.Companion.get().getDisabled())
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleCommandBlockBypass.class).getEnabled())
                return true;
        return instance.isCreativeLevelTwoOp();
    }
}
