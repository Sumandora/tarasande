package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICrossbowItem {

    float tarasande_invokeGetSpeed(ItemStack stack);

    List<ItemStack> tarasande_invokeGetProjectiles(ItemStack stack);

}
