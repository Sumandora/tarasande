package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.block.CommandBlock;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventCommandBlockUsage;

@Mixin(CommandBlock.class)
public class MixinCommandBlock {

    @Redirect(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean modifyIsCreativeLevelTwoOp(PlayerEntity instance) {
        final EventCommandBlockUsage eventCommandBlockUsage = new EventCommandBlockUsage(instance.isCreativeLevelTwoOp());
        TarasandeMain.Companion.get().getManagerEvent().call(eventCommandBlockUsage);
        return eventCommandBlockUsage.getAllowed();
    }
}
