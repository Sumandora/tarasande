package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.TarasandeViaFabricPlus;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import de.florianmichael.viafabricplus.settings.type_impl.ProtocolSyncBooleanSetting;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ProtocolSyncBooleanSetting.class, remap = false)
public abstract class MixinProtocolSyncBooleanSetting {

    @Inject(method = "getValue()Ljava/lang/Boolean;", at = @At("HEAD"), cancellable = true)
    public void addInventoryHook(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == DebugSettings.INSTANCE.sendOpenInventoryPacket) {
            if (ManagerModule.INSTANCE.get(ModuleInventoryMove.class).getEnabled().getValue() && TarasandeViaFabricPlus.cancelOpenPacket.getValue()) {
                cir.setReturnValue(false);
            }
        }
    }
}
