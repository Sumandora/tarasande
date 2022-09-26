package su.mandora.tarasande.mixin.mixins;

import de.florianmichael.tarasande.module.exploit.ModuleAntiBindingCurse;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.TarasandeMain;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "hasBindingCurse", at = @At("HEAD"), cancellable = true)
    private static void hookHack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (TarasandeMain.Companion.get().getManagerModule().get(ModuleAntiBindingCurse.class).getEnabled())
            cir.setReturnValue(false);
    }
}
