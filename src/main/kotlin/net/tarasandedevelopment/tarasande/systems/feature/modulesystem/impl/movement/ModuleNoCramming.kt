package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.systems.base.valuesystem.impl.ValueMode
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleNoCramming : Module("No cramming", "Prevents entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

}