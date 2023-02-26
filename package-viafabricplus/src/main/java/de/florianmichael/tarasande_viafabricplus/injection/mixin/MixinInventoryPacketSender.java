package de.florianmichael.tarasande_viafabricplus.injection.mixin;

import de.florianmichael.tarasande_viafabricplus.TarasandeViaFabricPlus;
import de.florianmichael.viafabricplus.definition.v1_8_x.InventoryPacketSender;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement.ModuleInventoryMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryPacketSender.class, remap = false)
public class MixinInventoryPacketSender {

    @Inject(method = "sendOpenInventoryAchievement", at = @At("HEAD"), cancellable = true)
    private static void hookInventoryMove(CallbackInfo ci) {
        if (ManagerModule.INSTANCE.get(ModuleInventoryMove.class).getEnabled().getValue() && TarasandeViaFabricPlus.cancelOpenPacket.getValue()) {
            ci.cancel();
        }
    }
}
