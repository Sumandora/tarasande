package net.tarasandedevelopment.tarasande.mixin.mixins.item;

import net.minecraft.item.ArmorMaterials;
import net.tarasandedevelopment.tarasande.mixin.accessor.IArmorMaterials;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ArmorMaterials.class)
public class MixinArmorMaterials implements IArmorMaterials {
    @Shadow
    @Final
    private int durabilityMultiplier;

    @Override
    public int tarasande_getDurabilityMultiplier() {
        return durabilityMultiplier;
    }
}
