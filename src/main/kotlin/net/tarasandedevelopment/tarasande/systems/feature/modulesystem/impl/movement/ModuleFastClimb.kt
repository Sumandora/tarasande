package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleFastClimb : Module("Fast climb", "Speeds up climbing movement", ModuleCategory.MOVEMENT) {
    val multiplier = ValueNumber(this, "Multiplier", 0.5, 1.0, 3.0, 0.1)
}