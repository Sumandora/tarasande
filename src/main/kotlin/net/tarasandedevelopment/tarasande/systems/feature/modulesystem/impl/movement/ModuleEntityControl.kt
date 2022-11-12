package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.movement

import net.tarasandedevelopment.events.impl.EventIsSaddled
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleEntityControl : Module("Entity control", "Makes unsaddled entities controllable", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventIsSaddled::class.java) { event ->
            event.saddled = true
        }
    }

}