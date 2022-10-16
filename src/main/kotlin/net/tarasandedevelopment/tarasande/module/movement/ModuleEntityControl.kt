package net.tarasandedevelopment.tarasande.module.movement

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventIsSaddled

class ModuleEntityControl : Module("Entity control", "Makes unsaddled entities controllable", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventIsSaddled::class.java) { event ->
            event.saddled = true
        }
    }

}