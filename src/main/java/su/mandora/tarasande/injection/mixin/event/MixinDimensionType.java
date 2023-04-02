package su.mandora.tarasande.injection.mixin.event;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.render.ModuleWorldTime;

@Mixin(DimensionType.class)
public class MixinDimensionType {

    @Inject(method = "getMoonPhase", at = @At("HEAD"), cancellable = true)
    public void hookWorldTime(long time, CallbackInfoReturnable<Integer> cir) {
        final Integer moonPhase = ManagerModule.INSTANCE.get(ModuleWorldTime.class).moonPhase();
        if (moonPhase != null) {
            cir.setReturnValue(moonPhase);
        }
    }

}
