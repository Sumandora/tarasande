package su.mandora.tarasande.injection.mixin.feature.module.entity.entitycontrol;

import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleEntityControl;

@Mixin(AbstractHorseEntity.class)
public class MixinAbstractHorseEntity {

    @Inject(method = "isSaddled", at = @At("RETURN"), cancellable = true)
    public void hookEntityControl(CallbackInfoReturnable<Boolean> cir) {
        ModuleEntityControl moduleEntityControl = ManagerModule.INSTANCE.get(ModuleEntityControl.class);
        if(moduleEntityControl.getEnabled().getValue())
            cir.setReturnValue(true);
    }
}
