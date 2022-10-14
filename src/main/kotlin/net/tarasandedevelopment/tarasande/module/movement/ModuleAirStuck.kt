package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventUpdate

class ModuleAirStuck : Module("Air stuck", "Freezes all movement", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.PRE)
                event.cancelled = true
        }
    }

}