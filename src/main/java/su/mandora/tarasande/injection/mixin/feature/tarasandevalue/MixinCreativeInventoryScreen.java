package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(CreativeInventoryScreen.class)
public class MixinCreativeInventoryScreen {

    @Redirect(method = "shouldShowOperatorTab", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isCreativeLevelTwoOp()Z"))
    public boolean forceCreativeInventory(PlayerEntity instance) {
        if (DebugValues.INSTANCE.getForceCreativeInventory().getValue())
            return instance.hasPermissionLevel(2);
        return instance.isCreativeLevelTwoOp();
    }

}
