package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.viaversion;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.protocols.protocol1_11_1to1_11.packets.InventoryPackets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryPackets.class)
public class MixinInventoryPackets1_11 {

    @Inject(method = "handleItemToServer", at = @At("HEAD"), cancellable = true, remap = false)
    public void fixBug(Item item, CallbackInfoReturnable<Item> cir) {
        cir.setReturnValue(item);
    }
}
