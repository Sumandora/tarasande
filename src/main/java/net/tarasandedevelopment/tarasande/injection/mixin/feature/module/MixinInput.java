package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.input.Input;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSprint;
import net.tarasandedevelopment.tarasande.util.player.PlayerUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Input.class)
public class MixinInput {

    @Inject(method = "hasForwardMovement", at = @At("HEAD"), cancellable = true)
    public void hookSprint(CallbackInfoReturnable<Boolean> cir) {
        ModuleSprint moduleSprint = TarasandeMain.Companion.managerModule().get(ModuleSprint.class);
        if (moduleSprint.getEnabled() && moduleSprint.getAllowBackwards().getValue())
            if(PlayerUtil.INSTANCE.isPlayerMoving())
                cir.setReturnValue(true);
    }

}
