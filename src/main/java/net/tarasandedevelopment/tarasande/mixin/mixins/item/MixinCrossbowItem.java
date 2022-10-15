package net.tarasandedevelopment.tarasande.mixin.mixins.item;

import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.tarasandedevelopment.tarasande.mixin.accessor.ICrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class MixinCrossbowItem implements ICrossbowItem {

    @Shadow
    private static float getSpeed(ItemStack stack) {
        return 0;
    }

    @Shadow
    private static List<ItemStack> getProjectiles(ItemStack crossbow) {
        return null;
    }

    @Override
    public float tarasande_invokeGetSpeed(ItemStack stack) {
        return getSpeed(stack);
    }

    @Override
    public List<ItemStack> tarasande_invokeGetProjectiles(ItemStack stack) {
        return getProjectiles(stack);
    }
}
