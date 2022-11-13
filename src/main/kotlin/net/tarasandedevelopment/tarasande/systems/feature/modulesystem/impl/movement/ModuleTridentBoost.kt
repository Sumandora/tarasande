package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueBoolean
import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleTridentBoost : Module("Trident boost", "Boosts you when using riptide with a trident", ModuleCategory.MOVEMENT) {

    private val multiplier = ValueNumber(this, "Multiplier", 0.1, 2.0, 1.0, 1E-2)
    private val allowOutOfWater = ValueBoolean(this, "Allow out of water", true)

    fun multiplier() = if (enabled) multiplier.value else 1.0
    fun allowOutOfWater() = if (enabled) allowOutOfWater.value else false
}
