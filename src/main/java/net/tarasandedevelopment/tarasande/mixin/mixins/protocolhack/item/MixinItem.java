package net.tarasandedevelopment.tarasande.mixin.mixins.protocolhack.item;

import net.minecraft.item.Item;
import net.tarasandedevelopment.tarasande.mixin.accessor.protocolhack.IItem_Protocol;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(Item.class)
public class MixinItem implements IItem_Protocol {
    @Shadow @Final protected static UUID ATTACK_DAMAGE_MODIFIER_ID;

    @Override
    public UUID tarasande_getAttackDamageModifierId() {
        return ATTACK_DAMAGE_MODIFIER_ID;
    }
}
