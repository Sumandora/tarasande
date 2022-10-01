package net.tarasandedevelopment.tarasande.mixin.mixins;

import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.event.EventItemCooldown;

@Mixin(ItemCooldownManager.class)
public class MixinItemCooldownManager {

    @Inject(method = "getCooldownProgress", at = @At("RETURN"), cancellable = true)
    public void injectGetCooldownProgress(Item item, float partialTicks, CallbackInfoReturnable<Float> cir) {
        EventItemCooldown eventItemCooldown = new EventItemCooldown(item, cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventItemCooldown);
        cir.setReturnValue(eventItemCooldown.getCooldown());
    }

}
