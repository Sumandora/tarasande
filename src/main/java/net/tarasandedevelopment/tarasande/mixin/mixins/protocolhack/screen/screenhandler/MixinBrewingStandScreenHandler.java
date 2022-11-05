package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.screen.screenhandler;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.screen.BrewingStandScreenHandler$FuelSlot")
public class MixinBrewingStandScreenHandler extends Slot {

    public MixinBrewingStandScreenHandler(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "matches(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void removeFuelSlot(CallbackInfoReturnable<Boolean> ci) {
        if (VersionList.isOlderOrEqualTo(ProtocolVersion.v1_8))
            ci.setReturnValue(false);
    }

    @Override
    public boolean isEnabled() {
        return VersionList.isNewerTo(ProtocolVersion.v1_8);
    }
}
