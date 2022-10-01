package de.florianmichael.tarasande.mixin.mixins.protocolhack;

import de.florianmichael.viaprotocolhack.util.VersionList;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemCooldownManager.class)
public class MixinItemCooldownManager {

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void injectSet(Item item, int duration, CallbackInfo ci) {
        if (VersionList.isOlderOrEqualTo(VersionList.R1_8))
            ci.cancel();
    }
}
