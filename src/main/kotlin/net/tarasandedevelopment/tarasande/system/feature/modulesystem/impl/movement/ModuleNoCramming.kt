package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.system.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoCramming : Module("No cramming", "Force enables/disables entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

    init {
        mode.select(1) // "No cramming" sounds like it would disable it by default...
    }

}