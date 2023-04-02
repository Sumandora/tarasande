package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAirStuck : Module("Air stuck", "Freezes all movement", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                event.cancelled = true
        }
    }

}