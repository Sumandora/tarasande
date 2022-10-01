package de.florianmichael.tarasande.mixin.mixins;

import de.florianmichael.tarasande.event.EventBindingEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "hasBindingCurse", at = @At("RETURN"), cancellable = true)
    private static void injectHasBindingCurse(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        final EventBindingEnchantment eventBindingEnchantment = new EventBindingEnchantment(cir.getReturnValue());
        TarasandeMain.Companion.get().getManagerEvent().call(eventBindingEnchantment);
        cir.setReturnValue(eventBindingEnchantment.getPresent());
    }
}
