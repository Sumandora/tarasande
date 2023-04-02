package su.mandora.tarasande.system.feature.modulesystem.impl.misc

import su.mandora.tarasande.event.impl.EventShowsDeathScreen
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleAutoRespawn : Module("Auto respawn", "Instantaneously respawns when dying", ModuleCategory.MISC) {

    init {
        registerEvent(EventShowsDeathScreen::class.java) { event ->
            event.showsDeathScreen = false
        }
    }

}