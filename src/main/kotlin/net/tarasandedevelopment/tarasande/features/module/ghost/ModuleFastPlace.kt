package net.tarasandedevelopment.tarasande.features.module.ghost

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventTick

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    // TODO | Clamp
    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE)
                mc.itemUseCooldown = 0
        }
    }
}
