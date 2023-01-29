package net.tarasandedevelopment.tarasande.injection.mixin.feature.clientvalue;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.tarasandedevelopment.tarasande.feature.clientvalue.impl.DebugValues;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    public void removeDelay(CallbackInfoReturnable<Boolean> cir) {
        if(DebugValues.INSTANCE.getEliminateHitDelay().getValue())
            cir.setReturnValue(false); // rofl kartoffel
    }

}
