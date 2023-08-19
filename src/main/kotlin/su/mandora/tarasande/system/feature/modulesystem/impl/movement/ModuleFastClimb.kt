package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFastClimb : Module("Fast climb", "Speeds up climbing movement", ModuleCategory.MOVEMENT) {
    val maxHorizontalVelocityMultiplier = ValueNumber(this, "Max horizontal velocity multiplier", 0.0, 1.0, 3.0, 0.1)
    val ascendMultiplier = ValueNumber(this, "Ascend multiplier", 0.0, 1.0, 3.0, 0.1)
    val descendMultiplier = ValueNumber(this, "Descend multiplier", 0.0, 1.0, 3.0, 0.1)
}