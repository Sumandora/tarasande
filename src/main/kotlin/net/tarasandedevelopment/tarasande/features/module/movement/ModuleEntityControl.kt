package net.tarasandedevelopment.tarasande.features.module.movement

import net.tarasandedevelopment.tarasande.base.features.module.Module
import net.tarasandedevelopment.tarasande.base.features.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventIsSaddled

class ModuleEntityControl : Module("Entity control", "Makes unsaddled entities controllable", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventIsSaddled::class.java) { event ->
            event.saddled = true
        }
    }

}