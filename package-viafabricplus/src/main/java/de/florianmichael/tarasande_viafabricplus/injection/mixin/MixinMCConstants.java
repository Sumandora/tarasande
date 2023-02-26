package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.viafabricplus.definition.MCConstants;
import net.minecraft.block.Blocks;
import net.tarasandedevelopment.tarasande.event.EventDispatcher;
import net.tarasandedevelopment.tarasande.event.impl.EventVelocityMultiplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MCConstants.class, remap = false)
public class MixinMCConstants {

    @Inject(method = "getSoulSandMultiplier", at = @At("HEAD"), cancellable = true)
    private static void changeMultiplier(CallbackInfoReturnable<Double> cir) {
        EventVelocityMultiplier eventVelocityMultiplier = new EventVelocityMultiplier(Blocks.SOUL_SAND, 0.4D);
        EventDispatcher.INSTANCE.call(eventVelocityMultiplier);

        if (eventVelocityMultiplier.getDirty()) {
            cir.setReturnValue(eventVelocityMultiplier.getVelocityMultiplier());
        }
    }
}
