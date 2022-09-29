package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.exploit.ModuleCommandBlockBypass;
import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.TarasandeMain;

@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean redirectOnUse(PlayerEntity instance) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleCommandBlockBypass.class).getEnabled())
            return true;

        return instance.isCreativeLevelTwoOp();
    }
}
