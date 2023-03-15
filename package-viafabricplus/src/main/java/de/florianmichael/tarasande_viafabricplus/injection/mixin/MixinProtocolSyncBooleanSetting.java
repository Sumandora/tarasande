package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.TarasandeViaFabricPlus;
import de.florianmichael.viafabricplus.settings.base.AbstractSetting;
import de.florianmichael.viafabricplus.settings.base.SettingGroup;
import de.florianmichael.viafabricplus.settings.groups.DebugSettings;
import de.florianmichael.viafabricplus.settings.type_impl.ProtocolSyncBooleanSetting;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ProtocolSyncBooleanSetting.class, remap = false)
public abstract class MixinProtocolSyncBooleanSetting extends AbstractSetting<Boolean> {

    public MixinProtocolSyncBooleanSetting(SettingGroup parent, String name, Boolean defaultValue) {
        super(parent, name, defaultValue);
    }

    @Inject(method = "getValue()Ljava/lang/Boolean;", at = @At("HEAD"), cancellable = true)
    public void addInventoryHook(CallbackInfoReturnable<Boolean> cir) {
        if (getName().equals(DebugSettings.INSTANCE.sendOpenInventoryPacket.getName())) {
            if (ManagerModule.INSTANCE.get(ModuleInventoryMove.class).getEnabled().getValue() && TarasandeViaFabricPlus.cancelOpenPacket.getValue()) {
                cir.setReturnValue(false);
            }
        }
    }
}
