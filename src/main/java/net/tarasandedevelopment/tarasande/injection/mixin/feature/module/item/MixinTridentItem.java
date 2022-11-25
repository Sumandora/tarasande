package net.tarasandedevelopment.tarasande.injection.mixin.feature.module.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleTridentBoost;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(TridentItem.class)
public class MixinTridentItem {

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void hookTridentBoost(Args args) {
        ModuleTridentBoost moduleTridentBoost = TarasandeMain.Companion.managerModule().get(ModuleTridentBoost.class);
        if (moduleTridentBoost.getEnabled()) {
            double multiplier = moduleTridentBoost.getMultiplier().getValue();

            args.set(0, (double) args.get(0) * multiplier);
            args.set(1, (double) args.get(1) * multiplier);
            args.set(2, (double) args.get(2) * multiplier);
        }
    }

    @Redirect(method = {"use", "onStoppedUsing"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean hookTridentBoost_use(PlayerEntity instance) {
        ModuleTridentBoost moduleTridentBoost = TarasandeMain.Companion.managerModule().get(ModuleTridentBoost.class);
        if (moduleTridentBoost.getEnabled() && moduleTridentBoost.getAllowOutOfWater().getValue())
            return true;
        return instance.isTouchingWaterOrRain();
    }
}
