package su.mandora.tarasande.mixin.mixins;

import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import su.mandora.tarasande.mixin.accessor.ICrossbowItem;

@Mixin(CrossbowItem.class)
public abstract class MixinCrossbowItem implements ICrossbowItem {

    @Shadow
    private static float getSpeed(ItemStack stack) {
        return 0;
    }

    @Override
    public float invokeGetSpeed(ItemStack stack) {
        return getSpeed(stack);
    }
}
