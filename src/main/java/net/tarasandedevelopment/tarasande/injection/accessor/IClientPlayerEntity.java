package net.tarasandedevelopment.tarasande.injection.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

public interface IClientPlayerEntity {

    boolean tarasande_getBypassChat();

    void tarasande_setBypassChat(boolean bypassChat);

    boolean tarasande_forceHasStatusEffect(StatusEffect effect);

    StatusEffectInstance tarasande_forceGetStatusEffect(StatusEffect effect);

}
