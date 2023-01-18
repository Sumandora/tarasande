package net.tarasandedevelopment.tarasande.injection.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.Vec3d;

public interface ILivingEntity {

    Vec3d tarasande_prevServerPos();

    boolean tarasande_forceHasStatusEffect(StatusEffect effect);
    StatusEffectInstance tarasande_forceGetStatusEffect(StatusEffect effect);

    float tarasande_getHeadPitch();
    void tarasande_setHeadPitch(float headPitch);
    float tarasande_getPrevHeadPitch();
    void tarasande_setPrevHeadPitch(float prevHeadPitch);

    float tarasande_getHeadYaw();
    void tarasande_setHeadYaw(float headYaw);
    float tarasande_getPrevHeadYaw();
    void tarasande_setPrevHeadYaw(float prevHeadYaw);
}
