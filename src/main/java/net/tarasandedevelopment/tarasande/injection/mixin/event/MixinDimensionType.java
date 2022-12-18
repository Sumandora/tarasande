package net.tarasandedevelopment.tarasande.injection.mixin.event;

import net.minecraft.world.dimension.DimensionType;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleWorldTime;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionType.class)
public class MixinDimensionType {

    @Inject(method = "getMoonPhase", at = @At("HEAD"), cancellable = true)
    public void hookWorldTime(long time, CallbackInfoReturnable<Integer> cir) {
        final Integer moonPhase = TarasandeMain.Companion.managerModule().get(ModuleWorldTime.class).moonPhase();
        if (moonPhase != null) {
            cir.setReturnValue(moonPhase);
        }
    }

}
