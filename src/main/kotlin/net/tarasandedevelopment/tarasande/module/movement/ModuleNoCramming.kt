package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.value.ValueMode

class ModuleNoCramming : Module("No cramming", "Prevents entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

}