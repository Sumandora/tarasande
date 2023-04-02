package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.system.base.valuesystem.impl.ValueBoolean
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleTridentBoost : Module("Trident boost", "Boosts you when using riptide with a trident", ModuleCategory.MOVEMENT) {

    val multiplier = ValueNumber(this, "Multiplier", 0.1, 2.0, 1.0, 1E-2)
    val allowOutOfWater = ValueBoolean(this, "Allow out of water", true)

}
