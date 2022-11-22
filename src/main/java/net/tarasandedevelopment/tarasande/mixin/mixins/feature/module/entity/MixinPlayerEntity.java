package net.tarasandedevelopment.tarasande.mixin.mixins.feature.module.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement.ModuleSafeWalk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity {

    @Inject(method = "clipAtLedge", at = @At("HEAD"), cancellable = true)
    public void hookSafeWalk(CallbackInfoReturnable<Boolean> cir) {
        ModuleSafeWalk moduleSafeWalk = TarasandeMain.Companion.managerModule().get(ModuleSafeWalk.class);
        if (moduleSafeWalk.getEnabled() && !moduleSafeWalk.getSneak().getValue())
            cir.setReturnValue(true);
    }
}
