package de.enzaxd.viaforge.injection.mixin;

import de.enzaxd.viaforge.equals.ProtocolEquals;
import de.enzaxd.viaforge.equals.VersionList;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldownManager.class)
public class ItemCooldownManagerMixin {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void injectSet(Item item, int duration, CallbackInfo ci) {
        if (ProtocolEquals.isOlderOrEqualTo(VersionList.R1_8))
            ci.cancel();
    }
}
