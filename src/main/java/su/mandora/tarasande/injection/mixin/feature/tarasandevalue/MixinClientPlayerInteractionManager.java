package su.mandora.tarasande.injection.mixin.feature.tarasandevalue;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.feature.tarasandevalue.impl.DebugValues;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {

    @Inject(method = "hasLimitedAttackSpeed", at = @At("HEAD"), cancellable = true)
    public void removeDelay(CallbackInfoReturnable<Boolean> cir) {
        if (DebugValues.INSTANCE.getEliminateHitDelay().getValue())
            cir.setReturnValue(false); // rofl kartoffel
    }

    @Inject(method = "hasCreativeInventory", at = @At("RETURN"), cancellable = true)
    public void forceCreativeInventory(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(cir.getReturnValue() || DebugValues.INSTANCE.getForceCreativeInventory().getValue());
    }

}
