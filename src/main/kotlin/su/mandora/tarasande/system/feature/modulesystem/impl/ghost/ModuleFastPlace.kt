package su.mandora.tarasande.system.feature.modulesystem.impl.ghost

import su.mandora.tarasande.event.impl.EventTick
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.base.valuesystem.impl.ValueNumber
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleFastPlace : Module("Fast place", "Speeds up block placements", ModuleCategory.GHOST) {

    private val maximumDelay = ValueNumber(this, "Maximum delay", 0.0, 0.0, 10.0, 1.0)

    init {
        registerEvent(EventTick::class.java) { event ->
            if (event.state == EventTick.State.PRE)
                mc.itemUseCooldown = mc.itemUseCooldown.coerceAtMost(maximumDelay.value.toInt())
        }
    }
}
