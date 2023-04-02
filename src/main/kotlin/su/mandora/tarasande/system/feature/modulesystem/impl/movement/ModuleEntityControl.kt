package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventIsSaddled
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleEntityControl : Module("Entity control", "Makes unsaddled entities controllable", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventIsSaddled::class.java) { event ->
            event.saddled = true
        }
    }

}