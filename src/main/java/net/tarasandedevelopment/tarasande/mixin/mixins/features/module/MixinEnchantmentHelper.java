package net.tarasandedevelopment.tarasande.mixin.mixins.features.module;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.tarasandedevelopment.tarasande.TarasandeMain;
import net.tarasandedevelopment.tarasande.module.exploit.ModuleAntiBindingCurse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "hasBindingCurse", at = @At("RETURN"), cancellable = true)
    private static void hookAntiBindingCurse(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!TarasandeMain.Companion.get().getDisabled())
            if (TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiBindingCurse.class).getEnabled())
                cir.setReturnValue(false);
    }
}
