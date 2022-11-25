package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventIsSaddled
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleEntityControl : Module("Entity control", "Makes unsaddled entities controllable", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventIsSaddled::class.java) { event ->
            event.saddled = true
        }
    }

}