package net.tarasandedevelopment.tarasande.module.misc

import net.tarasandedevelopment.tarasande.base.module.Module
import net.tarasandedevelopment.tarasande.base.module.ModuleCategory
import net.tarasandedevelopment.tarasande.event.EventRespawn

class ModuleAutoRespawn : Module("Auto respawn", "Instantaneously respawns when dying", ModuleCategory.MISC) {

    init {
        registerEvent(EventRespawn::class.java) { event ->
            event.showDeathScreen = false
        }
    }

}