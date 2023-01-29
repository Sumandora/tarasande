package net.tarasandedevelopment.tarasande.util.extension.minecraft

import net.minecraft.entity.LivingEntity
import net.tarasandedevelopment.tarasande.mc

fun LivingEntity.smoothedHurtTime(): Float {
    return if(maxHurtTime > 0) (hurtTime - mc.tickDelta).coerceAtLeast(0.0F) / maxHurtTime else 0.0f
}