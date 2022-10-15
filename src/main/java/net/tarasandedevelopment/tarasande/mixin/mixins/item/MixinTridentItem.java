package net.tarasandedevelopment.tarasande.mixin.mixins.item;

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
    private boolean waterCheck() {
        return TarasandeMain.Companion.get().getManagerModule().get(ModuleTridentBoost.class).allowOutOfWater();
    }

    @ModifyArgs(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addVelocity(DDD)V"))
    public void modifyPlayerVelocity(Args args) {
        final double multiplier = TarasandeMain.Companion.get().getManagerModule().get(ModuleTridentBoost.class).multiplier();

        args.set(0, (double) args.get(0) + multiplier);
        args.set(1, (double) args.get(1) + multiplier);
        args.set(2, (double) args.get(2) + multiplier);
    }

    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean modifyPlayerIsTouchingWater(PlayerEntity instance) {
        return waterCheck() || instance.isTouchingWaterOrRain();
    }

    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z"))
    public boolean modifyPlayerIsTouchingWaterTwo(PlayerEntity instance) {
        return waterCheck() || instance.isTouchingWaterOrRain();
    }
}
