package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueBoolean
import net.tarasandedevelopment.tarasande.value.ValueNumber

class ModuleTridentBoost : Module("Trident boost", "Boosts you when using riptide with a trident", ModuleCategory.MOVEMENT) {

    private val multiplier = ValueNumber(this, "Multiplier", 0.1, 2.0, 1.0, 1E-2)
    private val allowOutOfWater = ValueBoolean(this, "Allow out of water", true)

    fun multiplier() = if (enabled) multiplier.value else 1.0
    fun allowOutOfWater() = if (enabled) allowOutOfWater.value else false
}
