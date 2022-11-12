package net.tarasandedevelopment.tarasande.systems.feature.modulesystem.impl.misc

import net.tarasandedevelopment.events.impl.EventShowsDeathScreen
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.systems.feature.modulesystem.ModuleCategory

class ModuleAutoRespawn : Module("Auto respawn", "Instantaneously respawns when dying", ModuleCategory.MISC) {

    init {
        registerEvent(EventShowsDeathScreen::class.java) { event ->
            event.showsDeathScreen = false
        }
    }

}