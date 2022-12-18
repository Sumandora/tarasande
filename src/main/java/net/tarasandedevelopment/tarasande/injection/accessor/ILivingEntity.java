package net.tarasandedevelopment.tarasande.injection.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;

public interface ILivingEntity {

    Vec3d tarasande_prevServerPos();

    boolean tarasande_forceHasStatusEffect(StatusEffect effect);

    StatusEffectInstance tarasande_forceGetStatusEffect(StatusEffect effect);
}
