package de.florianmichael.tarasande_protocol_hack.injection.mixin.tarasande;

import de.florianmichael.tarasande_protocol_hack.injection.accessor.IModuleSpeed;
import de.florianmichael.tarasande_protocol_hack.tarasande.values.ProtocolHackValues;
import net.tarasandedevelopment.tarasande.system.base.valuesystem.ManagerValue;
import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueBoolean;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleSpeed;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModuleSpeed.class, remap = false)
public class MixinModuleSpeed implements IModuleSpeed {

    @Mutable
    @Final
    @Shadow
    private ValueBoolean lowHop;

    @Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/tarasandedevelopment/tarasande/system/feature/modulesystem/impl/movement/ModuleSpeed;lowHop:Lnet/tarasandedevelopment/tarasande/system/base/valuesystem/impl/ValueBoolean;", shift = At.Shift.AFTER))
    public void addIsEnabled(CallbackInfo ci) {
        ManagerValue.INSTANCE.rem(lowHop);
        lowHop = new ValueBoolean(lowHop.getOwner(), lowHop.getName(), lowHop.getValue(), true, () -> !ProtocolHackValues.INSTANCE.getEmulatePlayerMovement().getValue(), true);
    }

    @Redirect(method = "_init_$lambda$0", at = @At(value = "INVOKE", target = "Lnet/tarasandedevelopment/tarasande/system/base/valuesystem/impl/ValueBoolean;getValue()Z"))
    private static boolean disableIfDisabled(ValueBoolean instance) {
        if(instance == ((IModuleSpeed) (Object) ManagerModule.INSTANCE.get(ModuleSpeed.class)).protocolhack_getLowHop())
            return instance.getValue() && instance.isEnabled().invoke();
        return instance.getValue();
    }

    @Override
    public ValueBoolean protocolhack_getLowHop() {
        return lowHop;
    }
}
