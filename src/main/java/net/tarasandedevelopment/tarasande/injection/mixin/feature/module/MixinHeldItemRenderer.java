package net.tarasandedevelopment.tarasande.injection.mixin.feature.module;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ManagerModule;
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.render.ModuleNoSwing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    public boolean hookNoSwing(ItemStack left, ItemStack right) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            if (left.isEmpty() && right.isEmpty()) {
                return true;
            }
            if (left.isEmpty() || right.isEmpty()) {
                return false;
            }
            return left.isOf(right.getItem()); // Ignore count and nbt
        }
        return ItemStack.areEqual(left, right);
    }

    @Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getAttackCooldownProgress(F)F"))
    public float hookNoSwing(ClientPlayerEntity instance, float v) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            return 1.0F;
        }
        return instance.getAttackCooldownProgress(v);
    }

    @Inject(method = "resetEquipProgress", at = @At("HEAD"), cancellable = true)
    public void hookNoSwing(Hand hand, CallbackInfo ci) {
        ModuleNoSwing moduleNoSwing = ManagerModule.INSTANCE.get(ModuleNoSwing.class);
        if (moduleNoSwing.getEnabled().getValue() && moduleNoSwing.getDisableEquipProgress().getValue()) {
            ci.cancel();
        }
    }
}
