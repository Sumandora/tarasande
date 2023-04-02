package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.client.input.Input;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.movement.ModuleSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Input.class)
public class MixinInput {

    @Redirect(method = "hasForwardMovement", at = @At(value = "FIELD", target = "Lnet/minecraft/client/input/Input;movementForward:F"))
    public float hookSprint(Input instance) {
        ModuleSprint moduleSprint = ManagerModule.INSTANCE.get(ModuleSprint.class);
        if (moduleSprint.getEnabled().getValue() && moduleSprint.getAllowBackwards().isEnabled().invoke() && moduleSprint.getAllowBackwards().getValue())
            return instance.getMovementInput().length();
        return instance.movementForward;
    }

}
