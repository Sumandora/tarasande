package su.mandora.tarasande.util.extension.minecraft

import net.minecraft.entity.LivingEntity
import su.mandora.tarasande.mc

fun LivingEntity.smoothedHurtTime(): Float {
    return if (maxHurtTime > 0) (hurtTime - mc.tickDelta).coerceAtLeast(0F) / maxHurtTime else 0F
}