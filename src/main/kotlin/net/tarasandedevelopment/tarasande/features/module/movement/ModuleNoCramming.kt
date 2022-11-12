package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.impl.ValueMode

class ModuleNoCramming : Module("No cramming", "Prevents entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

}