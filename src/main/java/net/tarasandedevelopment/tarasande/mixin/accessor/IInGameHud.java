package net.tarasandedevelopment.tarasande.mixin.accessor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IInGameHud {
    void tarasande_invokeRenderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);
}
