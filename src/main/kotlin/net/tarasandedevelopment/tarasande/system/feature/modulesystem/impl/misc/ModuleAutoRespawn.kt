package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.misc

import net.tarasandedevelopment.tarasande.event.impl.EventShowsDeathScreen
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAutoRespawn : Module("Auto respawn", "Instantaneously respawns when dying", ModuleCategory.MISC) {

    init {
        registerEvent(EventShowsDeathScreen::class.java) { event ->
            event.showsDeathScreen = false
        }
    }

}