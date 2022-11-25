package net.tarasandedevelopment.tarasande.system.feature.modulesystem.impl.movement

import net.tarasandedevelopment.tarasande.event.EventUpdate
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.Module
import net.tarasandedevelopment.tarasande.system.feature.modulesystem.ModuleCategory

class ModuleNoJumpCooldown : Module("No jump cooldown", "Removes minecraft's jump cooldown", ModuleCategory.MOVEMENT) {

    init {
        registerEvent(EventUpdate::class.java) { event ->
            if (event.state == EventUpdate.State.POST)
                mc.player?.jumpingCooldown = 0
        }
    }
}
