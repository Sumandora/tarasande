package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.ghost

import net.tarasandedevelopment.tarasande.event.EventTick
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    // TODO | Clamp
    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE)
                mc.itemUseCooldown = 0
        }
    }
}
