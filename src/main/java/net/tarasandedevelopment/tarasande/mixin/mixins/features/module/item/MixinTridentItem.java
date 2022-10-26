package net.tarasandedevelopment.tarasande.mixin.mixins.features.module.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.movement.ModuleTridentBoost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public class MixinTridentItem {

    @Unique
    private boolean tarasande_waterCheck() {
        return !TarasandeMain.Companion.get().getDisabled() && TarasandeMain.Companion.get().getManagerModule().get(ModuleTridentBoost.class).allowOutOfWater();
    }

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void hookTridentBoost(Args args) {
        if (!TarasandeMain.Companion.get().getDisabled()) {
            final double multiplier = TarasandeMain.Companion.get().getManagerModule().get(ModuleTridentBoost.class).multiplier();

            args.set(0, (double) args.get(0) + multiplier);
            args.set(1, (double) args.get(1) + multiplier);
            args.set(2, (double) args.get(2) + multiplier);
        }
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean hookTridentBoost_use(PlayerEntity instance) {
        return tarasande_waterCheck() || instance.isTouchingWaterOrRain();
    }

    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean hookTridentBoost_onStoppedUsing(PlayerEntity instance) {
        return tarasande_waterCheck() || instance.isTouchingWaterOrRain();
    }
}
