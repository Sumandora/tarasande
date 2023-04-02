package su.mandora.tarasande.injection.mixin.feature.module.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleTridentBoost;

@Mixin(TridentItem.class)
public class MixinTridentItem {

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void hookTridentBoost(Args args) {
        ModuleTridentBoost moduleTridentBoost = ManagerModule.INSTANCE.get(ModuleTridentBoost.class);
        if (moduleTridentBoost.getEnabled().getValue()) {
            double multiplier = moduleTridentBoost.getMultiplier().getValue();

            args.set(0, (double) args.get(0) * multiplier);
            args.set(1, (double) args.get(1) * multiplier);
            args.set(2, (double) args.get(2) * multiplier);
        }
    }

    @Redirect(method = {"use", "onStoppedUsing"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean hookTridentBoost_use(PlayerEntity instance) {
        ModuleTridentBoost moduleTridentBoost = ManagerModule.INSTANCE.get(ModuleTridentBoost.class);
        if (moduleTridentBoost.getEnabled().getValue() && moduleTridentBoost.getAllowOutOfWater().getValue())
            return true;
        return instance.isTouchingWaterOrRain();
    }
}
