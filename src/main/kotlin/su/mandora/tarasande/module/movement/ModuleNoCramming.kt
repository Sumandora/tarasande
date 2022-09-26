package su.mandora.tarasande.module.movement

import su.mandora.tarasande.base.module.Module
import su.mandora.tarasande.base.module.ModuleCategory
import su.mandora.tarasande.value.ValueMode

class ModuleNoCramming : Module("No cramming", "Prevents entity cramming", ModuleCategory.MOVEMENT) {

    val mode = ValueMode(this, "Mode", false, "Force enable", "Force disable")

}