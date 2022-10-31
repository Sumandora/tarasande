package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate

class ModuleAirStuck : Module("Air stuck", "Freezes all movement", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                event.cancelled = true
        }
    }

}