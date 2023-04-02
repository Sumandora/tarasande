package su.mandora.tarasande.injection.mixin.feature.module;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import su.mandora.tarasande.system.feature.modulesystem.ManagerModule;
import su.mandora.tarasande.system.feature.modulesystem.impl.exploit.ModuleAntiBindingCurse;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

    @Inject(method = "hasBindingCurse", at = @At("RETURN"), cancellable = true)
    private static void hookAntiBindingCurse(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (ManagerModule.INSTANCE.get(ModuleAntiBindingCurse.class).getEnabled().getValue()) {
            cir.setReturnValue(false);
        }
    }
}
