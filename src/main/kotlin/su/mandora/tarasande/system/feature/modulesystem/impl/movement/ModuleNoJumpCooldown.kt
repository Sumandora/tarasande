package su.mandora.tarasande.system.feature.modulesystem.impl.movement

import su.mandora.tarasande.event.impl.EventUpdate
import su.mandora.tarasande.mc
import su.mandora.tarasande.system.feature.modulesystem.Module
import su.mandora.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoJumpCooldown : Module("No jump cooldown", "Removes minecraft's jump cooldown", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST)
                mc.player?.jumpingCooldown = 0
        }
    }
}
