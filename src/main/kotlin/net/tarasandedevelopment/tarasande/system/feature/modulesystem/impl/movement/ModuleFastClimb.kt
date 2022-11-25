package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueNumber
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFastClimb : Module("Fast climb", "Speeds up climbing movement", ModuleCategory.MOVEMENT) {
    val multiplier = ValueNumber(this, "Multiplier", 0.5, 1.0, 3.0, 0.1)
}